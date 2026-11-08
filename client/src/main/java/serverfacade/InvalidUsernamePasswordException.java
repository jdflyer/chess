package serverfacade;

public class InvalidUsernamePasswordException extends RuntimeException {
  public InvalidUsernamePasswordException(String message) {
    super(message);
  }
}
