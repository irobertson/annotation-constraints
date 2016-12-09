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
import org.annotationconstraints.TargetShouldBeAnnotatedWith;
import org.annotationconstraints.testutils.MemorySource;;

public class RecommendAnnotationsTest extends AbstractCompilationTest {
    private static final MemorySource RECOMMEND_RESOURCE = annotationSource(
        "RecommendResource", TargetShouldBeAnnotatedWith.class, classRef(Resource.class));

    private static final MemorySource RECOMMEND_MULTIPLE = annotationSource(
        "RecommendMultiple",
        TargetShouldBeAnnotatedWith.class,
        classRefs(Resource.class, Generated.class));

    @Test
    public void testRecommendOneFail() throws ClassNotFoundException, Exception {
        MemorySource failSource = new MemorySource(
            "Fail",
            "@RecommendResource",
            "public class Fail {}");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            RECOMMEND_RESOURCE,
            failSource);

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.WARNING,
                failSource,
                "",
                "Fail is annotated with @RecommendResource but not with @Resource")));
    }

    @Test
    public void testRecommendOnePass() throws Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            RECOMMEND_RESOURCE,
            new MemorySource(
                "Pass",
                "@RecommendResource",
                "@" + Resource.class.getName(),
                "public class Pass {}"));
        assertThat(diagnostics, Matchers.empty());
    }

    @Test
    public void testRecommendMultipleFailOne() throws Exception {
        MemorySource failSource = new MemorySource(
            "Fail",
            "@RecommendMultiple",
            "@" + Resource.class.getName(),
            "public class Fail {}");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            RECOMMEND_MULTIPLE,
            failSource);

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.WARNING,
                failSource,
                "",
                "Fail is annotated with @RecommendMultiple but not with @Generated")));
    }

    @Test
    public void testRecommendMultipleFailAll() throws Exception {
        MemorySource failSource = new MemorySource(
            "Fail",
            "@RecommendMultiple",
            "public class Fail {}");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            RECOMMEND_MULTIPLE,
            failSource);

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.WARNING,
                failSource,
                "",
                "Fail is annotated with @RecommendMultiple but not with @Resource and @Generated")));
    }

    @Test
    public void testRecommendMultiplePass() throws Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            RECOMMEND_MULTIPLE,
            new MemorySource("Pass",
                "@RecommendMultiple",
                "@" + Resource.class.getName(),
                "@" + Generated.class.getName() + "({})",
                "public class Pass {}"));
        assertThat(diagnostics, Matchers.empty());

    }
}

