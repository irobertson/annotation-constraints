package nestedclasses;

import requireconstructors.RequireNoArgConstructor;

import verifier.CustomVerifier.NestedVerifier;

import java.io.Serializable;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import org.annotationconstraints.Constraint;
import org.annotationconstraints.TargetMustHaveSupertypes;
import org.annotationconstraints.processor.ConstraintMirror;
import org.annotationconstraints.verifier.AbstractVerifier;
import org.annotationconstraints.verifier.Verifier;

public class Containing {

  @RequireNoArgConstructor
  public static class NestedClassPass {}

  @RequireNoArgConstructor
  public static class NestedClassFail {
    public NestedClassFail(String s) {}
  }

  @TargetMustHaveSupertypes(Serializable.class)
  private static @interface NestedConstrained {}

  @NestedConstrained
  private static class NestedConstrainedPass implements Serializable {
    public static final long serialVersionUID = 1;
  }

  @NestedConstrained
  private static class NestedConstrainedFail {}

  @NestedVerified
  private static class NestedVerifierFail {}

  @Constraint(verifiedBy = NestedVerifier.class)
  private static @interface NestedConstraint {}

  @NestedConstraint
  private static @interface NestedVerified {}

}