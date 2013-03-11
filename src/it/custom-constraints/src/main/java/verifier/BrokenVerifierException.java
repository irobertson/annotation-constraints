package verifier;

public class BrokenVerifierException extends RuntimeException {
  public BrokenVerifierException(String message) {
    super(message);
  }
}
