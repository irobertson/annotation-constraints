package requiresupertypes;

import java.io.Serializable;

@RequireBaseClassAndSerializable
public class RequireSupertypesPass extends BaseClass implements Serializable {
  public static final long serialVersionUID = 1;
}
