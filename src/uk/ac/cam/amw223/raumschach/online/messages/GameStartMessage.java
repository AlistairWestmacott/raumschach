package uk.ac.cam.amw223.raumschach.online.messages;

import java.io.Serializable;

public class GameStartMessage extends Message implements Serializable {

  private static final long serialVersionUID = 1L;

  private boolean isWhite;

  public GameStartMessage(boolean isWhite) {
    this.isWhite = isWhite;
  }

  public boolean isWhite() {
    return isWhite;
  }

  @Override
  public String toString() {
    return "Message: game starting, you are " + (isWhite? "white" : "black");
  }
}
