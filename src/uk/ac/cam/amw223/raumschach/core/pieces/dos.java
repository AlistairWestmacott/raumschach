package uk.ac.cam.amw223.raumschach.core.pieces;

import org.joml.Vector3i;
import uk.ac.cam.amw223.raumschach.core.position;

import java.io.Serializable;

public class dos extends piece implements Serializable {

  private static final long serialVersionUID = 1L;

  public dos(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public boolean verifyMove(position start, position end) {
    Vector3i d = new Vector3i();
    end.asVector().sub(start.asVector(), d);

    // move must not be zero and must have equal components
    d.absolute();
    return (d.x != 0) && (d.x == d.y) && (d.y == d.z);
  }
}
