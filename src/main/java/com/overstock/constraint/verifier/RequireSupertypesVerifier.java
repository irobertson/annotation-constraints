package com.overstock.constraint.verifier;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.overstock.constraint.TargetRequiresSupertypes;
import com.overstock.constraint.processor.ConstraintMirror;
import com.overstock.constraint.processor.Constraints;
import com.overstock.constraint.processor.MirrorUtils;

/**
 * A verifier for {@link TargetRequiresSupertypes}.
 */
public class RequireSupertypesVerifier extends AbstractVerifier {

  @Override
  public void verify(Element element, AnnotationMirror constrained, Constraints constraints) {
    ConstraintMirror requireSupertypes = constraints.get(TargetRequiresSupertypes.class);
    if (requireSupertypes == null) {
      return;
    }

    List<TypeMirror> requiredSupertypes = VerifierUtils.getValuesAsTypes(requireSupertypes.getAnnotation());
    if (requiredSupertypes.isEmpty()) {
      return;
    }

    Types typeUtils = processingEnv.getTypeUtils();
    for (TypeMirror supertype : MirrorUtils.getSupertypes(element.asType(), typeUtils)) {
      VerifierUtils.removeType(requiredSupertypes, supertype, typeUtils);
    }

    for (TypeMirror missingRequiredSupertype : requiredSupertypes) {
      printMessage(
        Diagnostic.Kind.ERROR,
        element,
        constrained,
        " but does not have " + missingRequiredSupertype + " as a supertype",
        requireSupertypes);
    }
  }
}
