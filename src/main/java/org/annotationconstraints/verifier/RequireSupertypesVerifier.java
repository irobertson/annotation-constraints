package org.annotationconstraints.verifier;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import org.annotationconstraints.processor.ConstraintMirror;
import org.annotationconstraints.processor.MirrorUtils;

/**
 * A verifier for {@link org.annotationconstraints.TargetMustHaveSupertypes}.
 */
public class RequireSupertypesVerifier extends AbstractVerifier {

  @Override
  public void verify(Element element, AnnotationMirror annotationMirror, ConstraintMirror constraint) {
    Types typeUtils = getTypeUtils();
    List<TypeMirror> requiredSupertypes = VerifierUtils.eraseGenerics(
      VerifierUtils.getValuesAsTypes(constraint.getAnnotation()), typeUtils);
    if (requiredSupertypes.isEmpty()) {
      return;
    }

    for (TypeMirror supertype : MirrorUtils.getSupertypes(element.asType(), typeUtils)) {
      VerifierUtils.removeType(requiredSupertypes, typeUtils.erasure(supertype), typeUtils);
    }

    if (!requiredSupertypes.isEmpty()) {
      messageBuilder(Diagnostic.Kind.ERROR, element, annotationMirror, constraint)
        .appendText(" but does not have ")
        .appendTypes(requiredSupertypes, "", " or ")
        .appendText(" as a supertype")
        .print();
    }
  }
}
