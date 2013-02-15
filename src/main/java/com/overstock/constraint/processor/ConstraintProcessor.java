package com.overstock.constraint.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import com.overstock.constraint.provider.ProvidesConstraintsFor;
import com.overstock.constraint.verifier.Verifier;

@SupportedAnnotationTypes("*")
public class ConstraintProcessor extends AbstractProcessor {

  private Iterable<Verifier> verifiers;

  private ProvidedConstraints providedConstraints;

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    ClassLoader classLoader = getClass().getClassLoader();
    verifiers = ServiceLoader.load(Verifier.class, classLoader);
    for (Verifier verifier : verifiers) {
      verifier.init(processingEnv);
    }

    providedConstraints = ProvidedConstraints.from(findConstraintProviders(classLoader), processingEnv);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    ProvidedConstraints roundConstraints = ProvidedConstraints.from(
      roundEnv.getElementsAnnotatedWith(ProvidesConstraintsFor.class), processingEnv);
    providedConstraints = providedConstraints.combineWith(roundConstraints);

    Elements elementUtils = processingEnv.getElementUtils();
    for (Element element : roundEnv.getRootElements()) {
      for (AnnotationMirror annotationMirror : elementUtils.getAllAnnotationMirrors(element)) {
        Constraints constraints = Constraints.on(annotationMirror, providedConstraints, processingEnv);
        if (!constraints.isEmpty()) {
          for (Verifier verifier : verifiers) {
            verifier.verify(element, annotationMirror, constraints);
          }
        }
      }
    }

    return false;
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
            if (providerElement != null) {
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
