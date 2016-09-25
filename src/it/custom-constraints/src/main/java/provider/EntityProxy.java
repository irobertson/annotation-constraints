package provider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.Entity;

import org.annotationconstraints.Constructor;
import org.annotationconstraints.TargetMustHaveConstructors;
import org.annotationconstraints.provider.ProvidesConstraintsFor;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@TargetMustHaveConstructors(@Constructor({}))
@ProvidesConstraintsFor(Entity.class)
public @interface EntityProxy {
}
