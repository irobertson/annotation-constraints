package verifier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import com.overstock.constraint.processor.ConstraintMirror;
import com.overstock.constraint.verifier.VerificationContext;
import com.overstock.constraint.verifier.Verifier;

public class CustomVerifier implements Verifier {

  @Override
  public void verify(VerificationContext context) {
    context.getMessager().printMessage(Diagnostic.Kind.ERROR,
      "Failing " + context.getElement().getSimpleName() + ".class from a custom verifier", context.getElement());
  }

  @Override
  public void init(ProcessingEnvironment environment) {
  }

}
