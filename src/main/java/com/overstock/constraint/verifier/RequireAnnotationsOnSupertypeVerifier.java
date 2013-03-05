package com.overstock.constraint.verifier;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.overstock.constraint.processor.MirrorUtils;

/**
 * A verifier for {@link com.overstock.constraint.TargetMustHaveASupertypeAnnotatedWith}.
 */
public class RequireAnnotationsOnSupertypeVerifier implements Verifier {

  @Override
  public void verify(VerificationContext context) {
    List<TypeMirror> requiredAnnotations = VerifierUtils.getValuesAsTypes(context.getConstraint().getAnnotation());
    if (requiredAnnotations.isEmpty()) {
      return;
    }

    Types typeUtils = context.getTypeUtils();
    for (TypeMirror supertypeMirror : MirrorUtils.getSupertypes(context.getElement().asType(), typeUtils)) {
      TypeElement supertype = VerifierUtils.asTypeElement(supertypeMirror);
      for (AnnotationMirror supertypeAnnotationMirror : supertype.getAnnotationMirrors()) {
        VerifierUtils.removeType(requiredAnnotations, supertypeAnnotationMirror.getAnnotationType(), typeUtils);
        requiredAnnotations.remove(VerifierUtils.asType(supertypeAnnotationMirror));
      }
    }

    if (!requiredAnnotations.isEmpty()) {
      MessageBuilder.format(Diagnostic.Kind.ERROR, context)
        .appendText(" but does not have a supertype annotated with ")
        .appendAnnotations(requiredAnnotations, " or ")
        .print();
    }
  }

  @Override
  public void init(ProcessingEnvironment environment) {
  }
}
