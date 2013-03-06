package provider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.Entity;

import com.overstock.constraint.Constructor;
import com.overstock.constraint.TargetMustHaveConstructors;
import com.overstock.constraint.provider.ProvidesConstraintsFor;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@TargetMustHaveConstructors(@Constructor({}))
@ProvidesConstraintsFor(Entity.class)
public @interface EntityProxy {
}
