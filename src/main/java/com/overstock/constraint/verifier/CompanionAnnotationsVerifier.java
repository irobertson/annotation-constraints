package com.overstock.constraint.verifier;

import java.util.Collections;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.overstock.constraint.TargetRecommendsAnnotations;
import com.overstock.constraint.TargetRequiresAnnotations;
import com.overstock.constraint.processor.ConstraintMirror;
import com.overstock.constraint.processor.MirrorUtils;

/**
 * A verifier for {@link TargetRequiresAnnotations} and {@link TargetRecommendsAnnotations}.
 */
public class CompanionAnnotationsVerifier extends AbstractVerifier {

  public void verify(Element element, AnnotationMirror constrained, ConstraintMirror constraint) {
    ConstraintMirror requireAnnotations = null;
    Types typeUtils = processingEnv.getTypeUtils();
    Elements elementUtils = processingEnv.getElementUtils();
    DeclaredType constraintType = constraint.getAnnotation().getAnnotationType();
    if (MirrorUtils.isSameType(TargetRequiresAnnotations.class, constraintType, typeUtils, elementUtils)) {
      requireAnnotations = constraint;
    }

    ConstraintMirror recommendAnnotations = null;
    if (MirrorUtils.isSameType(TargetRecommendsAnnotations.class, constraintType, typeUtils, elementUtils)) {
      recommendAnnotations = constraint;
    }
    if (requireAnnotations == null && recommendAnnotations == null) {
      return;
    }

    final List<TypeMirror> requiredAnnotations = requireAnnotations == null ? Collections.<TypeMirror>emptyList()
      : VerifierUtils.getValuesAsTypes(requireAnnotations.getAnnotation());
    final List<TypeMirror> recommendedAnnotations = recommendAnnotations == null ? Collections.<TypeMirror>emptyList()
      : VerifierUtils.getValuesAsTypes(recommendAnnotations.getAnnotation());

    if (requiredAnnotations.isEmpty() && recommendedAnnotations.isEmpty()) {
      return;
    }

    for (AnnotationMirror annotated : element.getAnnotationMirrors()) {
      TypeMirror annotatedType = annotated.getAnnotationType().asElement().asType();
      VerifierUtils.removeType(requiredAnnotations, annotatedType, typeUtils);
      VerifierUtils.removeType(recommendedAnnotations, annotatedType, typeUtils);
      if (requiredAnnotations.isEmpty() && recommendedAnnotations.isEmpty()) {
        return;
      }
    }

    printMessage(
      Diagnostic.Kind.WARNING,
      element,
      constrained,
      " but not with " + formatAnnotations(recommendedAnnotations, " and "),
      recommendAnnotations);

    printMessage(
      Diagnostic.Kind.ERROR,
      element,
      constrained,
      " but not with " + formatAnnotations(requiredAnnotations, " and "),
      requireAnnotations);
  }
}
