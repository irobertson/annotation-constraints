package verifier;

import java.lang.Object;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.ws.rs.Path;

import com.overstock.constraint.processor.MirrorUtils;
import com.overstock.constraint.verifier.MessageBuilder;
import com.overstock.constraint.verifier.VerificationContext;
import com.overstock.constraint.verifier.Verifier;

public class ReservedPathVerifier implements Verifier {

  @Override
  public void verify(VerificationContext context) {
    List<String> reservedPaths = getReservedPaths(context);

    List<? extends AnnotationMirror> annotationMirrors = context.getElementUtils()
      .getAllAnnotationMirrors(context.getElement());
    for (AnnotationMirror annotationMirror : annotationMirrors) {
      if (MirrorUtils.isSameType(Path.class, annotationMirror.getAnnotationType(), context.getTypeUtils(),
          context.getElementUtils())) {
        String path = MirrorUtils.getAnnotationValue(annotationMirror, "value").getValue().toString();
        if (reservedPaths.contains(path)) {
          MessageBuilder.format(Diagnostic.Kind.ERROR, context)
            .appendText(" using a reserved path: " + path)
            .print();
        }
      }
    }
  }

  private List<String> getReservedPaths(VerificationContext context) {
    List<String> reservedPaths = new ArrayList<String>();
    AnnotationValue value = MirrorUtils.getAnnotationValue(context.getConstraint().getAnnotation(), "value");
    for (AnnotationValue annotationValue : (List<AnnotationValue>) value.getValue()) {
      reservedPaths.add(annotationValue.getValue().toString());
    }
    return reservedPaths;
  }

  @Override
  public void init(ProcessingEnvironment environment) {
  }
}
