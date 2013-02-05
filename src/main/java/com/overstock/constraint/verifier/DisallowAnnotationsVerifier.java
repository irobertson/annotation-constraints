package com.overstock.constraint.verifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import com.overstock.constraint.TargetDisallowsAnnotations;
import com.overstock.constraint.processor.Constraints;

/**
 * A verifier for {@link com.overstock.constraint.TargetDisallowsAnnotations}.
 */
public class DisallowAnnotationsVerifier extends AbstractVerifier {

  public void verify(Element element, AnnotationMirror annotation, Constraints constraints) {
    AnnotationMirror disallowAnnotations = constraints.get(TargetDisallowsAnnotations.class);
    if (disallowAnnotations == null) {
      return;
    }

    Collection<String> disallowedAnnotations = VerifierUtils.getValuesAsClassNames(disallowAnnotations);
    if (disallowedAnnotations.isEmpty()) {
      return;
    }

    List<String> presentAndDisallowed = new ArrayList<String>();
    for (AnnotationMirror annotated : element.getAnnotationMirrors()) {
      String className = VerifierUtils.getClassName(annotated);
      if (disallowedAnnotations.contains(className)) {
        presentAndDisallowed.add(className);
      }
    }

    for (String presentAndDisallowedAnnotationType : presentAndDisallowed) {
      raiseAnnotatedClassMessage(
        Diagnostic.Kind.ERROR,
        element,
        annotation,
        " which is not allowed with @" + presentAndDisallowedAnnotationType);
    }
  }
}
