package org.annotationconstraints.processor;
import static org.annotationconstraints.testutils.MemorySource.annotationSource;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.annotation.Generated;
import javax.annotation.Resource;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.annotationconstraints.TargetMustHaveASupertypeAnnotatedWith;
import org.annotationconstraints.testutils.MemorySource;;

public class RequireAnnotationsOnSuperTypeTest extends AbstractCompilationTest {
    private static final String GENERATED_ANNOTATION = "@" + Generated.class.getName() + "({})";

    private static final String RESOURCE_ANNOTATION = "@" + Resource.class.getName();

    private static final MemorySource REQUIRE_RESOURCE_ON_SUPERTYPE = annotationSource(
        "RequireResourceOnSupertype", TargetMustHaveASupertypeAnnotatedWith.class, classRef(Resource.class));

    private static final MemorySource REQUIRE_MULTIPLE_ON_SUPERTYPE = annotationSource(
        "RequireMultipleOnSupertype",
        TargetMustHaveASupertypeAnnotatedWith.class,
        classRefs(Resource.class, Generated.class));

    private static final MemorySource CHILD = new MemorySource(
        "Child",
        "@RequireResourceOnSupertype",
        "public class Child extends Parent {}");

    private static final MemorySource CHILD_MULTIPLE = new MemorySource(
        "Child",
        "@RequireMultipleOnSupertype",
        "public class Child extends Parent {}");

    @Test
    public void testRequireOneFail() throws ClassNotFoundException, Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_RESOURCE_ON_SUPERTYPE, CHILD, new MemorySource("Parent", "public class Parent {}"));

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.ERROR,
                CHILD,
                "",
                "Child is annotated with @RequireResourceOnSupertype but does not have a supertype annotated with @Resource")));
    }

    @Test
    public void testRequireOnePass() throws Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_RESOURCE_ON_SUPERTYPE,
            CHILD,
            new MemorySource("Parent", RESOURCE_ANNOTATION, "public class Parent {}"));
        assertThat(diagnostics, Matchers.empty());
    }

    @Test
    public void testRequireOneIndirectPass() throws Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_RESOURCE_ON_SUPERTYPE,
            new MemorySource("Parent", RESOURCE_ANNOTATION, "public class Parent {}"),
            new MemorySource("Child", "public class Child extends Parent {}"),
            new MemorySource("Grandchild", "@RequireResourceOnSupertype", "public class Grandchild extends Child {}"));

        assertThat(diagnostics, Matchers.empty());
    }

    @Test
    public void testRequireOneSelfPass() throws Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_RESOURCE_ON_SUPERTYPE,
            new MemorySource("Pass", RESOURCE_ANNOTATION, "@RequireResourceOnSupertype", "public class Pass {}"));

        assertThat(diagnostics, Matchers.empty());
    }

    @Test
    public void testRequireMultipleFailOne() throws Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_MULTIPLE_ON_SUPERTYPE,
            CHILD_MULTIPLE,
            new MemorySource("Parent", GENERATED_ANNOTATION, "public class Parent {}"));

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.ERROR,
                CHILD_MULTIPLE,
                "",
                "Child is annotated with @RequireMultipleOnSupertype but does not have a supertype annotated with @Resource")));
    }

    @Test
    public void testRequireMultipleFailAll() throws Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_MULTIPLE_ON_SUPERTYPE,
            CHILD_MULTIPLE,
            new MemorySource("Parent", "public class Parent {}"));

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.ERROR,
                CHILD_MULTIPLE,
                "",
                "Child is annotated with @RequireMultipleOnSupertype but does not have a supertype annotated with @Resource and @Generated")));
    }

    @Test
    public void testRequireMultiplePass() throws Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_MULTIPLE_ON_SUPERTYPE,
            CHILD_MULTIPLE,
            new MemorySource("Parent", RESOURCE_ANNOTATION, GENERATED_ANNOTATION, "public class Parent {}"));

        assertThat(diagnostics, Matchers.empty());
    }

}

