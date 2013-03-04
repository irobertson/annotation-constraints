package example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.overstock.constraint.RequiredConstructor;
import com.overstock.constraint.TargetMustHaveConstructors;
import com.overstock.constraint.TargetMustHaveSupertypes;

@TargetMustHaveSupertypes(AbstractModel.class)
@TargetMustHaveConstructors(@RequiredConstructor({}))
@Target(ElementType.TYPE)
public @interface Model {
}
