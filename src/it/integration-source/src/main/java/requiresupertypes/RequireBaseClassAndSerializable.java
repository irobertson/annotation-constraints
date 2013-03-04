package requiresupertypes;

import java.io.Serializable;

import com.overstock.constraint.TargetMustHaveSupertypes;

@TargetMustHaveSupertypes({BaseClass.class, Serializable.class})
public @interface RequireBaseClassAndSerializable {
}
