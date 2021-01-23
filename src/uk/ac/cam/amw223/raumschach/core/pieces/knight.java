package uk.ac.cam.amw223.raumschach.core.pieces;

import org.joml.Vector3i;
import uk.ac.cam.amw223.raumschach.core.position;

import java.io.Serializable;

public class knight extends piece implements Serializable {

  private static final long serialVersionUID = 1L;

  public knight(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public boolean verifyMove(position start, position end) {
    Vector3i d = new Vector3i();
    end.asVector().sub(start.asVector(), d);

    // one component must be 0, one must be 1 and one must be 2
    //  get absolute value, add (1, 1, 1)^T. Then the components must sum to 6 and multiply to 6
    //  this can be simplified to x + y + z == xyz as seen here
    //  https://www.quora.com/What-is-the-way-to-find-three-numbers-whose-product-and-sum-are-equal

    d.absolute();
    d.add(1, 1, 1);
    return (d.x + d.y + d.z) == (d.x * d.y * d.z);
  }

  @Override
  public String toString() {
    return (isWhite() ? "W" : "B") + getName().charAt(1);
  }
}
