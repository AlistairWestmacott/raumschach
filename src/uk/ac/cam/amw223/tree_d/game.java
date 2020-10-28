package uk.ac.cam.amw223.tree_d;

public class game {
  private board b;

  private boolean turn = true; // start with white

  private player p1;
  private player p2;

  public game(player p1, player p2) {
    b = new board();
    b.linkBoardToPieces();

    this.p1 = p1;
    this.p2 = p2;

    p1.linkBoard(b);
    p2.linkBoard(b);
  }

  public void playGame() {
    while (b.hasKing(true) && b.hasKing(false)) {
      if (turn)
        p1.takeTurn();
      else
        p2.takeTurn();
      turn = !turn;
    }
    String winner = b.hasKing(false) ? "Black" : "White";
    System.out.println("Congratulation " + winner + ", you win!");
  }

  public static void main(String[] args) {
    player p1 = new player(true);
    player p2 = new player(false);
    game g = new game(p1, p2);
    g.playGame();
  }
}
