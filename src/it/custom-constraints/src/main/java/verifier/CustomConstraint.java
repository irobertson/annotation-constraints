package verifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.overstock.constraint.Constraint;

@Constraint(verifiedBy = CustomVerifier.class)
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
public @interface CustomConstraint {
}
