package uk.ac.cam.amw223.raumschach.core;

import org.joml.Vector3i;
import uk.ac.cam.amw223.raumschach.core.pieces.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class board implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final int BOARD_SIZE = 5;
  private static final int SPACING = 2;

  private boolean dirty;

  private piece[][][] grid = new piece[BOARD_SIZE][BOARD_SIZE][BOARD_SIZE];
  private Map<piece, Vector3i> whitePieces = new HashMap<>();
  private Map<piece, Vector3i> blackPieces = new HashMap<>();
  private Map<piece, Vector3i> allPieces = new HashMap<>();

  private float[] vertexData;
  private float[] normalData;
  private float[] textureData;
  private int[] indexData;

  private boolean whiteHasKing = true;
  private boolean blackHasKing = true;

  private static float[] whiteTextureData = new float[]{
          0.1f, 0.1f,
          0.4f, 0.1f,
          0.4f, 0.4f
  };
  private static float[] blackTextureData = new float[]{
          0.6f, 0.1f,
          0.9f, 0.1f,
          0.9f, 0.4f
  };

  public board() {

    /*for (int i = 0; i < 20; i++) {
      boolean isWhite = i < 10;
      int x = (i / 5) + (isWhite? 0 : 1);
      int y = isWhite? 1 : 3;
      int z = i % 5;
      grid[x][y][z] = new pawn(isWhite);
    }*/

    // this one doesn't use as many magic numbers
    for (int i = 0; i < BOARD_SIZE * 4; i++) {
      boolean isWhite = i < BOARD_SIZE * 2;
      int x = (i / BOARD_SIZE) + (isWhite? 0 : BOARD_SIZE - 4);
      int y = isWhite? 1 : BOARD_SIZE - 2;
      int z = i % BOARD_SIZE;
      grid[x][y][z] = new pawn(isWhite, this);
      if (isWhite)
        whitePieces.put(grid[x][y][z], new Vector3i(x, y, z));
      else
        blackPieces.put(grid[x][y][z], new Vector3i(x, y, z));
    }

    for (int i = 0; i < 4; i++) {
      boolean isWhite = i < 2;
      int x = isWhite? 0 : 4;
      int z = (i % 2) * 4; // 0 or 4
      // x and y are the same number, this is not erroneous
      grid[x][x][z] = new rook(isWhite);
      if (isWhite)
        whitePieces.put(grid[x][x][z], new Vector3i(x, x, z));
      else
        blackPieces.put(grid[x][x][z], new Vector3i(x, x, z));
    }

    for (int i = 0; i < 4; i++) {
      boolean isWhite = i < 2;
      int x = isWhite? 0 : 4;
      int z = 1 + (i % 2) * 2; // 1 or 3
      // x and y are the same number, this is not erroneous
      grid[x][x][z] = new knight(isWhite);
      if (isWhite)
        whitePieces.put(grid[x][x][z], new Vector3i(x, x, z));
      else
        blackPieces.put(grid[x][x][z], new Vector3i(x, x, z));
    }

    for (int i = 0; i < 4; i++) {
      boolean isWhite = i < 2;
      int x = isWhite? 1 : 3;
      int y = isWhite? 0 : 4;
      int z = (i % 2) * 3; // 0 or 3
      grid[x][y][z] = new bishop(isWhite);
      if (isWhite)
        whitePieces.put(grid[x][y][z], new Vector3i(x, y, z));
      else
        blackPieces.put(grid[x][y][z], new Vector3i(x, y, z));
    }

    for (int i = 0; i < 4; i++) {
      boolean isWhite = i < 2;
      int x = isWhite? 1 : 3;
      int y = isWhite? 0 : 4;
      int z = 1 + (i % 2) * 3; // 1 or 4
      grid[x][y][z] = new dos(isWhite);
      if (isWhite)
        whitePieces.put(grid[x][y][z], new Vector3i(x, y, z));
      else
        blackPieces.put(grid[x][y][z], new Vector3i(x, y, z));
    }

    grid[0][0][2] = new king(true);
    grid[4][4][2] = new king(false);
    whitePieces.put(grid[0][0][2], new Vector3i(0, 0, 2));
    blackPieces.put(grid[4][4][2], new Vector3i(4, 4, 2));

    grid[1][0][2] = new queen(true);
    grid[3][4][2] = new queen(false);
    whitePieces.put(grid[1][0][2], new Vector3i(1, 0, 2));
    blackPieces.put(grid[3][4][2], new Vector3i(3, 4, 2));

    allPieces.putAll(whitePieces);
    allPieces.putAll(blackPieces);

    dirty = true;
  }

  public synchronized void generateModel() {

    // todo: if a piece is selected now would be a good time to add valid move boxes
    //  could put valid move boxes in a separate upload and generation algorithm.
    //  maybe a generic function that can generate for pieces or for valid move boxes

    // loop over the board
    //   if piece is not null
    //     add vertex, UV (generated here based on colour) and normal data to buffer
    //     adding piece position to vertices
    //   check if this position is a valid move,
    //   if so we need to add it to the model data buffers
    // generate index buffers using alg in modelLoader

    // if the board hasn't changed, no need to regenerate the graphics data
    if (dirty) {

      List<Float> vertexBuffers = new ArrayList<>();
      List<Float> textureBuffers = new ArrayList<>();
      List<Float> normalBuffers = new ArrayList<>();
      List<Integer> indexBuffers = new ArrayList<>();

      int indexOffset = 0;

      for (Map.Entry<piece, Vector3i> e : allPieces.entrySet()) {
        int x, y, z;
        piece p = e.getKey();
        x = e.getValue().x; y = e.getValue().y; z = e.getValue().z;

        int count = 0;
        int offset = 0;
        for (float f : p.getGameObject().getVertexBuffer()) {
          switch (count) {
            case 0:
              offset = y * SPACING;
              break;
            case 1:
              offset = x * SPACING;
              break;
            case 2:
              offset = z * SPACING;
              break;
          }
          vertexBuffers.add(f + offset);
          count = (count + 1) % 3;
        }
        for (float f : p.getGameObject().getNormalBuffer()) {
          normalBuffers.add(f);
        }
        count = 0;
        for (float f : p.getGameObject().getUVBuffer()) {
          count = (count + 1) % 6;
          if (p.isWhite()) {
            textureBuffers.add(whiteTextureData[count]);
          } else {
            textureBuffers.add(blackTextureData[count]);
          }
        }
        for (int index : p.getGameObject().getIndexBuffer()) {
          indexBuffers.add(index + indexOffset);
        }
        indexOffset = vertexBuffers.size() / 3;
      }

      vertexData = new float[vertexBuffers.size()];
      for (int i = 0; i < vertexData.length; i++) {
         vertexData[i] = vertexBuffers.get(i);
      }

      normalData = new float[normalBuffers.size()];
      for (int i = 0; i < normalData.length; i++) {
        normalData[i] = normalBuffers.get(i);
      }

      textureData = new float[textureBuffers.size()];
      for (int i = 0; i < textureData.length; i++) {
        textureData[i] = textureBuffers.get(i);
      }

      indexData = new int[indexBuffers.size()];
      for (int i = 0; i < indexData.length; i++) {
        indexData[i] = indexBuffers.get(i);
      }

      dirty = false;
    }
  }

  public float[] getVertexData() {
    return vertexData;
  }

  public float[] getNormalData() {
    return normalData;
  }

  public float[] getTextureData() {
    return textureData;
  }

  public int[] getIndexData() {
    return indexData;
  }

  public synchronized piece getPiece(position pos) {
    return grid[pos.getI()][pos.getJ()][pos.getK()];
  }

  public boolean hasKing(boolean color) {
    if (color)
      return whiteHasKing;
    else
      return blackHasKing;
  }

  public synchronized void validateMove(Move move, boolean turn) throws InvalidMoveException {

    if (move == null)
      throw new InvalidMoveException(false, false,
              "Move passed was null");

    // piece to move exists
    if (getPiece(move.start) == null || getPiece(move.start).isWhite() != turn)
      throw new InvalidMoveException(false, true,
              "There is no piece in that position to move");
    // piece to move and to take are not the same colour
    if (getPiece(move.end) != null
            && getPiece(move.start).isWhite() == getPiece(move.end).isWhite())
      throw new InvalidMoveException(true, false,
              "Cannot capture a piece which is the same colour as the capturing piece");
    // piece to move can move in such a way
    if (!getPiece(move.start).verifyMove(move.start, move.end))
      throw new InvalidMoveException(true, false,
              "That piece cannot move like that");

    if (!getPiece(move.start).getName().equals("knight")) {
      Vector3i d = new Vector3i();
      Vector3i loop = new Vector3i();
      move.end.asVector().sub(move.start.asVector(), d);
      int max = Math.max(Math.abs(d.x),
              Math.max(Math.abs(d.y),
                      Math.abs(d.z)));
      d.div(max);
      for (int i = 1; i < max; i++) {
        d.mul(i, loop);
        if (getPiece(position.fromVector(move.start.asVector().add(loop))) != null) {
          throw new InvalidMoveException(true, false, "That piece can't move through other pieces");
        }
      }
    }
  }

  public synchronized void makeMove(Move move, boolean turn) throws InvalidMoveException {

    validateMove(move, turn);

    // if taking the king then update hasKing
    piece toTake = grid[move.end.getI()][move.end.getJ()][move.end.getK()];
    if (toTake instanceof king)
      if (toTake.isWhite())
        whiteHasKing = false;
      else
        blackHasKing = false;
    // move piece (overwriting the piece to take if relevant)
    if (toTake != null) {
      allPieces.remove(grid[move.end.getI()][move.end.getJ()][move.end.getK()]);
    }
    grid[move.end.getI()][move.end.getJ()][move.end.getK()] = getPiece(move.start);
    grid[move.start.getI()][move.start.getJ()][move.start.getK()] = null;
    dirty = true;
  }

  public synchronized void initialiseGraphics() {
    allPieces.forEach((x, v) -> x.initialiseGraphics());
  }

  public static int boardSize() {
    return BOARD_SIZE;
  }

  @Override
  public synchronized String toString() {
    String result = "";
    for (int i = 0; i < BOARD_SIZE; i++) {
      result += "Level " + (char)((int)'A' + i) + "\n\n";
      result += "  a    b    c    d    e  \n";
      for (int j = 0; j < BOARD_SIZE; j++) {
        result += "+----+----+----+----+----+\n";
        for (int k = 0; k < BOARD_SIZE; k++) {
          result += "| ";
          if (grid[i][j][k] == null)
            result += "  ";
          else
            result += grid[i][j][k];
          result += " ";
        }
        result += "| " + (j + 1) + "\n";
      }
      result += "+----+----+----+----+----+\n\n";
    }
    return result;
  }
}
