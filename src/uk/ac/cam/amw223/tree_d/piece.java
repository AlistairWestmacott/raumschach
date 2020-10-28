package uk.ac.cam.amw223.tree_d;

public class piece {
  private pieceType type;

  private boolean isWhite;

  public piece(pieceType type, boolean isWhite) {
    this.type = type;
    this.isWhite = isWhite;
  }

  @Override
  public String toString() {
    String result = "";
    if (type == pieceType.KNIGHT)
      result += "N";
    else
      result += type.toString().charAt(0);
    if (isWhite)
      result += "W";
    else
      result += "B";
    return result;
  }
}
