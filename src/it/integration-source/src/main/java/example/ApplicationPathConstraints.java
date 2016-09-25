package example;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.annotationconstraints.TargetMustHaveSupertypes;
import org.annotationconstraints.provider.ProvidesConstraintsFor;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@ProvidesConstraintsFor(ApplicationPath.class)
@TargetMustHaveSupertypes(Application.class)
public @interface ApplicationPathConstraints {
}