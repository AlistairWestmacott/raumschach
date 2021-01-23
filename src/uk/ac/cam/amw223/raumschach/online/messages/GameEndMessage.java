package uk.ac.cam.amw223.raumschach.online.messages;

public class GameEndMessage extends Message {

  private boolean winner;

  public GameEndMessage(boolean winner) {
    this.winner = winner;
  }

  public boolean getWinner() {
    return winner;
  }

  @Override
  public String toString() {
    return "Message: The game has finished, the winner is " + (winner? "white" : "black");
  }
}
