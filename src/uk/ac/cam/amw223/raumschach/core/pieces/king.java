package uk.ac.cam.amw223.raumschach.core.pieces;

import org.joml.Vector3i;
import uk.ac.cam.amw223.raumschach.core.position;

import java.io.Serializable;

public class king extends piece implements Serializable {

  private static final long serialVersionUID = 1L;

  public king(boolean isWhite) {
    super(isWhite);
  }

  @Override
  public boolean verifyMove(position start, position end) {
    Vector3i d = new Vector3i();
    end.asVector().sub(start.asVector(), d);

    // move must not be zero and must have all components x_i such that |x_i| <= 1
    //  the longest move it can make is through a vertex which will have length sqrt(3)
    //  this is shorter than moving through 2 faces (the shortest illegal move)
    double len = d.length();
    return (len > 0) && (len < 2);
  }
}
