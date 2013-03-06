package verifier;

import java.lang.Object;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.ws.rs.Path;

import com.overstock.constraint.processor.ConstraintMirror;
import com.overstock.constraint.processor.MirrorUtils;
import com.overstock.constraint.verifier.MessageBuilder;
import com.overstock.constraint.verifier.AbstractVerifier;

public class ReservedPathVerifier extends AbstractVerifier {

  @Override
  public void verify(Element element, AnnotationMirror annotationMirror, ConstraintMirror constraint) {
    List<String> reservedPaths = getReservedPaths(constraint);

    List<? extends AnnotationMirror> annotationMirrors = getElementUtils()
      .getAllAnnotationMirrors(element);
    for (AnnotationMirror possiblePathAnnotation : annotationMirrors) {
      if (MirrorUtils.isSameType(
          Path.class, possiblePathAnnotation.getAnnotationType(), getTypeUtils(), getElementUtils())) {
        String path = MirrorUtils.getAnnotationValue(possiblePathAnnotation, "value").getValue().toString();
        if (reservedPaths.contains(path)) {
          MessageBuilder.format(Diagnostic.Kind.ERROR, processingEnv, element, annotationMirror, constraint)
            .appendText(" using a reserved path: " + path)
            .print();
        }
      }
    }
  }

  private List<String> getReservedPaths(ConstraintMirror constraint) {
    List<String> reservedPaths = new ArrayList<String>();
    AnnotationValue value = MirrorUtils.getAnnotationValue(constraint.getAnnotation(), "value");
    for (AnnotationValue annotationValue : (List<AnnotationValue>) value.getValue()) {
      reservedPaths.add(annotationValue.getValue().toString());
    }
    return reservedPaths;
  }
}
