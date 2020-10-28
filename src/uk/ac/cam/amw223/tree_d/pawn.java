package uk.ac.cam.amw223.tree_d;

import org.joml.Vector3i;

public class pawn extends piece {

  private static final String name = "pawn";

  public pawn(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public boolean verifyMove(position start, position end) {
    Vector3i d = new Vector3i();
    end.asVector().sub(start.asVector(), d);

    // move validity is dependant on whether the end position has a piece

    int dir = isWhite() ? 1 : -1;

    final boolean correctDirection = (d.x == dir && d.z == 0) || (d.x == 0 && d.z == dir);
    final boolean capturing = (Math.abs(d.y) == 1);
    final boolean nonCapturing = d.y == 0;

    return ((b.getPiece(end) == null) ? nonCapturing : capturing) && correctDirection;
  }

  @Override
  public String toString() {
    return name.charAt(0) + super.toString();
  }
}
