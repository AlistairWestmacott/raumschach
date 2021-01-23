package uk.ac.cam.amw223.raumschach.core.pieces;

import org.joml.Vector3i;
import uk.ac.cam.amw223.raumschach.core.position;

import java.io.Serializable;

public class queen extends piece implements Serializable {

  private static final long serialVersionUID = 1L;

  public queen(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public boolean verifyMove(position start, position end) {
    Vector3i d = new Vector3i();
    end.asVector().sub(start.asVector(), d);
    d.absolute();

    // Any component of the move can be 0 (but not all of them)
    //  all non-zero components must be equal
    if (d.length() == 0)
      return false;

    int l = Math.max(d.x, Math.max(d.y, d.z));
    return
            (d.x == 0 || d.x == l) &&
            (d.y == 0 || d.y == l) &&
            (d.z == 0 || d.z == l);
  }
}
