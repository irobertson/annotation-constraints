package verifier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import com.overstock.constraint.processor.Constraints;
import com.overstock.constraint.verifier.AbstractVerifier;

public class VerifierSubclassPass extends AbstractVerifier {

  public void verify(Element element, AnnotationMirror annotation, Constraints constraints) {}
  public void init(ProcessingEnvironment environment) {}

}
