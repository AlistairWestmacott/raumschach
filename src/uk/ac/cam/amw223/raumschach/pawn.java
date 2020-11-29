package uk.ac.cam.amw223.raumschach;

import org.joml.Vector3i;

public class pawn extends piece {

  public pawn(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public boolean verifyMove(position start, position end) {
    Vector3i d = new Vector3i();
    end.asVector().sub(start.asVector(), d);

    // move validity is dependant on whether the end position has a piece

    int dir = isWhite() ? 1 : -1;

    final boolean correctDirection = d.z == 0;
    final boolean capturing = d.x == dir && d.y == dir;
    final boolean nonCapturing = (d.x == dir && d.y == 0) || (d.x == 0 && d.y == dir);

    return ((b.getPiece(end) == null) ? nonCapturing : capturing) && correctDirection;
  }
}
