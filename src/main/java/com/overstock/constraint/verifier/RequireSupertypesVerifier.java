package com.overstock.constraint.verifier;

import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import com.overstock.constraint.RequireSupertypes;
import com.overstock.constraint.processor.Constraints;

/**
 * A verifier for {@link RequireSupertypes}.
 */
public class RequireSupertypesVerifier extends AbstractVerifier {

  public void verify(Element element, AnnotationMirror annotation, Constraints constraints) {
    AnnotationMirror requireSupertypes = constraints.get(RequireSupertypes.class);
    if (requireSupertypes == null) {
      return;
    }

    Set<String> requiredSupertypes = VerifierUtils.getValuesAsClassNames(requireSupertypes);
    if (requiredSupertypes.isEmpty()) {
      return;
    }

    for (TypeMirror supertype : VerifierUtils.getSuperTypes(element.asType(), processingEnv.getTypeUtils())) {
      requiredSupertypes.remove(VerifierUtils.getClassName(supertype));
    }

    for (String missingRequiredSupertype : requiredSupertypes) {
      raiseAnnotatedClassMessage(
        Diagnostic.Kind.ERROR,
        element,
        annotation,
        " but does not have " + missingRequiredSupertype + " as a supertype");
    }
  }
}
