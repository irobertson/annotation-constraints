package provider;

import javax.persistence.Entity;

import com.overstock.constraint.provider.ConstraintsFor;
import com.overstock.constraint.provider.ConstraintProvider;

@ConstraintsFor(annotation = Entity.class, canBeFoundOn = EntityProxy.class)
public class ExternalProvider implements ConstraintProvider {
}
