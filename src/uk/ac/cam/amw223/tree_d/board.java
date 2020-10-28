package uk.ac.cam.amw223.tree_d;

public class board {

  private static final int BOARD_SIZE = 5;

  private piece[][][] grid = new piece[BOARD_SIZE][BOARD_SIZE][BOARD_SIZE];

  public board() {
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        for (int k = 0; k < BOARD_SIZE; k++) {

          // white pawns
          if (i < 2 && j == 1) {
            grid[i][j][k] = new pawn(true);
          }
          // black pawns
          if (i > 2 && j == 3) {
            grid[i][j][k] = new pawn(false);
          }

          // white rooks
          if (i == 0 && j == 0 && k % 4 == 0) {
            grid[i][j][k] = new rook(true);
          }
          // black rooks
          if (i == 4 && j == 4 && k % 4 == 0) {
            grid[i][j][k] = new rook(false);
          }

          // white knights
          if (i == 0 && j == 0 && k % 2 == 1) {
            grid[i][j][k] = new knight(true);
          }
          // black knights
          if (i == 4 && j == 4 && k % 2 == 1) {
            grid[i][j][k] = new knight(false);
          }

          // white bishops
          if (i == 1 && j == 0 && k % 3 == 0) {
            grid[i][j][k] = new bishop(true);
          }
          // black bishops
          if (i == 3 && j == 4 && k % 3 == 0) {
            grid[i][j][k] = new bishop(false);
          }

          // white doses
          if (i == 1 && j == 0 && k % 3 == 1) {
            grid[i][j][k] = new dos(true);
          }
          // black doses
          if (i == 3 && j == 4 && k % 3 == 1) {
            grid[i][j][k] = new dos(false);
          }
        }
      }
    }

    // black and white kings
    grid[0][0][2] = new king(true);
    grid[4][4][2] = new king(false);
    // black and white queens
    grid[1][0][2] = new queen(true);
    grid[3][4][2] = new queen(false);


    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        for (int k = 0; k < BOARD_SIZE; k++) {
          grid[i][j][k].linkBoard(this);
        }
      }
    }
  }

  @Override
  public String toString() {
    String result = "";
    for (int i = 0; i < BOARD_SIZE; i++) {
       result += "Level " + (char)((int)'A' + i) + "\n\n";
       result += "  a    b    c    d    e  \n";
      for (int j = 0; j < BOARD_SIZE; j++) {
        result += "|----|----|----|----|----|\n";
        for (int k = 0; k < BOARD_SIZE; k++) {
          result += "| ";
          if (grid[i][j][k] == null)
            result += "  ";
          else
            result += grid[i][j][k];
          result += " ";
        }
        result += "| " + j + "\n";
      }
      result += "|----|----|----|----|----|\n\n";
    }
    return result;
  }

  public piece getPiece(position pos) {
    return grid[pos.getI()][pos.getJ()][pos.getK()];
  }

  public void makeMove(position start, position end) throws InvalidMoveException {
    if (getPiece(start) == null)
      throw new InvalidMoveException(false, true);
    if (getPiece(start).isWhite() == getPiece(end).isWhite())
      throw new InvalidMoveException(true, false);
    if (!getPiece(start).verifyMove(start, end))
      throw new InvalidMoveException(true, false);
  }
}
