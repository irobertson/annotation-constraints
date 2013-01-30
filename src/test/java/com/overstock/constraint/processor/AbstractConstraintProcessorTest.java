package com.overstock.constraint.processor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public abstract class AbstractConstraintProcessorTest extends AbstractCompilingTest {
  protected static final String PACKAGE_NAME = "com.overstock.constraint";
  protected static final String PACKAGE_DECLARATION = "package " + PACKAGE_NAME + ";";

  @Mock
  protected Messager messager;

  protected Processor wrapped;

  public AbstractConstraintProcessorTest(CompilerProvider provider) {
    super(provider);
  }

  protected final void compile(SourceFile... sourceFiles) throws Exception {
    assertTrue("Compilation should succeed", compiler.compileWithProcessor(wrapped, sourceFiles));
  }

  protected final void assertCleanCompile(SourceFile... sourceFiles) throws Exception {
    compile(sourceFiles);
    verifyZeroInteractions(messager); //no warnings or errors of the annotation processor
  }

  protected final void verifyPrintMessage(
    Diagnostic.Kind kind, String message, String elementClassName, Class<?> annotationMirrorClass) {
    verifyPrintMessage(kind, message, elementClassName, annotationMirrorClass.getName());
  }

  protected final void verifyPrintMessage(
    Diagnostic.Kind kind, String message, String elementClassName, String annotationMirrorClass) {
    verify(messager).printMessage(
      eq(kind),
      eq(message),
      argThat(Matchers.<Element>hasToString(elementClassName)),
      argThat(Matchers.<AnnotationMirror>hasToString("@" + annotationMirrorClass)));
  }

  @Before
  public void setupConstraintProcessorTest() {
    MockitoAnnotations.initMocks(this);
    wrapped = new ProcessorWrapper(new ConstraintProcessor(), messager);
  }

  protected static String className(String simpleName) {
    return PACKAGE_NAME + "." + simpleName;
  }

  protected static String filePath(String fileName) {
    return PACKAGE_NAME.replaceAll("\\.", "/") + "/" + fileName;
  }

}
