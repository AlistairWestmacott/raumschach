package uk.ac.cam.amw223.raumschach.online;

import uk.ac.cam.amw223.raumschach.core.Move;
import uk.ac.cam.amw223.raumschach.core.player;
import uk.ac.cam.amw223.raumschach.core.position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.MalformedParametersException;

public class OnlinePlayerClient extends player {

  public OnlinePlayerClient(boolean isWhite) {
    super(isWhite);
    System.out.println("You are " + (isWhite? "white" : "black"));
  }

  public void gameSetup(boolean isWhite) {

  }

  @Override
  public Move getMove() {
    String start, end;
    position startPos, endPos;

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    while (true) {

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

      return new Move(startPos, endPos);
    }
  }

}
