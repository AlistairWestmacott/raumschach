package uk.ac.cam.amw223.raumschach.online;

import uk.ac.cam.amw223.raumschach.core.InvalidMoveException;
import uk.ac.cam.amw223.raumschach.core.Move;
import uk.ac.cam.amw223.raumschach.core.game;
import uk.ac.cam.amw223.raumschach.online.messages.GameEndMessage;
import uk.ac.cam.amw223.raumschach.online.messages.Message;
import uk.ac.cam.amw223.raumschach.online.messages.MoveResultMessage;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.yield;

public class OnlineGameServer extends game {

  private boolean finished = false;

  private ClientHandler ch1;
  private ClientHandler ch2;

  public OnlineGameServer(ClientHandler p1, ClientHandler p2) {
    super(p1, p2);
    this.ch1 = p1;
    this.ch2 = p2;

    Thread checkClientsConnected = new Thread (() -> {
      while (true) {
        if (ch1.isConnected() && ch2.isConnected()) {
          yield();
        } else {
          finished = true;
          if (ch1.isConnected()) {
            ch1.sendMessage(new GameEndMessage(true));
          } else if (ch2.isConnected()) {
            ch2.sendMessage(new GameEndMessage(false));
          } // otherwise both are gone and the game doesn't matter anymore
          return;
        }
      }
    });
    checkClientsConnected.setDaemon(true);
    checkClientsConnected.start();
  }

  @Override
  public void playGame() {
    Move moveToMake;
    Message msg;
    boolean hasGameEnded = false;

    while (!finished) {
      if (turn) {
        moveToMake = ch1.getMove();
      } else {
        moveToMake = ch2.getMove();
      }
      try {

        b.makeMove(moveToMake, turn);

        // todo: replace this with a check for checkmate (could make determining winner harder)
        hasGameEnded = !b.hasKing(true) || !b.hasKing(false);
        if (hasGameEnded)
          // a player can only win on their turn
          msg = new GameEndMessage(turn);
        else
          msg = new MoveResultMessage(moveToMake, turn);

      } catch (InvalidMoveException e) {
        msg = new MoveResultMessage(e, turn);
      }
      ch1.sendMessage(msg);
      ch2.sendMessage(msg);
      turn = !turn;

      // need to send game end messages before the server cleans up the game
      finished = hasGameEnded;
    }
  }

  public boolean isFinished() {
    // fairly sure java wouldn't need locks for read only primitive types in classes
    return finished;
  }

  public List<Integer> getRemainingConnectedPlayers() {
    List<Integer> IDs = new ArrayList<>();
    if (ch1.isConnected()) IDs.add(ch1.getID());
    if (ch2.isConnected()) IDs.add(ch2.getID());
    return IDs;
  }
}
