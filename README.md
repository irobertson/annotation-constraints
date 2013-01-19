Annotation Constraints
======================

Annotation Constraints allows you to specify constraints on annotated types which are verified at compile-time. For
example, the following `Model` annotation can only be placed on a class which extends `AbstractModel` and has a no-arg constructor.

```java
@RequireSupertypes(AbstractModel.class)
@RequireConstructors(@RequiredConstructor({}))
@Target(ElementType.TYPE)
public @interface Model {
}
```

These constraints are violated at compile-time when annotation-constraints is on the compiler's classpath. TODO: Maven pom snippet
If you violate any of the constraints, you'll receive an error from the compiler.

```java
@Model
public class Person {

  private final String name;

  public Person(String name) {
    this.name = name;
  }
}
```

Results in
```
TODO: compiler error for the above
```