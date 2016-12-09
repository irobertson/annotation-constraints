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
import org.annotationconstraints.TargetMustBeAnnotatedWith;
import org.annotationconstraints.testutils.MemorySource;;

public class RequireAnnotationsTest extends AbstractCompilationTest {
    private static final MemorySource REQUIRE_RESOURCE = annotationSource(
        "RequireResource", TargetMustBeAnnotatedWith.class, classRef(Resource.class));

    private static final MemorySource REQUIRE_MULTIPLE = annotationSource(
        "RequireMultiple",
        TargetMustBeAnnotatedWith.class,
        classRefs(Resource.class, Generated.class));

    @Test
    public void testRequireOneFail() throws ClassNotFoundException, Exception {
        MemorySource failSource = new MemorySource(
            "Fail",
            "@RequireResource",
            "public class Fail {}");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_RESOURCE,
            failSource);

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.ERROR,
                failSource,
                "",
                "Fail is annotated with @RequireResource but not with @Resource")));
    }

    @Test
    public void testRequireOnePass() throws Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_RESOURCE,
            new MemorySource(
                "Pass",
                "@RequireResource",
                "@" + Resource.class.getName(),
                "public class Pass {}"));
        assertThat(diagnostics, Matchers.empty());
    }

    @Test
    public void testRequireMultipleFailOne() throws Exception {
        MemorySource failSource = new MemorySource(
            "Fail",
            "@RequireMultiple",
            "@" + Resource.class.getName(),
            "public class Fail {}");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_MULTIPLE,
            failSource);

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.ERROR,
                failSource,
                "",
                "Fail is annotated with @RequireMultiple but not with @Generated")));
    }

    @Test
    public void testRequireMultipleFailAll() throws Exception {
        MemorySource failSource = new MemorySource(
            "Fail",
            "@RequireMultiple",
            "public class Fail {}");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_MULTIPLE,
            failSource);

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.ERROR,
                failSource,
                "",
                "Fail is annotated with @RequireMultiple but not with @Resource and @Generated")));
    }

    @Test
    public void testRequireMultiplePass() throws Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_MULTIPLE,
            new MemorySource("Pass",
                "@RequireMultiple",
                "@" + Resource.class.getName(),
                "@" + Generated.class.getName() + "({})",
                "public class Pass {}"));
        assertThat(diagnostics, Matchers.empty());

    }
}

