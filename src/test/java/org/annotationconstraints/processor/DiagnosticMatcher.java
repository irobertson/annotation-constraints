package org.annotationconstraints.processor;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class DiagnosticMatcher extends TypeSafeDiagnosingMatcher<Diagnostic<? extends JavaFileObject>> {
    private final Kind kind;
    private final String message;
    private final int start;

    public DiagnosticMatcher(Kind kind, MemorySource source, String problemCode, String message) {
        this.kind = kind;
        this.message = message;
        this.start = source.getCharContent(true).indexOf(problemCode);
    }


    @Override
    public void describeTo(Description description) {
        description.appendValue(kind).appendText(": ").appendText(message)
            .appendText(" at ").appendValue(start)
            .appendText(" with message '").appendText(message).appendText("'");
    }

    @Override
    protected boolean matchesSafely(Diagnostic<? extends JavaFileObject> item, Description mismatchDescription) {
        return item.getKind().equals(kind)
            && item.getMessage(null).equals(message)
            && item.getStartPosition() == start;
    }

}
