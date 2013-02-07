package requireconstructors;

@RequireStringLongIntArrayConstructor
public class RequireStringLongIntArrayConstructorFail {

  public RequireStringLongIntArrayConstructorFail(String s, long l, long[] array) {}

}
