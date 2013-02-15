package provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.overstock.constraint.provider.ProvidesConstraintsFor;
import com.overstock.constraint.TargetRequiresAnnotations;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@TargetRequiresAnnotations(Entity.class)
@ProvidesConstraintsFor(Table.class)
public @interface TableProxy {
}
