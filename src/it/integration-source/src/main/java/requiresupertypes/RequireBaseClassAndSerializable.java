package requiresupertypes;

import java.io.Serializable;

import org.annotationconstraints.TargetMustHaveSupertypes;

@TargetMustHaveSupertypes({BaseClass.class, Serializable.class})
public @interface RequireBaseClassAndSerializable {
}
