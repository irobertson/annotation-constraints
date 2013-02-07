package com.overstock.constraint.verifier;

import java.util.Collections;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import com.overstock.constraint.TargetRecommendsAnnotations;
import com.overstock.constraint.TargetRequiresAnnotations;
import com.overstock.constraint.processor.Constraints;

/**
 * A verifier for {@link com.overstock.constraint.TargetRequiresAnnotations} and {@link com.overstock.constraint.TargetRecommendsAnnotations}.
 */
public class CompanionAnnotationsVerifier extends AbstractVerifier {

  public void verify(Element element, AnnotationMirror annotation, Constraints constraints) {
    AnnotationMirror requireAnnotations = constraints.get(TargetRequiresAnnotations.class);
    AnnotationMirror recommendAnnotations = constraints.get(TargetRecommendsAnnotations.class);
    if (requireAnnotations == null && recommendAnnotations == null) {
      return;
    }

    final List<TypeMirror> requiredAnnotations = requireAnnotations == null ? Collections.<TypeMirror>emptyList()
      : VerifierUtils.getValuesAsTypes(requireAnnotations);
    final List<TypeMirror> recommendedAnnotations = recommendAnnotations == null ? Collections.<TypeMirror>emptyList()
      : VerifierUtils.getValuesAsTypes(recommendAnnotations);

    if (requiredAnnotations.isEmpty() && recommendedAnnotations.isEmpty()) {
      return;
    }

    for (AnnotationMirror annotated : element.getAnnotationMirrors()) {
      TypeMirror annotatedType = annotated.getAnnotationType().asElement().asType();
      VerifierUtils.removeType(requiredAnnotations, annotatedType, processingEnv.getTypeUtils());
      VerifierUtils.removeType(recommendedAnnotations, annotatedType, processingEnv.getTypeUtils());
      if (requiredAnnotations.isEmpty() && recommendedAnnotations.isEmpty()) {
        return;
      }
    }

    for (TypeMirror missingRequiredAnnotationType : requiredAnnotations) {
      raiseAnnotatedClassMessage(
        Diagnostic.Kind.ERROR,
        element,
        annotation,
        " but not with @" + missingRequiredAnnotationType);
    }
    for (TypeMirror missingRecommendedAnnotationType : recommendedAnnotations) {
      raiseAnnotatedClassMessage(
        Diagnostic.Kind.WARNING,
        element,
        annotation,
        " but not with @" + missingRecommendedAnnotationType);
    }
  }
}
