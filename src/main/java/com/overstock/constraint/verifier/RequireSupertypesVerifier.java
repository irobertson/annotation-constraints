package com.overstock.constraint.verifier;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.overstock.constraint.processor.MirrorUtils;

/**
 * A verifier for {@link com.overstock.constraint.TargetMustHaveSupertypes}.
 */
public class RequireSupertypesVerifier implements Verifier {

  @Override
  public void verify(VerificationContext context) {
    Types typeUtils = context.getTypeUtils();
    List<TypeMirror> requiredSupertypes = VerifierUtils.eraseGenerics(
      VerifierUtils.getValuesAsTypes(context.getConstraint().getAnnotation()), typeUtils);
    if (requiredSupertypes.isEmpty()) {
      return;
    }

    for (TypeMirror supertype : MirrorUtils.getSupertypes(context.getElement().asType(), typeUtils)) {
      VerifierUtils.removeType(requiredSupertypes, typeUtils.erasure(supertype), typeUtils);
    }

    if (!requiredSupertypes.isEmpty()) {
      MessageBuilder.format(Diagnostic.Kind.ERROR, context)
        .appendText(" but does not have ")
        .appendTypes(requiredSupertypes, "", " or ")
        .appendText(" as a supertype")
        .print();
    }
  }

  @Override
  public void init(ProcessingEnvironment environment) {
  }
}
