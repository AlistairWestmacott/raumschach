package uk.ac.cam.amw223.raumschach.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.MalformedParametersException;

public class player {

  protected boolean isWhite;
  protected String colourName;

  public player(boolean isWhite) {
    this.isWhite = isWhite;
    if (isWhite)
      colourName = "White";
    else
      colourName = "Black";
  }

  public Move getMove() {
    return new Move(getPosition(true), getPosition(false));
  }

  protected position getPosition(boolean start) {
    String input;
    position pos = null;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    while (pos == null) {
      try {
        System.out.print(colourName + " move " + (start? "from" : "to") + "> ");
        input = reader.readLine();
        pos = position.fromGridRef(input);
      } catch (IOException e) {
        System.err.println("Error reading from input: " + e.getMessage());
      } catch (MalformedParametersException e) {
        System.err.println("Incorrectly formatted grid reference.\n" +
                "Examples of valid grid reference: Ab3, Cd5");
      }
    }
    return pos;
  }
}
