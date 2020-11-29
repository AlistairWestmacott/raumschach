package uk.ac.cam.amw223.raumschach;

import org.joml.Vector3i;

public class queen extends piece {

  public queen(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public boolean verifyMove(position start, position end) {
    Vector3i d = new Vector3i();
    end.asVector().sub(start.asVector(), d);

    // Any component of the move can be 0 (but not all of them)
    //  all non-zero components must be equal
    if (d.length() == 0)
      return false;

    int l = Math.max(d.x, Math.max(d.y, d.z));
    return
            d.x == 0 || d.x == l &&
            d.y == 0 || d.y == l &&
            d.z == 0 || d.z == l;
  }
}
