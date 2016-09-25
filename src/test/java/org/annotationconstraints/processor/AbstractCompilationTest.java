package org.annotationconstraints.processor;

import static java.util.Arrays.asList;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
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
    public JavaCompiler compiler;

    @Parameter(1)
    public String compilerName;

    protected List<Diagnostic<? extends JavaFileObject>> compileClasses(MemorySource... files) throws Exception {
        JavaCompiler javac = compiler;

        SpecialClassLoader cl = new SpecialClassLoader();
        StandardJavaFileManager sjfm = javac.getStandardFileManager(null, null, null);
        SpecialJavaFileManager fileManager = new SpecialJavaFileManager(sjfm, cl);
        DiagnosticCollector<JavaFileObject> diagnosticListener = new DiagnosticCollector<JavaFileObject>();
        Writer out = new PrintWriter(System.err);
        JavaCompiler.CompilationTask compile = javac.getTask(
            out,
            fileManager,
            diagnosticListener,
            Collections.<String> emptyList(),
            null,
            asList(files));
        compile.setProcessors(Arrays.asList(new ConstraintProcessor()));
        compile.call();
        sjfm.close();
        fileManager.close();
        return diagnosticListener.getDiagnostics();
    }

    protected DiagnosticMatcher diagnostic(Kind kind, MemorySource source, String problemText, String message) {
        return new DiagnosticMatcher(kind, source, problemText, message);
    }

}
