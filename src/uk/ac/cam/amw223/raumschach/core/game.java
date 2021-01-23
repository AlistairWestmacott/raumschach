package uk.ac.cam.amw223.raumschach.core;

public abstract class game {
  protected board b;

  protected boolean turn = true; // start with white

  protected player p1;
  protected player p2;

  public game(player p1, player p2) {
    b = new board();

    this.p1 = p1;
    this.p2 = p2;
  }

  public abstract void playGame();

}
