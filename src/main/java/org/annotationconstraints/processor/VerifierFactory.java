package org.annotationconstraints.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import org.annotationconstraints.Constraint;
import org.annotationconstraints.verifier.Verifier;

class VerifierFactory {
  private final Map<DeclaredType, Verifier> verifiers = new HashMap<DeclaredType, Verifier>();
  private final ProcessingEnvironment processingEnv;
  private final ClassLoader classLoader;

  public VerifierFactory(ProcessingEnvironment processingEnv, ClassLoader classLoader) {
    this.processingEnv = processingEnv;
    this.classLoader = classLoader;
  }

  public Verifier createVerifier(ConstraintMirror constraint)
    throws VerifierInstantiationException, VerifierNotFoundException {
    DeclaredType constraintType = constraint.getAnnotation().getAnnotationType();
    Verifier verifier = verifiers.get(constraintType);
    if (verifier != null) {
      return verifier; //used cached value
    }

    TypeMirror constraintMirror = MirrorUtils.getTypeMirror(Constraint.class, processingEnv.getElementUtils());
    Types typeUtils = processingEnv.getTypeUtils();
    List<? extends AnnotationMirror> annotationMirrors = constraint.getAnnotation().getAnnotationType().asElement()
      .getAnnotationMirrors();
    for (AnnotationMirror annotationMirror : annotationMirrors) {
      if (typeUtils.isSameType(constraintMirror, annotationMirror.getAnnotationType())) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
        for (ExecutableElement executableElement : elementValues.keySet()) {
          if (executableElement.getSimpleName().contentEquals("verifiedBy")) {
            verifier = createVerifier((TypeMirror) elementValues.get(executableElement).getValue());
            verifiers.put(constraintType, verifier); //cache
            return verifier;
          }
        }
      }
    }
    throw new VerifierNotFoundException();
  }

  private Verifier createVerifier(TypeMirror verifierType) throws VerifierInstantiationException {
    Verifier verifier;
    try {
      verifier = newInstance(verifierType);
    }
    catch (Exception e) {
      throw new VerifierInstantiationException(verifierType, e);
    }
    verifier.init(processingEnv);
    return verifier;
  }

  private Verifier newInstance(TypeMirror verifierType) throws Exception {
    TypeElement element = processingEnv.getElementUtils().getTypeElement(verifierType.toString());
    String className = processingEnv.getElementUtils().getBinaryName(element).toString();
    return (Verifier) Class.forName(className, true, classLoader).newInstance();
  }

  public static class VerifierNotFoundException extends Exception {}

  public static class VerifierInstantiationException extends Exception {
    private final TypeMirror verifierType;

    public VerifierInstantiationException(TypeMirror verifierType, Throwable cause) {
      super(cause);
      this.verifierType = verifierType;
    }

    public TypeMirror getVerifierType() {
      return verifierType;
    }
  }
}
