package org.annotationconstraints.verifier;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import org.annotationconstraints.processor.ConstraintMirror;

/**
 * A verifier for {@link org.annotationconstraints.TargetMustHaveConstructors}.
 */
public class RequireConstructorsVerifier extends AbstractVerifier {

  @Override
  public void verify(Element element, AnnotationMirror annotationMirror, ConstraintMirror constraint) {
    @SuppressWarnings("unchecked")
    List<AnnotationValue> requiredConstructorValues = (List<AnnotationValue>) constraint.getAnnotation()
      .getElementValues().values().iterator().next().getValue();
    for (AnnotationValue requiredConstructorValue : requiredConstructorValues) {
      AnnotationMirror requiredConstructorMirror = (AnnotationMirror) requiredConstructorValue.getValue();
    @SuppressWarnings("unchecked")
      List<AnnotationValue> argumentList = (List<AnnotationValue>) requiredConstructorMirror.getElementValues().values()
        .iterator().next().getValue();
      if (!hasConstructor(element, argumentList)) {
        messageBuilder(Diagnostic.Kind.ERROR, element, annotationMirror, constraint)
          .appendText(" but does not have a constructor with " + argumentLabel(argumentList))
          .print();
      }
    }
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
      if (!getTypeUtils().isSameType(
          getTypeUtils().erasure(VerifierUtils.asType(parameters.get(i))),
          getTypeUtils().erasure(VerifierUtils.asType(expected.get(i))))) {
        return false;
      }
    }
    return true;
  }

}
