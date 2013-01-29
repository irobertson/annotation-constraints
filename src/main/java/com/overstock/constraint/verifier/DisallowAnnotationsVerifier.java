package com.overstock.constraint.verifier;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import com.overstock.constraint.DisallowAnnotations;
import com.overstock.constraint.processor.Constraints;

/**
 * A verifier for {@link DisallowAnnotations}.
 */
public class DisallowAnnotationsVerifier extends AbstractVerifier {

  public DisallowAnnotationsVerifier(ProcessingEnvironment processingEnv) {
    super(processingEnv);
  }

  public void verify(Element element, AnnotationMirror annotation, Constraints constraints) {
    AnnotationMirror disallowAnnotations = constraints.get(DisallowAnnotations.class);
    if (disallowAnnotations == null) {
      return;
    }

    Set<String> disallowedAnnotations = new HashSet<String>(getValuesAsClassNames(disallowAnnotations));
    if (disallowedAnnotations.isEmpty()) {
      return;
    }

    Set<String> presentAndDisallowed = new HashSet<String>();
    for (AnnotationMirror annotated : element.getAnnotationMirrors()) {
      String className = VerifierUtils.getClassName(annotated);
      if (disallowedAnnotations.contains(className)) {
        presentAndDisallowed.add(className);
      }
    }

    for (String missingRequiredAnnotationType : presentAndDisallowed) {
      raiseAnnotatedClassMessage(
        Diagnostic.Kind.ERROR,
        element,
        annotation,
        " and with @" + missingRequiredAnnotationType);
    }
  }

  private Set<String> getValuesAsClassNames(AnnotationMirror requireAnnotations) {
    return VerifierUtils.getClassNames(VerifierUtils.getArrayValues(requireAnnotations));
  }
}
