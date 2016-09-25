package verifier;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import org.annotationconstraints.processor.ConstraintMirror;
import org.annotationconstraints.verifier.AbstractVerifier;

public class UninstantiableVerifier extends AbstractVerifier {

  public UninstantiableVerifier() {
    throw new BrokenVerifierException(getClass().getSimpleName() + " is not instantiable");
  }

  @Override
  public void verify(Element element, AnnotationMirror annotationMirror, ConstraintMirror constraint) {
  }

}
