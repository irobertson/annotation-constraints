package com.overstock.constraint.verifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.overstock.constraint.RequireSupertypes;
import com.overstock.constraint.processor.Constraints;

/**
 * A verifier for {@link RequireSupertypes}.
 */
public class RequireSupertypesVerifier extends AbstractVerifier {

  public RequireSupertypesVerifier(ProcessingEnvironment processingEnv) {
    super(processingEnv);
  }

  public void verify(Element element, AnnotationMirror annotation, Constraints constraints) {
    AnnotationMirror requireSupertypes = constraints.get(RequireSupertypes.class);
    if (requireSupertypes == null) {
      return;
    }

    Set<String> requiredSupertypes = new HashSet<String>(VerifierUtils.getValuesAsClassNames(requireSupertypes));
    if (requiredSupertypes.isEmpty()) {
      return;
    }

    Set<String> missingSupertypes = missingSupertypes(processingEnv.getTypeUtils().directSupertypes(element.asType()),
      requiredSupertypes, processingEnv.getTypeUtils()
    );

    for (String missingRequiredSupertype : missingSupertypes) {
      raiseAnnotatedClassMessage(
        Diagnostic.Kind.ERROR,
        element,
        annotation,
        " but does not have " + missingRequiredSupertype + " as a supertype");
    }
  }

  private Set<String> missingSupertypes(List<? extends TypeMirror> supertypes, Set<String> missingSupertypes,
    Types typeUtils) {
    if (!missingSupertypes.isEmpty()) {
      for (TypeMirror supertype : supertypes) {
        missingSupertypes.remove(VerifierUtils.getClassName(supertype));
        missingSupertypes(typeUtils.directSupertypes(supertype), missingSupertypes, typeUtils);
      }
    }
    return missingSupertypes;
  }
}
