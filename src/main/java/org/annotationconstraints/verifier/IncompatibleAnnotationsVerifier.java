package org.annotationconstraints.verifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import org.annotationconstraints.processor.ConstraintMirror;

/**
 * A verifier for {@link org.annotationconstraints.TargetMustNotBeAnnotatedWith}.
 */
public class IncompatibleAnnotationsVerifier extends AbstractVerifier {

  @Override
  public void verify(Element element, AnnotationMirror annotationMirror, ConstraintMirror constraint) {
    Collection<TypeMirror> disallowedAnnotations = VerifierUtils.getValuesAsTypes(constraint.getAnnotation());
    if (disallowedAnnotations.isEmpty()) {
      return;
    }

    Types typeUtils = getTypeUtils();
    List<TypeMirror> presentAndDisallowed = new ArrayList<TypeMirror>();
    for (AnnotationMirror annotated : element.getAnnotationMirrors()) {
      TypeMirror annotatedType = VerifierUtils.asType(annotated);
      for (TypeMirror disallowedAnnotation : disallowedAnnotations) {
        if (typeUtils.isSameType(annotatedType, disallowedAnnotation)) {
          presentAndDisallowed.add(annotatedType);
        }
      }
    }

    if (!presentAndDisallowed.isEmpty()) {
      messageBuilder(Diagnostic.Kind.ERROR, element, annotationMirror, constraint)
        .appendText(" which is not allowed with ")
        .appendAnnotations(presentAndDisallowed, " or ")
        .print();
    }
  }
}
