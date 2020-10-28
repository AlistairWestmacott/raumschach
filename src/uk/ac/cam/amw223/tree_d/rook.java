package uk.ac.cam.amw223.tree_d;

import org.joml.Vector3i;

public class rook extends piece {

  public rook(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public boolean verifyMove(position start, position end) {
    Vector3i d = new Vector3i();
    end.asVector().sub(start.asVector(), d);

    // move must not be zero and must have two zero components and one non-zero component
    boolean a = d.x == 0 && d.y == 0 && d.z != 0;
    boolean b = d.y == 0 && d.z == 0 && d.x != 0;
    boolean c = d.z == 0 && d.x == 0 && d.y != 0;

    // must be exactly one of the 3 possibilities
    return a ^ b ^ c;
  }
}
