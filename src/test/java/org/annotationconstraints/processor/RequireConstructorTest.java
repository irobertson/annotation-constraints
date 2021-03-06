package org.annotationconstraints.processor;
import static org.annotationconstraints.testutils.MemorySource.annotationSource;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.annotationconstraints.TargetMustHaveConstructors;
import org.annotationconstraints.testutils.MemorySource;;

public class RequireConstructorTest extends AbstractCompilationTest {
    private static final MemorySource REQUIRE_NO_ARG_CONSTRUCTOR = annotationSource(
        "RequireNoArgConstructor", TargetMustHaveConstructors.class, "@org.annotationconstraints.Constructor({})");

    private static final MemorySource REQUIRE_STRING_CONSTRUCTOR = annotationSource(
        "RequireStringConstructor",
        TargetMustHaveConstructors.class,
        "@org.annotationconstraints.Constructor({String.class})");

    private static final MemorySource REQUIRE_STRING_LONG_INT_ARRAY_CONSTRUCTOR = annotationSource(
        "RequireStringLongIntArrayConstructor",
        TargetMustHaveConstructors.class,
        "@org.annotationconstraints.Constructor({String.class, long.class, int[].class})");

    @Test
    public void testRequireNoArgConstructorFail() throws ClassNotFoundException, Exception {
        MemorySource failSource = new MemorySource(
            "Fail",
            "@RequireNoArgConstructor",
            "public class Fail {",
            "  public Fail(String s) {}",
            "}");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_NO_ARG_CONSTRUCTOR,
            failSource);

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.ERROR,
                failSource,
                "",
                "Fail is annotated with @RequireNoArgConstructor but does not have a constructor with no arguments")));
    }

    @Test
    public void testRequireNoArgConstructorPass() throws ClassNotFoundException, Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_NO_ARG_CONSTRUCTOR,
            new MemorySource("Pass", "@RequireNoArgConstructor public class Pass {}"));
        assertThat(diagnostics, Matchers.empty());
    }

    @Test
    public void testRequireNoArgConstructorStaticMemberClassPass() throws ClassNotFoundException, Exception {
        MemorySource source = new MemorySource(
            "Containing", "public class Containing { @RequireNoArgConstructor public static class Pass {} }");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(REQUIRE_NO_ARG_CONSTRUCTOR, source);
        if ("eclipse".equals(compilerName)) {
            // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=508222
            // verify the eroneous error message; when ECJ gets fixed, this test will fail, telling us its time to
            // fix the comment on TargetMustHaveConstructors referencing the bug.
            assertThat(diagnostics, contains(
                diagnostic(
                    Kind.ERROR,
                    source,
                    "@RequireNoArgConstructor",
                    "Containing.Pass is annotated with @RequireNoArgConstructor but is an inner class")));
        }
        else {
            assertThat(diagnostics, Matchers.empty());
        }
    }

    @Test
    public void testRequireNoArgConstructorInnerClassFail() throws ClassNotFoundException, Exception {
        MemorySource failSource = new MemorySource(
            "Containing",
            "public class Containing { ",
            "  @RequireNoArgConstructor public class Fail {}",
            " }");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(REQUIRE_NO_ARG_CONSTRUCTOR, failSource);
        assertThat(diagnostics, contains(
            diagnostic(
                Kind.ERROR,
                failSource,
                "@RequireNoArgConstructor",
                "Containing.Fail is annotated with @RequireNoArgConstructor but is an inner class")));
    }

    @Test
    public void testRequireStringConstructorFail() throws ClassNotFoundException, Exception {
        MemorySource failSource = new MemorySource("Fail", "@RequireStringConstructor public class Fail {}");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_STRING_CONSTRUCTOR,
            failSource);

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.ERROR,
                failSource,
                "",
                "Fail is annotated with @RequireStringConstructor but does not have a constructor with arguments (java.lang.String)")));
    }

    @Test
    public void testRequireStringConstructorPass() throws ClassNotFoundException, Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_STRING_CONSTRUCTOR,
            new MemorySource("Pass", "@RequireStringConstructor public class Pass { public Pass(String s) {} }"));
        assertThat(diagnostics, Matchers.empty());
    }

    @Test
    public void testRequireStringLongIntArrayConstructorFail() throws ClassNotFoundException, Exception {
        MemorySource failSource = new MemorySource("Fail", "@RequireStringLongIntArrayConstructor public class Fail {}");
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_STRING_LONG_INT_ARRAY_CONSTRUCTOR,
            failSource);

        assertThat(diagnostics, contains(
            diagnostic(
                Kind.ERROR,
                failSource,
                "",
                "Fail is annotated with @RequireStringLongIntArrayConstructor but does not have a constructor with arguments (java.lang.String, long, int[])")));
    }

    @Test
    public void testRequireStringLongIntArrayConstructorPass() throws ClassNotFoundException, Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compileClasses(
            REQUIRE_STRING_LONG_INT_ARRAY_CONSTRUCTOR,
            new MemorySource("Pass", "@RequireStringLongIntArrayConstructor public class Pass { public Pass(String s, long l, int[] a) {} }"));
        assertThat(diagnostics, Matchers.empty());
    }
}

