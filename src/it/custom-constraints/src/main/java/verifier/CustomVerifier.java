package verifier;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import org.annotationconstraints.processor.ConstraintMirror;
import org.annotationconstraints.verifier.AbstractVerifier;

public class CustomVerifier extends AbstractVerifier {

  @Override
  public void verify(Element element, AnnotationMirror annotationMirror, ConstraintMirror constraint) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
      "Failing " + element.getSimpleName() + ".class from " + getClass().getSimpleName(), element);
  }

  public static class NestedVerifier extends AbstractVerifier {
    @Override
    public void verify(Element element, AnnotationMirror annotationMirror, ConstraintMirror constraint) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
        "Failing " + element.getSimpleName() + ".class from " + getClass().getSimpleName(), element);
    }
  }

}
