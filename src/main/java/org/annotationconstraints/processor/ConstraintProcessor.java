package org.annotationconstraints.processor;

import static org.annotationconstraints.processor.VerifierFactory.VerifierInstantiationException;
import static org.annotationconstraints.processor.VerifierFactory.VerifierNotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import org.annotationconstraints.provider.ProvidesConstraintsFor;
import org.annotationconstraints.verifier.Verifier;

@SupportedAnnotationTypes("*")
public class ConstraintProcessor extends AbstractProcessor {
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
    VerifierFactory verifierFactory = new VerifierFactory(processingEnv, classLoader);

    try {
      Elements elementUtils = processingEnv.getElementUtils();
      for (Element element : elementsToProcess(roundEnv)) {
        for (AnnotationMirror constrained : elementUtils.getAllAnnotationMirrors(element)) {
          Constraints constraints = Constraints.on(constrained, providedConstraints, processingEnv);
          for (ConstraintMirror constraint : constraints) {
            DeclaredType constraintType = constraint.getAnnotation().getAnnotationType();
            final Verifier verifier;
            try {
              verifier = verifierFactory.createVerifier(constraint);
            }
            catch (VerifierNotFoundException e) {
              processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                "The verifier for " + constraintType + " was not found, which is required for "
                  + constrained.getAnnotationType(), element, constrained);
              continue;
            }
            catch (VerifierInstantiationException e) {
              processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error instantiating verifier "
                + e.getVerifierType() + " which is required for " + constrained.getAnnotationType()
                + " due to an exception: " + e.getMessage(), element, constrained);
              continue;
            }
            try {
              verifier.verify(element, constrained, constraint);
            }
            catch (Exception e) {
              processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Verifier " + verifier.getClass()
                + " threw an exception: " + new Exception(e).getMessage(), element, constrained);
            }
          }
        }
      }
    }
    finally {
      MirrorUtils.clearCaches(); //in Eclipse we don't want caches hanging around
    }
    return false;
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
