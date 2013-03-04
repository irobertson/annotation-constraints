package com.overstock.constraint.verifier;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.overstock.constraint.processor.ConstraintMirror;
import com.overstock.constraint.processor.MirrorUtils;

/**
 * A verifier for {@link com.overstock.constraint.TargetMustHaveASupertypeAnnotatedWith}.
 */
public class RequireAnnotationsOnSupertypeVerifier extends AbstractVerifier {

  @Override
  public void verify(Element element, AnnotationMirror constrained, ConstraintMirror constraint) {
    List<TypeMirror> requiredAnnotations = VerifierUtils.getValuesAsTypes(constraint.getAnnotation());
    if (requiredAnnotations.isEmpty()) {
      return;
    }

    Types typeUtils = processingEnv.getTypeUtils();
    for (TypeMirror supertypeMirror : MirrorUtils.getSupertypes(element.asType(), typeUtils)) {
      TypeElement supertype = VerifierUtils.asTypeElement(supertypeMirror);
      for (AnnotationMirror supertypeAnnotationMirror : supertype.getAnnotationMirrors()) {
        VerifierUtils.removeType(requiredAnnotations, supertypeAnnotationMirror.getAnnotationType(), typeUtils);
        requiredAnnotations.remove(VerifierUtils.asType(supertypeAnnotationMirror));
      }
    }

    if (!requiredAnnotations.isEmpty()) {
      printMessage(
        Diagnostic.Kind.ERROR,
        element,
        constrained,
        " but does not have a supertype annotated with " + formatAnnotations(requiredAnnotations, " or "),
        constraint);
    }
  }
}
