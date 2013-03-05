package com.overstock.constraint.verifier;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * A verifier for {@link com.overstock.constraint.TargetMustHaveConstructors}.
 */
public class RequireConstructorsVerifier implements Verifier {

  private Types typeUtils;

  @Override
  public void verify(VerificationContext context) {
    @SuppressWarnings("unchecked")
    List<AnnotationValue> requiredConstructorValues = (List<AnnotationValue>) context.getConstraint().getAnnotation()
      .getElementValues().values().iterator().next().getValue();
    for (AnnotationValue requiredConstructorValue : requiredConstructorValues) {
      AnnotationMirror requiredConstructorMirror = (AnnotationMirror) requiredConstructorValue.getValue();
    @SuppressWarnings("unchecked")
      List<AnnotationValue> argumentList = (List<AnnotationValue>) requiredConstructorMirror.getElementValues().values()
        .iterator().next().getValue();
      if (!hasConstructor(context.getElement(), argumentList)) {
        MessageBuilder.format(Diagnostic.Kind.ERROR, context)
          .appendText(" but does not have a constructor with " + argumentLabel(argumentList))
          .print();
      }
    }
  }

  @Override
  public void init(ProcessingEnvironment environment) {
    this.typeUtils = environment.getTypeUtils();
  }

  private String argumentLabel(List<AnnotationValue> argumentList) {
    return argumentList.isEmpty() ? "no arguments" : "arguments " + asString(argumentList);
  }

  private String asString(List<AnnotationValue> argumentList) {
    StringBuilder result = new StringBuilder("(");
    boolean first = true;
    for (AnnotationValue annotationValue : argumentList) {
      if (!first) {
        result.append(", ");
      }
      result.append(annotationValue.getValue().toString());
      first = false;
    }
    return result.append(')').toString();
  }

  private boolean hasConstructor(Element element, List<AnnotationValue> argumentTypes) {
    for (ExecutableElement constructorElement : ElementFilter.constructorsIn(element.getEnclosedElements())) {
      if (argumentTypesMatch(constructorElement.getParameters(), argumentTypes)) {
        return true;
      }
    }
    return false;
  }

  private boolean argumentTypesMatch(List<? extends VariableElement> parameters, List<AnnotationValue> expected) {
    if (parameters.size() != expected.size()) {
      return false;
    }
    for (int i = 0; i < parameters.size(); ++i) {
      if (!typeUtils.isSameType(
          typeUtils.erasure(VerifierUtils.asType(parameters.get(i))),
          typeUtils.erasure(VerifierUtils.asType(expected.get(i))))) {
        return false;
      }
    }
    return true;
  }

}
