package verifier;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import com.overstock.constraint.processor.Constraints;
import com.overstock.constraint.verifier.AbstractVerifier;

public class CustomVerifier extends AbstractVerifier {

  @Override
  public void verify(Element element, AnnotationMirror annotation, Constraints constraints) {
    String simpleName = "CustomVerifyFail";
    if (element.getSimpleName().contentEquals(simpleName)) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
        "Failing " + simpleName + ".class from a custom verifier", element);
    }
  }

}
