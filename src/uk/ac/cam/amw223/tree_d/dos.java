package uk.ac.cam.amw223.tree_d;

import org.joml.Vector3i;

public class dos extends piece {

  public dos(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public boolean verifyMove(position start, position end) {
    Vector3i d = new Vector3i();
    end.asVector().sub(start.asVector(), d);

    // move must not be zero and must have equal components
    d.absolute();
    return d.x != 0 && d.x == d.y && d.y == d.z;
  }
}
