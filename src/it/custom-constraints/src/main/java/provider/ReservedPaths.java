package provider;

import java.lang.String;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.overstock.constraint.Constraint;

import verifier.ReservedPathVerifier;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@Constraint(verifiedBy = ReservedPathVerifier.class)
public @interface ReservedPaths {

  public String[] value();

}
