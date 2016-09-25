package verifier;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import org.annotationconstraints.processor.ConstraintMirror;
import org.annotationconstraints.verifier.AbstractVerifier;

public class BrokenVerifier extends AbstractVerifier {

  @Override
  public void verify(Element element, AnnotationMirror annotationMirror, ConstraintMirror constraint) {
    throw new BrokenVerifierException(getClass().getSimpleName() + " is broken");
  }

}
