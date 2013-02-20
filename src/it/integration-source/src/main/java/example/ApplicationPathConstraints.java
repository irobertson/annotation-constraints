package example;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.overstock.constraint.TargetRequiresSupertypes;
import com.overstock.constraint.provider.ProvidesConstraintsFor;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@ProvidesConstraintsFor(ApplicationPath.class)
@TargetRequiresSupertypes(Application.class)
public @interface ApplicationPathConstraints {
}