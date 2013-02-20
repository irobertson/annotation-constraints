package requireconstructors;

import java.util.concurrent.Callable;

@RequireCallableConstructor
public class RequireCallableConstructorPass {

  public RequireCallableConstructorPass(Callable<String> callable) {}

}
