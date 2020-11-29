package uk.ac.cam.amw223.raumschach;

import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class modelLoader {

  // todo: singleton design pattern?

  private static Map<String, float[]> vertexData = new HashMap<>();
  private static Map<String, float[]> normalData = new HashMap<>();
  private static Map<String, float[]> textureData = new HashMap<>();

  private static float[] whiteTextureData = new float[]{
          0.0f, 0.0f,
          0.5f, 0.0f,
          0.5f, 0.5f
  };
  private static float[] blackTextureData = new float[]{
          0.5f, 0.0f,
          1.0f, 0.0f,
          1.0f, 0.5f
  };

  private static modelLoader instance = null;

  public static modelLoader getInstance() {
    if (instance == null) {
      instance = new modelLoader();
    }
    return instance;
  }

  public float[] getVertexData(String piece) throws IOException {
    if (!vertexData.containsKey(piece)) {
      generateModelData(piece);
    }
    return vertexData.get(piece);
  }

  public float[] getNormalData(String piece) throws IOException {
    if (!normalData.containsKey(piece)) {
      generateModelData(piece);
    }
    return normalData.get(piece);
  }

  public float[] getTextureData(String piece) throws IOException {
    if (!textureData.containsKey(piece)) {
      generateModelData(piece);
    }
    return textureData.get(piece);
  }

  private modelLoader() {}

  protected void generateModelData(String piece) throws IOException {
    List<String> file = Files.readAllLines(Path.of("resources/models/" + piece + ".obj"));
    String[] tokens;

    List<float[]> vertices = new ArrayList<>();
    List<float[]> textures = new ArrayList<>();
    List<float[]> normals = new ArrayList<>();

    // todo: texture data will be handled elsewhere!!
    //  e.g. the board class. It will also be modifying the vertex data

    List<int[]> faces = new ArrayList<>();

    for (String line : file) {
      tokens = line.split(",");
      try {
        if (tokens.length > 0) {
          switch (tokens[0]) {
            case "v":
              vertices.add(new float[]{
                      Float.parseFloat(tokens[1]),
                      Float.parseFloat(tokens[2]),
                      Float.parseFloat(tokens[3])
              });
              break;
            case "vt":
              // texture data ignored and inserted later based on piece colour
              break;
            case "vn":
              normals.add(new float[]{
                      Float.parseFloat(tokens[1]),
                      Float.parseFloat(tokens[2]),
                      Float.parseFloat(tokens[3])
              });
              break;
            case "f":
              // face data
              int faceType;
              int faceRank = tokens.length - 1;
              String[][] faceTokens = new String[faceRank][];
              for (int i = 1; i < faceRank; i++) {
                faceTokens[i] = tokens[i].split("/");
              }
              for (int i = 1; i < faceRank - 1; i++) { // first token is the data type (e.g. "f")
                // triangle i can be made with the following
                // 0 (i) (i + 1)  [for i in 1..(n - 2)]

                int[] face = new int[9];

                // 1 : f v1 v2 v3 ...
                // 2 : f v1/vt1 v2/vt2 v3/vt3 ...
                // 3 : f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3 ...
                // 4 : f v1//vn1 v2//vn2 v3//vn3 ...
                faceType = tokens.length;
                // texture data to be ignored, recreate based on colour of piece

                // vertices
                face[0] = Integer.parseInt(faceTokens[0][0]);
                face[1] = Integer.parseInt(faceTokens[i][0]);
                face[2] = Integer.parseInt(faceTokens[i + 1][0]);

                // textures
                face[3] = 0;
                face[4] = 1;
                face[5] = 2;

                // normals
                if (faceType == 3) {
                  face[6] = Integer.parseInt(faceTokens[0][2]);
                  face[7] = Integer.parseInt(faceTokens[i][2]);
                  face[8] = Integer.parseInt(faceTokens[i + 1][2]);
                } else {
                  // normals not supplied: use cross product
                  Vector3f v1 = new Vector3f(vertices.get(face[0]));
                  Vector3f v2 = new Vector3f(vertices.get(face[1]));
                  v1.cross(v2);
                  v1.normalize();
                  face[6] = face[7] = face[8] = normals.size();
                  normals.add(new float[]{v1.x, v1.y, v1.z});
                }
              }
              break;
            case "#":
              // comment; can safely be ignored
              break;
            default:
              System.err.println(
                      "Model loader cannot parse lines starting \"" + tokens[0] + "\". " +
                              "This functionality is non-essential and the loader will continue."
              );
              break;
          }
        }
      } catch (IndexOutOfBoundsException e) {
        System.err.println("Error in model data on line: " + line);
      }
    }

    // todo: need to compact data into indexed arrays
    //vertexData.put(piece, vertices);
  }

}
