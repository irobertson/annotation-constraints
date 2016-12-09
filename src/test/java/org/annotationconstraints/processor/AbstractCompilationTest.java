package org.annotationconstraints.processor;

import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.annotationconstraints.testutils.Compiler;
import org.annotationconstraints.testutils.DiagnosticMatcher;
import org.annotationconstraints.testutils.MemorySource;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class AbstractCompilationTest {

    @Parameters(name = "{1}")
    public static Iterable<Object[]> compilers() {
        return Arrays.asList(
            new Object[] { ToolProvider.getSystemJavaCompiler(), "javac" },
            new Object[] { new EclipseCompiler(), "eclipse" });
    }

    @Parameter
    public JavaCompiler javaCompiler;

    @Parameter(1)
    public String compilerName;

    private Compiler compiler;

    @Before
    public void createCompiler() {
        compiler = new Compiler(javaCompiler);
    }

    protected List<Diagnostic<? extends JavaFileObject>> compileClasses(MemorySource... files) throws Exception {
        return compiler.compileClasses(files);
    }

    protected DiagnosticMatcher diagnostic(Kind kind, MemorySource source, String problemText, String message) {
        return new DiagnosticMatcher(kind, source, problemText, message);
    }

    protected static String classRef(Class<?> clazz) {
        return clazz.getName() + ".class";
    }

    protected static String classRefs(Class<?>... classes) {
        StringBuilder builder = new StringBuilder("{");
        for (int i = 0; i < classes.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(classRef(classes[i]));
        }
        return builder.append("}").toString();
    }

}
