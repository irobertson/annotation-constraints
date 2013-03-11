package verifier;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import com.overstock.constraint.processor.ConstraintMirror;
import com.overstock.constraint.verifier.AbstractVerifier;

public class UninstantiableVerifier extends AbstractVerifier {

  public UninstantiableVerifier() {
    throw new BrokenVerifierException(getClass().getSimpleName() + " is not instantiable");
  }

  @Override
  public void verify(Element element, AnnotationMirror annotationMirror, ConstraintMirror constraint) {
  }

}
