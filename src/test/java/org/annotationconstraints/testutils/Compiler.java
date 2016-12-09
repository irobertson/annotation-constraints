package org.annotationconstraints.testutils;

import static java.util.Arrays.asList;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import org.annotationconstraints.processor.ConstraintProcessor;

public class Compiler {
    private final JavaCompiler javac;

    public Compiler(JavaCompiler javac) {
        this.javac = javac;
    }

    public List<Diagnostic<? extends JavaFileObject>> compileClasses(MemorySource... files) throws Exception {
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
}
