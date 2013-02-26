package verifier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import com.overstock.constraint.processor.ConstraintMirror;
import com.overstock.constraint.verifier.AbstractVerifier;

public class VerifierSubclassPass extends AbstractVerifier {

  public void verify(Element element, AnnotationMirror annotation, ConstraintMirror constraint) {}
  public void init(ProcessingEnvironment environment) {}

}
