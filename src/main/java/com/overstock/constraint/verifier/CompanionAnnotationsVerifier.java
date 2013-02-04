package com.overstock.constraint.verifier;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import com.overstock.constraint.RecommendAnnotations;
import com.overstock.constraint.RequireAnnotations;
import com.overstock.constraint.processor.Constraints;

/**
 * A verifier for {@link RequireAnnotations} and {@link RecommendAnnotations}.
 */
public class CompanionAnnotationsVerifier extends AbstractVerifier {

  public void verify(Element element, AnnotationMirror annotation, Constraints constraints) {
    AnnotationMirror requireAnnotations = constraints.get(RequireAnnotations.class);
    AnnotationMirror recommendAnnotations = constraints.get(RecommendAnnotations.class);
    if (requireAnnotations == null && recommendAnnotations == null) {
      return;
    }

    List<String> requiredAnnotations = new ArrayList<String>();
    if (requireAnnotations != null) {
      requiredAnnotations.addAll(VerifierUtils.getValuesAsClassNames(requireAnnotations));
    }

    List<String> recommendedAnnotations = new ArrayList<String>();
    if (recommendAnnotations != null) {
      recommendedAnnotations.addAll(VerifierUtils.getValuesAsClassNames(recommendAnnotations));
    }

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
