package org.annotationconstraints.processor;
import static org.annotationconstraints.processor.MemorySource.annotationSource;
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

import org.annotationconstraints.TargetMustNotBeAnnotatedWith;;

public class DisallowAnnotationsTest extends AbstractCompilationTest {
    private static final MemorySource DISALLOW_RESOURCE = annotationSource(
        "DisallowResource", TargetMustNotBeAnnotatedWith.class, classRef(Resource.class));

    private static final MemorySource DISALLOW_MULTIPLE = annotationSource(
        "DisallowMultiple",
        TargetMustNotBeAnnotatedWith.class,
        classRefs(Resource.class, Generated.class));

    @Test
    public void testDisallowOneFail() throws ClassNotFoundException, Exception {
        MemorySource failSource = new MemorySource(
            "Fail",
            "@DisallowResource",
            "@" + Resource.class.getName(),
            "public class Fail {}");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            DISALLOW_RESOURCE,
            failSource);

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.ERROR,
                failSource,
                "",
                "Fail is annotated with @DisallowResource which is not allowed with @Resource")));
    }

    @Test
    public void testDisallowOnePass() throws Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            DISALLOW_RESOURCE,
            new MemorySource("Pass", "@DisallowResource public class Pass {}"));
        assertThat(diagnostics, Matchers.empty());
    }

    @Test
    public void testDisallowMultipleFailOne() throws Exception {
        MemorySource failSource = new MemorySource(
            "Fail",
            "@DisallowMultiple",
            "@" + Resource.class.getName(),
            "public class Fail {}");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            DISALLOW_MULTIPLE,
            failSource);

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.ERROR,
                failSource,
                "",
                "Fail is annotated with @DisallowMultiple which is not allowed with @Resource")));
    }

    @Test
    public void testDisallowMultipleFailAll() throws Exception {
        MemorySource failSource = new MemorySource(
            "Fail",
            "@DisallowMultiple",
            "@" + Resource.class.getName(),
            "@" + Generated.class.getName() + "({})",
            "public class Fail {}");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            DISALLOW_MULTIPLE,
            failSource);

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.ERROR,
                failSource,
                "",
                "Fail is annotated with @DisallowMultiple which is not allowed with @Resource or @Generated")));
    }

    @Test
    public void testDisallowMultiplePass() throws Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            DISALLOW_MULTIPLE,
            new MemorySource("Pass", "@DisallowMultiple public class Pass {}"));
        assertThat(diagnostics, Matchers.empty());

    }
}

