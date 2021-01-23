package uk.ac.cam.amw223.raumschach.core;

public class LocalGame extends game {

  public LocalGame(player p1, player p2) {
    super(p1, p2);
  }

  @Override
  public void playGame() {
    Move moveToMake;
    boolean valid = false;

    // todo: replace this with a check for checkmate
    while (b.hasKing(true) && b.hasKing(false)) {
      System.out.println(b);
      while (!valid) {
        if (turn)
          moveToMake = p1.getMove();
        else
          moveToMake = p2.getMove();
        try {
          b.makeMove(moveToMake, turn);
          valid = true;
        } catch (InvalidMoveException e) {
          System.err.println(e.reason());
        }
      }
      turn = !turn;
    }
    String winner = b.hasKing(false) ? "Black" : "White";
    System.out.println("Congratulation " + winner + ", you win!");
  }

}
