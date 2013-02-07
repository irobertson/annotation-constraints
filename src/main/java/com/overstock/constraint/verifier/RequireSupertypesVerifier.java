package com.overstock.constraint.verifier;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.overstock.constraint.TargetRequiresSupertypes;
import com.overstock.constraint.processor.Constraints;

/**
 * A verifier for {@link com.overstock.constraint.TargetRequiresSupertypes}.
 */
public class RequireSupertypesVerifier extends AbstractVerifier {

  public void verify(Element element, AnnotationMirror annotation, Constraints constraints) {
    AnnotationMirror requireSupertypes = constraints.get(TargetRequiresSupertypes.class);
    if (requireSupertypes == null) {
      return;
    }

    List<TypeMirror> requiredSupertypes = VerifierUtils.getValuesAsTypes(requireSupertypes);
    if (requiredSupertypes.isEmpty()) {
      return;
    }

    Types typeUtils = processingEnv.getTypeUtils();
    for (TypeMirror supertype : VerifierUtils.getSuperTypes(element.asType(), typeUtils)) {
      VerifierUtils.removeType(requiredSupertypes, supertype, typeUtils);
    }

    for (TypeMirror missingRequiredSupertype : requiredSupertypes) {
      raiseAnnotatedClassMessage(
        Diagnostic.Kind.ERROR,
        element,
        annotation,
        " but does not have " + missingRequiredSupertype + " as a supertype");
    }
  }
}
