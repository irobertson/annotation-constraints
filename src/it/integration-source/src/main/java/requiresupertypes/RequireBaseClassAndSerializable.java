package requiresupertypes;

import java.io.Serializable;

import com.overstock.constraint.TargetRequiresSupertypes;

@TargetRequiresSupertypes({BaseClass.class, Serializable.class})
public @interface RequireBaseClassAndSerializable {
}
