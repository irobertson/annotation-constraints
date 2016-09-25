package org.annotationconstraints.verifier;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import org.annotationconstraints.processor.ConstraintMirror;
import org.annotationconstraints.processor.MirrorUtils;

/**
 * A verifier for {@link org.annotationconstraints.TargetMustHaveASupertypeAnnotatedWith}.
 */
public class RequireAnnotationsOnSupertypeVerifier extends AbstractVerifier {

  @Override
  public void verify(Element element, AnnotationMirror annotationMirror, ConstraintMirror constraint) {
    List<TypeMirror> requiredAnnotations = VerifierUtils.getValuesAsTypes(constraint.getAnnotation());
    if (requiredAnnotations.isEmpty()) {
      return;
    }

    Types typeUtils = getTypeUtils();
    for (TypeMirror supertypeMirror : MirrorUtils.getSupertypes(element.asType(), typeUtils)) {
      TypeElement supertype = VerifierUtils.asTypeElement(supertypeMirror);
      for (AnnotationMirror supertypeAnnotationMirror : supertype.getAnnotationMirrors()) {
        VerifierUtils.removeType(requiredAnnotations, supertypeAnnotationMirror.getAnnotationType(), typeUtils);
        requiredAnnotations.remove(VerifierUtils.asType(supertypeAnnotationMirror));
      }
    }

    if (!requiredAnnotations.isEmpty()) {
      messageBuilder(Diagnostic.Kind.ERROR, element, annotationMirror, constraint)
        .appendText(" but does not have a supertype annotated with ")
        .appendAnnotations(requiredAnnotations, " or ")
        .print();
    }
  }
}
