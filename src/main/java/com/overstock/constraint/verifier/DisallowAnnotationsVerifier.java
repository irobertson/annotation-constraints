package com.overstock.constraint.verifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.overstock.constraint.TargetDisallowsAnnotations;
import com.overstock.constraint.processor.ConstraintMirror;
import com.overstock.constraint.processor.Constraints;

/**
 * A verifier for {@link TargetDisallowsAnnotations}.
 */
public class DisallowAnnotationsVerifier extends AbstractVerifier {

  @Override
  public void verify(Element element, AnnotationMirror constrained, Constraints constraints) {
    ConstraintMirror disallowAnnotations = constraints.get(TargetDisallowsAnnotations.class);
    if (disallowAnnotations == null) {
      return;
    }

    Collection<TypeMirror> disallowedAnnotations = VerifierUtils.getValuesAsTypes(disallowAnnotations.getAnnotation());
    if (disallowedAnnotations.isEmpty()) {
      return;
    }

    Types typeUtils = processingEnv.getTypeUtils();
    List<TypeMirror> presentAndDisallowed = new ArrayList<TypeMirror>();
    for (AnnotationMirror annotated : element.getAnnotationMirrors()) {
      TypeMirror annotatedType = VerifierUtils.asType(annotated);
      for (TypeMirror disallowedAnnotation : disallowedAnnotations) {
        if (typeUtils.isSameType(annotatedType, disallowedAnnotation)) {
          presentAndDisallowed.add(annotatedType);
        }
      }
    }

    for (TypeMirror presentAndDisallowedAnnotationType : presentAndDisallowed) {
      printMessage(
        Diagnostic.Kind.ERROR,
        element,
        constrained,
        " which is not allowed with @" + getSimpleName(presentAndDisallowedAnnotationType),
        disallowAnnotations);
    }
  }
}
