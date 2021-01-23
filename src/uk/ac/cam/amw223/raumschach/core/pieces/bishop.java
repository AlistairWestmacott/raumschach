package uk.ac.cam.amw223.raumschach.core.pieces;

import org.joml.Vector3i;
import uk.ac.cam.amw223.raumschach.core.position;

import java.io.Serializable;

public class bishop extends piece implements Serializable {

  private static final long serialVersionUID = 1L;

  public bishop(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public boolean verifyMove(position start, position end) {
    Vector3i d = new Vector3i();
    end.asVector().sub(start.asVector(), d);

    // move must not be zero and must have one zero components and two equal components
    boolean a = (d.x == 0) && (d.y != 0) && (d.y == d.z);
    boolean b = (d.y == 0) && (d.z != 0) && (d.z == d.x);
    boolean c = (d.z == 0) && (d.x != 0) && (d.x == d.y);

    // could do sum = 2 * max (if absolute taken) && product = 0;

    // must be exactly one of the 3 possibilities
    return a ^ b ^ c;
  }
}
