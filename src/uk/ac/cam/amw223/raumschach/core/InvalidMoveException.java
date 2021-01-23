package uk.ac.cam.amw223.raumschach.core;

import java.io.Serializable;

public class InvalidMoveException extends Exception implements Serializable {

  private static final long serialVersionUID = 1L;

  public final boolean startValid;
  public final boolean endValid;
  private final String message;

  public InvalidMoveException(boolean start, boolean end, String message) {
    startValid = start;
    endValid = end;
    this.message = message;
  }

  public boolean isStartValid() {
    return startValid;
  }

  public boolean isEndValid() {
    return endValid;
  }

  public String reason() {
    return message;
  }

}
