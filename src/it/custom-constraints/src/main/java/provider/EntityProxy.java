package provider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.Entity;

import com.overstock.constraint.provider.ProvidesConstraintsFor;
import com.overstock.constraint.RequiredConstructor;
import com.overstock.constraint.TargetRequiresConstructors;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@TargetRequiresConstructors(@RequiredConstructor({}))
@ProvidesConstraintsFor(Entity.class)
public @interface EntityProxy {
}
