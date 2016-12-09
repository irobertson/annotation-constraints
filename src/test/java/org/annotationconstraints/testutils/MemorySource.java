package org.annotationconstraints.testutils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class MemorySource extends SimpleJavaFileObject {
    private final String src;
    public MemorySource(String name, String... lines) {
        super(URI.create("file:///" + name + ".java"), Kind.SOURCE);
        StringBuilder builder = new StringBuilder();
        for (String line: lines) {
            builder.append(line).append("\n");
        }
        src = builder.toString();
    }

    public static MemorySource annotationSource(String name, Class<? extends Annotation> annotationClass, String args) {
        String annotationClause = "@" + annotationClass.getName();
        if (! args.isEmpty()) {
            annotationClause += "(" + args + ")";
        }
        return new MemorySource(name, annotationClause, "public @interface " + name + " {}");
    }

    @Override
    public String getCharContent(boolean ignoreEncodingErrors) {
        return src;
    }

    @Override
    public OutputStream openOutputStream() {
        throw new IllegalStateException();
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(src.getBytes());
    }
}
