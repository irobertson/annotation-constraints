package provider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.annotationconstraints.TargetMustBeAnnotatedWith;
import org.annotationconstraints.provider.ProvidesConstraintsFor;

@Target({})
@TargetMustBeAnnotatedWith(Entity.class)
@ProvidesConstraintsFor(Table.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableProxy {
}
