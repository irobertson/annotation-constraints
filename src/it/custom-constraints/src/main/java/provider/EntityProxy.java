package provider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.overstock.constraint.RequiredConstructor;
import com.overstock.constraint.TargetRequiresConstructors;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@TargetRequiresConstructors(@RequiredConstructor({}))
public @interface EntityProxy {
}
