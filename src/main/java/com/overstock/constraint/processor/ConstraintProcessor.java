package com.overstock.constraint.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.overstock.constraint.Constraint;
import com.overstock.constraint.provider.ProvidesConstraintsFor;
import com.overstock.constraint.verifier.Verifier;

@SupportedAnnotationTypes("*")
public class ConstraintProcessor extends AbstractProcessor {

  private Map<DeclaredType, Verifier> verifiers = new HashMap<DeclaredType, Verifier>();

  private ProvidedConstraints providedConstraints;

  private ClassLoader classLoader;

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    classLoader = getClass().getClassLoader();

    providedConstraints = ProvidedConstraints.from(findConstraintProviders(classLoader), processingEnv);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    ProvidedConstraints roundConstraints = ProvidedConstraints.from(
      roundEnv.getElementsAnnotatedWith(ProvidesConstraintsFor.class), processingEnv);
    providedConstraints = providedConstraints.combineWith(roundConstraints);

    Elements elementUtils = processingEnv.getElementUtils();
    for (Element element : elementsToProcess(roundEnv)) {
      for (AnnotationMirror annotationMirror : elementUtils.getAllAnnotationMirrors(element)) {
        Constraints constraints = Constraints.on(annotationMirror, providedConstraints, processingEnv);
        for (ConstraintMirror constraint : constraints) {
          DeclaredType constraintType = constraint.getAnnotation().getAnnotationType();
          Verifier verifier = verifiers.get(constraintType);
          if (verifier == null) {
            verifier = initializeVerifier(constraint, element, annotationMirror);
            if (verifier == null) {
              processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                "Verifier for " + constraintType + " could not be constructed, which is required for "
                  + annotationMirror.getAnnotationType(), element, annotationMirror);
              continue;
            }
            else {
              verifiers.put(constraintType, verifier);
            }
          }
          verifier.verify(element, annotationMirror, constraint);
        }
      }
    }
    return false;
  }

  private Verifier initializeVerifier(ConstraintMirror constraint, Element element, AnnotationMirror constrained) {
    TypeMirror constraintMirror = MirrorUtils.getTypeMirror(Constraint.class, processingEnv.getElementUtils());
    Types typeUtils = processingEnv.getTypeUtils();
    List<? extends AnnotationMirror> annotationMirrors = constraint.getAnnotation().getAnnotationType().asElement()
      .getAnnotationMirrors();
    for (AnnotationMirror annotationMirror : annotationMirrors) {
      if (typeUtils.isSameType(constraintMirror, annotationMirror.getAnnotationType())) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
        for (ExecutableElement executableElement : elementValues.keySet()) {
          if (executableElement.getSimpleName().contentEquals("verifiedBy")) {
            TypeMirror verifierType = (TypeMirror) elementValues.get(executableElement).getValue();
            Verifier verifier = newInstance(verifierType, element, constrained);
            verifier.init(processingEnv);
            return verifier;
          }
        }

      }
    }
    return null;
  }

  private Verifier newInstance(TypeMirror verifierType, Element element, AnnotationMirror constrained) {
    try {
      return (Verifier) Class.forName(verifierType.toString(), true, classLoader).newInstance();
    }
    catch (Exception e) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error instantiating verifier " + verifierType
        + " which is required for " + constrained.getAnnotationType() + " due to an exception: " + e.getMessage(),
        element, constrained);
      return null;
    }
  }

  private Set<? extends Element> elementsToProcess(RoundEnvironment roundEnv) {
    Set<? extends Element> rootElements = roundEnv.getRootElements();
    Set<Element> elements = new HashSet<Element>(rootElements.size());
    for (Element element : rootElements) {
      addEnclosedTypes(elements, element);
    }
    return elements;
  }

  private Collection<? extends Element> addEnclosedTypes(Collection<Element> elements, Element element) {
    elements.add(element);
    if (ElementKind.PACKAGE != element.getKind()) {
      for (Element enclosed : element.getEnclosedElements()) {
        addEnclosedTypes(elements, enclosed);
      }
    }
    return elements;
  }

  private Set<? extends Element> findConstraintProviders(ClassLoader classLoader) {
    Set<Element> constraintProviders = new HashSet<Element>();
    try {
      Enumeration<URL> resources = classLoader.getResources(ProvidesConstraintsFor.PROVIDERS_FILE);
      while (resources.hasMoreElements()) {
        URL url = resources.nextElement();
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        try {
          String line;
          while ((line = reader.readLine()) != null) {
            TypeElement providerElement = processingEnv.getElementUtils().getTypeElement(line);
            if (providerElement == null) {
              processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                "Could not find element for constraint provider " + line + " which was declared in " + url);
            }
            else {
              constraintProviders.add(providerElement);
            }
          }
        }
        finally {
          reader.close();
        }
      }
    }
    catch (IOException e) {
      throw new RuntimeException("Could not read constraint providers file", e);
    }
    return constraintProviders;
  }

}
