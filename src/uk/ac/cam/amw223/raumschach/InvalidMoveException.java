package uk.ac.cam.amw223.raumschach;

public class InvalidMoveException extends Exception {

  boolean startValid;
  boolean endValid;

  public InvalidMoveException(boolean start, boolean end) {
    startValid = start;
    endValid = end;
  }

  public boolean isStartValid() {
    return startValid;
  }

  public boolean isEndValid() {
    return endValid;
  }

}
