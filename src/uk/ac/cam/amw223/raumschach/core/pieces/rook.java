package uk.ac.cam.amw223.raumschach.core.pieces;

import org.joml.Vector3i;
import uk.ac.cam.amw223.raumschach.core.position;

import java.io.Serializable;

public class rook extends piece implements Serializable {

  private static final long serialVersionUID = 1L;

  public rook(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public boolean verifyMove(position start, position end) {
    Vector3i d = new Vector3i();
    end.asVector().sub(start.asVector(), d);

    // move must not be zero and must have two zero components and one non-zero component
    boolean a = (d.x == 0) && (d.y == 0) && (d.z != 0);
    boolean b = (d.y == 0) && (d.z == 0) && (d.x != 0);
    boolean c = (d.z == 0) && (d.x == 0) && (d.y != 0);

    // take absolute then product should be 0 and sum = max(x,y,z)

    // must be exactly one of the 3 possibilities
    return a ^ b ^ c;
  }
}
