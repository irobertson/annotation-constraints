package com.overstock.constraint.verifier;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import com.overstock.constraint.RequireAnnotationsOnSupertype;
import com.overstock.constraint.processor.Constraints;

/**
 * A verifier for {@link RequireAnnotationsOnSupertype}.
 */
public class RequireAnnotationsOnSupertypeVerifier extends AbstractVerifier {

  public void verify(Element element, AnnotationMirror annotation, Constraints constraints) {
    AnnotationMirror requireAnnotationsOnSupertype = constraints.get(RequireAnnotationsOnSupertype.class);
    if (requireAnnotationsOnSupertype == null) {
      return;
    }

    List<String> requiredAnnotations = VerifierUtils.getValuesAsClassNames(requireAnnotationsOnSupertype);
    if (requiredAnnotations.isEmpty()) {
      return;
    }

    for (TypeMirror supertypeMirror : VerifierUtils.getSuperTypes(element.asType(), processingEnv.getTypeUtils())) {
      TypeElement supertype = VerifierUtils.asTypeElement(supertypeMirror);
      for (AnnotationMirror supertypeAnnotationMirror : supertype.getAnnotationMirrors()) {
        requiredAnnotations.remove(VerifierUtils.getClassName(supertypeAnnotationMirror));
      }
    }

    for (String missingRequiredAnnotation : requiredAnnotations) {
      raiseAnnotatedClassMessage(
        Diagnostic.Kind.ERROR,
        element,
        annotation,
        " but does not have a supertype annotated with @" + missingRequiredAnnotation);
    }
  }
}
