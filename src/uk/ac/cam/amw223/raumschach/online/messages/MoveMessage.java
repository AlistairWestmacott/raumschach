package uk.ac.cam.amw223.raumschach.online.messages;

import uk.ac.cam.amw223.raumschach.core.Move;

import java.io.Serializable;

public class MoveMessage extends Message implements Serializable {

  private static final long serialVersionUID = 1L;

  private Move move;

  public MoveMessage(Move move) {
    this.move = move;
  }

  public Move getMove() {
    return move;
  }

  @Override
  public String toString() {
    return "Message: " + move.toString();
  }
}
