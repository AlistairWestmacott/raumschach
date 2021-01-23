package uk.ac.cam.amw223.raumschach.core.pieces;

import org.joml.Vector3i;
import uk.ac.cam.amw223.raumschach.core.board;
import uk.ac.cam.amw223.raumschach.core.position;

import java.io.Serializable;

public class pawn extends piece implements Serializable {

  private static final long serialVersionUID = 1L;

  private board b;

  public pawn(boolean isWhite, board board) {
    super(isWhite);
    this.b = board;
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
