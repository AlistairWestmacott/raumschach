package uk.ac.cam.amw223.raumschach;

import org.joml.Vector3i;

import java.lang.reflect.MalformedParametersException;

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
    return new position(gridRef.charAt(0) - 'A', gridRef.charAt(2) - '1', gridRef.charAt(1) - 'a');
  }

  public static position fromCoordinates(int x, int y, int z) {
    return new position(x, y, z);
  }

  public static position fromVector(Vector3i v) {
    return new position(v.x, v.y, v.z);
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
