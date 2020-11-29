package uk.ac.cam.amw223.raumschach;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.MalformedParametersException;

public class player {

  boolean isWhite;
  private board b;
  private String colourName;

  public player(boolean isWhite) {
    this.isWhite = isWhite;
    if (isWhite)
      colourName = "White";
    else
      colourName = "Black";
  }

  public void linkBoard(board b) {
    this.b = b;
  }

  public void takeTurn() {
    System.out.println(b);
    boolean valid = false;

    String start, end;
    position startPos, endPos;


    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    while (!valid) {

      // Read player start position
      try {
        System.out.print(colourName + " move from> ");
        start = reader.readLine();
        startPos = position.fromGridRef(start);
      } catch (IOException e) {
        System.err.println("Error reading from input: " + e.getMessage());
        continue;
      } catch (MalformedParametersException e) {
        System.err.println("Incorrectly formatted grid reference.\n" +
                "Examples of valid grid reference: Ab3, Cd5");
        continue;
      }

      // read player end position
      try {
        System.out.print(colourName + " move to> ");
        end = reader.readLine();
        endPos = position.fromGridRef(end);
      } catch (IOException e) {
        System.err.println("Error reading from input: " + e.getMessage());
        continue;
      } catch (MalformedParametersException e) {
        System.err.println("Incorrectly formatted grid reference.\n" +
                "Examples of valid grid reference: Ab3, Cd5");
        continue;
      }

      // validate and make move
      try {
        b.makeMove(startPos, endPos, isWhite);
      } catch (InvalidMoveException e) {
        if (!e.startValid)
          System.err.println("You cannot move that piece.");
        else if (!e.endValid)
          System.err.println("You cannot move to that space on the board.");
        continue;
      }
      valid = true;
    }
  }
}
