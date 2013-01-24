package com.overstock.constraint.processor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verifyZeroInteractions;

import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ConstraintProcessorTest extends AbstractCompilingTest {

  @Mock
  private Messager messager;

  private Processor wrapped;

  @Test
  public void testProcessNoConstraints() throws Exception {
    String packageName = getClass().getPackage().getName();
    SourceFile[] sourceFiles = { new SourceFile(
      packageName.replaceAll(".", "/") + "/SimpleAnnotated.java",
      "package " + packageName + ";",
      "@NoConstraints(someInt=3)",
      "public class SimpleAnnotated extends java.util.ArrayList {}") };

    assertTrue("Compilation should succeed", compiler.compileWithProcessor(wrapped, sourceFiles));
    verifyZeroInteractions(messager); //no warnings or errors from the annotation processor
  }

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    wrapped = new ProcessorWrapper(new ConstraintProcessor(), messager);
  }

}
