package uk.ac.cam.amw223.tree_d;

public class piece {

  private boolean isWhite;
  protected board b;

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

  @Override
  public String toString() {
    return (isWhite ? "W" : "B") + getName().charAt(0);
  }
}
