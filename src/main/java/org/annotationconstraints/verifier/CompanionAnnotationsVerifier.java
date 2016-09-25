package org.annotationconstraints.verifier;

import java.util.Collections;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import org.annotationconstraints.TargetMustBeAnnotatedWith;
import org.annotationconstraints.TargetShouldBeAnnotatedWith;
import org.annotationconstraints.processor.ConstraintMirror;
import org.annotationconstraints.processor.MirrorUtils;

/**
 * A verifier for {@link org.annotationconstraints.TargetMustBeAnnotatedWith} and {@link org.annotationconstraints.TargetShouldBeAnnotatedWith}.
 */
public class CompanionAnnotationsVerifier extends AbstractVerifier {

  @Override
  public void verify(Element element, AnnotationMirror annotationMirror, ConstraintMirror constraint) {
    Types typeUtils = getTypeUtils();
    Elements elementUtils = getElementUtils();
    DeclaredType constraintType = constraint.getAnnotation().getAnnotationType();

    ConstraintMirror requireAnnotations = null;
    if (MirrorUtils.isSameType(TargetMustBeAnnotatedWith.class, constraintType, typeUtils, elementUtils)) {
      requireAnnotations = constraint;
    }

    ConstraintMirror recommendAnnotations = null;
    if (MirrorUtils.isSameType(TargetShouldBeAnnotatedWith.class, constraintType, typeUtils, elementUtils)) {
      recommendAnnotations = constraint;
    }

    final List<TypeMirror> requiredAnnotations = requireAnnotations == null ? Collections.<TypeMirror>emptyList()
      : VerifierUtils.getValuesAsTypes(requireAnnotations.getAnnotation());
    final List<TypeMirror> recommendedAnnotations = recommendAnnotations == null ? Collections.<TypeMirror>emptyList()
      : VerifierUtils.getValuesAsTypes(recommendAnnotations.getAnnotation());

    if (requiredAnnotations.isEmpty() && recommendedAnnotations.isEmpty()) {
      return; //no constraints
    }

    for (AnnotationMirror annotated : element.getAnnotationMirrors()) {
      TypeMirror annotatedType = annotated.getAnnotationType().asElement().asType();
      VerifierUtils.removeType(requiredAnnotations, annotatedType, typeUtils);
      VerifierUtils.removeType(recommendedAnnotations, annotatedType, typeUtils);
      if (requiredAnnotations.isEmpty() && recommendedAnnotations.isEmpty()) {
        return; //early termination
      }
    }

    if (!recommendedAnnotations.isEmpty()) {
      messageBuilder(Diagnostic.Kind.WARNING, element, annotationMirror, constraint)
        .appendText(" but not with ")
        .appendAnnotations(recommendedAnnotations, " and ")
        .print();
    }

    if (!requiredAnnotations.isEmpty()) {
      messageBuilder(Diagnostic.Kind.ERROR, element, annotationMirror, constraint)
        .appendText(" but not with ")
        .appendAnnotations(requiredAnnotations, " and ")
        .print();
    }
  }
}
