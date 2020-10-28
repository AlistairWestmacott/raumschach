package uk.ac.cam.amw223.tree_d;

public class board {

  private static final int BOARD_SIZE = 5;

  piece[][][] grid = new piece[BOARD_SIZE][BOARD_SIZE][BOARD_SIZE];

  public board() {
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        for (int k = 0; k < BOARD_SIZE; k++) {

          // white pawns
          if (i < 2 && j == 1) {
            grid[i][j][k] = new piece(pieceType.PAWN, true);
          }
          // black pawns
          if (i > 2 && j == 3) {
            grid[i][j][k] = new piece(pieceType.PAWN, false);
          }

          // white rooks
          if (i == 0 && j == 0 && k % 4 == 0) {
            grid[i][j][k] = new piece(pieceType.ROOK, true);
          }
          // black rooks
          if (i == 4 && j == 4 && k % 4 == 0) {
            grid[i][j][k] = new piece(pieceType.ROOK, false);
          }

          // white knights
          if (i == 0 && j == 0 && k % 2 == 1) {
            grid[i][j][k] = new piece(pieceType.KNIGHT, true);
          }
          // black knights
          if (i == 4 && j == 4 && k % 2 == 1) {
            grid[i][j][k] = new piece(pieceType.KNIGHT, false);
          }

          // white bishops
          if (i == 1 && j == 0 && k % 3 == 0) {
            grid[i][j][k] = new piece(pieceType.BISHOP, true);
          }
          // black bishops
          if (i == 3 && j == 4 && k % 3 == 0) {
            grid[i][j][k] = new piece(pieceType.BISHOP, false);
          }

          // white doses
          if (i == 1 && j == 0 && k % 3 == 1) {
            grid[i][j][k] = new piece(pieceType.DOS, true);
          }
          // black doses
          if (i == 3 && j == 4 && k % 3 == 1) {
            grid[i][j][k] = new piece(pieceType.DOS, false);
          }
        }
      }
    }

    // black and white kings
    grid[0][0][2] = new piece(pieceType.KING, true);
    grid[4][4][2] = new piece(pieceType.KING, false);
    // black and white queens
    grid[1][0][2] = new piece(pieceType.QUEEN, true);
    grid[3][4][2] = new piece(pieceType.QUEEN, false);
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
}
