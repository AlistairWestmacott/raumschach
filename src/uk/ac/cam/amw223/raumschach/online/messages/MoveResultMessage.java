package uk.ac.cam.amw223.raumschach.online.messages;

import uk.ac.cam.amw223.raumschach.core.InvalidMoveException;
import uk.ac.cam.amw223.raumschach.core.Move;

import java.io.Serializable;

public class MoveResultMessage extends Message implements Serializable {

  private static final long serialVersionUID = 1L;

  private Move result = null;
  private InvalidMoveException exception;

  private int movesMade;
  private boolean lastMoveColour;

  public MoveResultMessage(Move result, boolean lastMoveColour) {
    this.result = result;
    this.lastMoveColour = lastMoveColour;
  }

  public MoveResultMessage(InvalidMoveException exception, boolean lastMoveColour) {
    this.exception = exception;
    this.lastMoveColour = lastMoveColour;
  }

  public boolean getLastMoveColour() {
    return lastMoveColour;
  }

  public Move getMove() throws InvalidMoveException {
    if (result == null)
      throw exception;
    else
      return result;
  }

  @Override
  public String toString() {
    if (result == null) {
      return "Message: move result error";
    } else {
      return "Message: move result success";
    }
  }
}
