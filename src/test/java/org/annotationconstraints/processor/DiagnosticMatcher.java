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
        if (!item.getKind().equals(kind)) {
            mismatchDescription.appendText("had kind ").appendValue(item.getKind());
            return false;
        }
        String itemMessage = item.getMessage(null);
        if (! (itemMessage.equals(message) || itemMessage.substring(itemMessage.indexOf(": ") + 2).equals(message))) {
            mismatchDescription.appendText("has message '").appendText(itemMessage).appendText("'");
            return false;
        }
        if (item.getStartPosition() != start) {
            mismatchDescription.appendText("has start position").appendValue(item.getStartPosition());
            return false;
        }
        return true;
    }

}
