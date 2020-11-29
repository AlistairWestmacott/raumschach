package uk.ac.cam.amw223.raumschach;

import uk.ac.cam.amw223.tinyPlanet.gameObject;

public abstract class piece {

  private boolean isWhite;
  protected board b;
  private gameObject go;

  public piece(boolean isWhite) {
    this.isWhite = isWhite;
  }

  public void linkBoard(board b) {
    this.b = b;
  }

  public boolean isWhite() {
    return isWhite;
  }

  public boolean verifyMove(position start, position end) {
    return false;
  }

  public String getName() {
    return this.getClass().getSimpleName();
  }

  public gameObject getGameObject() {
    return go;
  }

  public void initialiseGraphics() {
    go = new gameObject(getName(), (isWhite ? "white" : "black"));
  }

  @Override
  public String toString() {
    return (isWhite ? "W" : "B") + getName().charAt(0);
  }
}
