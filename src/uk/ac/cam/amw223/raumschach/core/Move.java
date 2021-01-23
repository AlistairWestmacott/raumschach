package uk.ac.cam.amw223.raumschach.core;

import java.io.Serializable;

public class Move implements Serializable {

  private static final long serialVersionUID = 1L;

  public final position start;
  public final position end;

  public Move(position start, position end) {
    this.start = start;
    this.end = end;
  }

  @Override
  public String toString() {
    return start.toString() + " -> " + end.toString();
  }
}
