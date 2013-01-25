package com.overstock.constraint.verifier;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import com.overstock.constraint.RequireConstructors;
import com.overstock.constraint.processor.Constraints;

public class RequireConstructorsVerifier extends AbstractVerifier {

  public RequireConstructorsVerifier(ProcessingEnvironment processingEnv) {
    super(processingEnv);
  }

  public void verify(Element element, AnnotationMirror annotation, Constraints constraints) {
    AnnotationMirror requireConstructors = constraints.get(RequireConstructors.class);
    if (requireConstructors == null) {
      return;
    }

    AnnotationValue value = requireConstructors.getElementValues().values().iterator().next();
    @SuppressWarnings("unchecked")
    List<AnnotationMirror> requiredConstructorValues = (List<AnnotationMirror>) value.getValue();
    for (AnnotationMirror requiredConstructorValue : requiredConstructorValues) {
      @SuppressWarnings("unchecked")
      List<AnnotationValue> argumentList = (List<AnnotationValue>) requiredConstructorValue.getElementValues().values()
        .iterator().next().getValue();
      if (!hasConstructor(element, argumentList)) {
        raiseAnnotatedClassMessage(
          Diagnostic.Kind.ERROR,
          element,
          annotation,
          " but does not have a constructor with " + argumentLabel(argumentList),
          annotation.getAnnotationType());
      }
    }
  }

  private String argumentLabel(List<?> argumentList) {
    return argumentList.isEmpty() ? "no arguments" : "arguments " + argumentList.toString();
  }

  private static boolean hasConstructor(Element element, List<AnnotationValue> argumentTypes) {
    for (ExecutableElement constructorElement : ElementFilter.constructorsIn(element.getEnclosedElements())) {
      if (argumentTypesMatch(constructorElement.getParameters(), argumentTypes)) {
        return true;
      }
    }
    return false;
  }

  private static boolean argumentTypesMatch(List<? extends VariableElement> parameters, List<AnnotationValue> expected) {
    if (parameters.size() != expected.size()) {
      return false;
    }
    //FIXME compare parameter types
    return true;
  }

}
