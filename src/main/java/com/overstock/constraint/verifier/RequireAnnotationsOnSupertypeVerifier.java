package com.overstock.constraint.verifier;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.overstock.constraint.TargetRequiresAnnotationsOnSupertype;
import com.overstock.constraint.processor.Constraints;

/**
 * A verifier for {@link TargetRequiresAnnotationsOnSupertype}.
 */
public class RequireAnnotationsOnSupertypeVerifier extends AbstractVerifier {

  @Override
  public void verify(Element element, AnnotationMirror constrained, Constraints constraints) {
    AnnotationMirror requireAnnotationsOnSupertype = constraints.get(TargetRequiresAnnotationsOnSupertype.class);
    if (requireAnnotationsOnSupertype == null) {
      return;
    }

    List<TypeMirror> requiredAnnotations = VerifierUtils.getValuesAsTypes(requireAnnotationsOnSupertype);
    if (requiredAnnotations.isEmpty()) {
      return;
    }

    Types typeUtils = processingEnv.getTypeUtils();
    for (TypeMirror supertypeMirror : VerifierUtils.getSuperTypes(element.asType(), typeUtils)) {
      TypeElement supertype = VerifierUtils.asTypeElement(supertypeMirror);
      for (AnnotationMirror supertypeAnnotationMirror : supertype.getAnnotationMirrors()) {
        VerifierUtils.removeType(requiredAnnotations, supertypeAnnotationMirror.getAnnotationType(), typeUtils);
        requiredAnnotations.remove(VerifierUtils.asType(supertypeAnnotationMirror));
      }
    }

    for (TypeMirror missingRequiredAnnotation : requiredAnnotations) {
      printMessage(
        Diagnostic.Kind.ERROR,
        element,
        constrained,
        " but does not have a supertype annotated with @" + getSimpleName(missingRequiredAnnotation));
    }
  }
}
