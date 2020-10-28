package uk.ac.cam.amw223.tree_d;

import org.joml.Vector3i;

import java.lang.reflect.MalformedParametersException;
import java.util.InvalidPropertiesFormatException;

public class position {
  int i;
  int j;
  int k;

  private position(int i, int j, int k) {
    this.i = i;
    this.j = j;
    this.k = k;
  }

  // If the grid reference is malformed then throws MalformedParametersException
  public static position fromGridRef(String gridRef) throws MalformedParametersException {
    if (!gridRef.matches("[A-E][a-e][1-5]")) {
      throw new MalformedParametersException();
    }
    return new position(gridRef.charAt(0) - 'A', gridRef.charAt(1) - 'a', gridRef.charAt(2) - '1');
  }

  public static position fromCoordinates(int x, int y, int z) {
    return new position(x, y, z);
  }

  public Vector3i asVector() {
    return new Vector3i(i, j, k);
  }

  public int getI() {
    return i;
  }

  public int getJ() {
    return j;
  }

  public int getK() {
    return k;
  }
}
