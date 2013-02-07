package example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.overstock.constraint.RequiredConstructor;
import com.overstock.constraint.TargetRequiresConstructors;
import com.overstock.constraint.TargetRequiresSupertypes;

@TargetRequiresSupertypes(AbstractModel.class)
@TargetRequiresConstructors(@RequiredConstructor({}))
@Target(ElementType.TYPE)
public @interface Model {
}
