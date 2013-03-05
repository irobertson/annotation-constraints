package provider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.Path;

import com.overstock.constraint.provider.ProvidesConstraintsFor;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@ProvidesConstraintsFor(Path.class)
@ReservedPaths("/health")
public @interface PathConstraintProvider {
}
