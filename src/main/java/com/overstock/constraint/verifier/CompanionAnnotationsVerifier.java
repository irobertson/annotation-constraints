package com.overstock.constraint.verifier;

import java.util.Collections;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
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

    final List<String> requiredAnnotations = requireAnnotations == null ? Collections.<String>emptyList()
      : VerifierUtils.getValuesAsClassNames(requireAnnotations);
    final List<String> recommendedAnnotations = recommendAnnotations == null ? Collections.<String>emptyList()
      : VerifierUtils.getValuesAsClassNames(recommendAnnotations);

    if (requiredAnnotations.isEmpty() && recommendedAnnotations.isEmpty()) {
      return;
    }

    for (AnnotationMirror annotated : element.getAnnotationMirrors()) {
      String className = annotated.getAnnotationType().asElement().toString();
      requiredAnnotations.remove(className);
      recommendedAnnotations.remove(className);
    }

    for (String missingRequiredAnnotationType : requiredAnnotations) {
      raiseAnnotatedClassMessage(
        Diagnostic.Kind.ERROR,
        element,
        annotation,
        " but not with @" + missingRequiredAnnotationType);
    }
    for (String missingRecommendedAnnotationType : recommendedAnnotations) {
      raiseAnnotatedClassMessage(
        Diagnostic.Kind.WARNING,
        element,
        annotation,
        " but not with @" + missingRecommendedAnnotationType);
    }
  }

}
