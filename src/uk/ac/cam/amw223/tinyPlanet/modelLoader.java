package uk.ac.cam.amw223.tinyPlanet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class modelLoader {

    private float[] vertexBuffer;
    private float[] uvBuffer;
    private float[] normalBuffer;

    private float[] barycentricBuffer = new float[]
                    {1.f, 0.f, 0.f,
                    0.f, 1.f, 0.f,
                    0.f, 0.f, 1.f};

    // used in construction of indexBuffer
    private int[] vertexIndexBuffer;
    private int[] uvIndexBuffer;
    private int[] normalIndexBuffer;

    private int[] barycentricIndexBuffer;

    private int[] indexBuffer;

    public modelLoader(String path) {
        try {
            loadBuffers(path);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void loadBuffers(String path) throws IOException {

        List<float[]> vertexBufferList = new ArrayList<>();
        List<float[]> uvBufferList = new ArrayList<>();
        List<float[]> normalBufferList = new ArrayList<>();
        List<int[]> vertexIndexBufferList = new ArrayList<>();
        List<int[]> uvIndexBufferList = new ArrayList<>();
        List<int[]> normalIndexBufferList = new ArrayList<>();

        List<int[]> baryBufferIndexList = new ArrayList<>();
        final List<int[]> baryBufferToAdd = List.of(new int[]{0, 1, 2});

        List<String> file = Files.readAllLines(Path.of(path));
        List<String> tokens;
        List<String> faceTokens;
        int faceType = 0;
        float[] currentVertexLine;
        float[] currentUVLine;
        float[] currentNormalLine;
        int[] currentVertexIndexLine;
        int[] currentUVIndexLine;
        int[] currentNormalIndexLine;

        for (String line : file) {
            tokens = parseLine(line, ' ');
            // ensure not empty line
            if (tokens.size() > 0) {
                if (tokens.get(0).equals("v")) {
                    // vertex coordinates
                    currentVertexLine = new float[3];
                    currentVertexLine[0] = Float.parseFloat(tokens.get(1));
                    currentVertexLine[1] = Float.parseFloat(tokens.get(2));
                    currentVertexLine[2] = Float.parseFloat(tokens.get(3));
                    if (tokens.get(0).length() == 5) {
//                        currentVertexLine[3] = Float.parseFloat(tokens.get(4));
                    } else {
//                        currentVertexLine[3] = 1.0f;
                    }
                    // anything not read in defaults to 0 anyway
                    vertexBufferList.add(currentVertexLine);
                } else if (tokens.get(0).equals("vt")) {
                    //texture co-ordinates
                    currentUVLine = new float[2];
                    currentUVLine[0] = Float.parseFloat(tokens.get(1));
                    if (tokens.size() > 2) {
                        currentUVLine[1] = Float.parseFloat(tokens.get(2));
                    }
                    if (tokens.size() > 3) {
//                        currentUVLine[2] = Float.parseFloat(tokens.get(3));
                    }
                    // anything not read in defaults to 0 anyway
                    uvBufferList.add(currentUVLine);
                } else if (tokens.get(0).equals("vn")) {
                    currentNormalLine = new float[3];
                    currentNormalLine[0] = Float.parseFloat(tokens.get(1));
                    currentNormalLine[1] = Float.parseFloat(tokens.get(2));
                    currentNormalLine[2] = Float.parseFloat(tokens.get(3));
                    normalBufferList.add(currentNormalLine);
                } else if (tokens.get(0).equals("f")) {
                    // face information
                    currentVertexIndexLine = new int[tokens.size() - 1];
                    currentUVIndexLine = new int[tokens.size() - 1];
                    currentNormalIndexLine = new int[tokens.size() - 1];
                    for (int i = 1; i < tokens.size(); i++) {
                        // triangle i can be made with the following
                        // 0 (i) (i + 1)  [for i in 1..(n - 2)]
                        faceTokens = parseLine(tokens.get(i), '/');
                        if (faceType == 0) {
                            faceType = tokens.size();
                            if (faceTokens.get(1).equals("")) {
                                faceType++;
                            }
                        }
                        // 1 : f v1 v2 v3 ...
                        // 2 : f v1/vt1 v2/vt2 v3/vt3 ...
                        // 3 : f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3 ...
                        // 4 : f v1//vn1 v2//vn2 v3//vn3 ...
                        if (faceType > 0) {
                            currentVertexIndexLine[i - 1] = Integer.parseInt(faceTokens.get(0)) - 1;
                        }
                        if (faceType > 1) {
                            currentUVIndexLine[i - 1] = Integer.parseInt(faceTokens.get(1)) - 1;
                        }
                        if (faceType > 2) {
                            // vector normal, not implemented yet
                            currentNormalIndexLine[i - 1] = Integer.parseInt(faceTokens.get(2)) - 1;
                        }
                    }
                    for (int i = 1; i < currentVertexIndexLine.length - 1; i++) {
                        // triangle i can be made with the following
                        // 0 (i) (i + 1)  [for i in 1..(n - 2)]
                        vertexIndexBufferList.add(
                                new int[]{currentVertexIndexLine[0],
                                        currentVertexIndexLine[i],
                                        currentVertexIndexLine[i + 1]});
                    }
                    for (int i = 1; i < currentUVIndexLine.length - 1; i++) {
                        // triangle i can be made with the following
                        // 0 (i) (i + 1)  [for i in 1..(n - 2)]
                        uvIndexBufferList.add(
                                new int[]{currentUVIndexLine[0],
                                        currentUVIndexLine[i],
                                        currentUVIndexLine[i + 1]});
                    }
                    for (int i = 1; i < currentNormalIndexLine.length - 1; i++) {
                        normalIndexBufferList.add(
                                new int[]{currentNormalIndexLine[0],
                                        currentNormalIndexLine[i],
                                        currentNormalIndexLine[i + 1]}
                                        );
                    }

                    // maybe this could be improved in the index buffer generation to reduce repeated data
                    // each vertex of a triangle needs one of the 3 basis vectors but it doesn't matter which

                    for (int i = 0; i < tokens.size() - 2; i++) {
                        baryBufferIndexList.addAll(baryBufferToAdd);
                    }

                    faceType = 0;
                }
                // other .obj functionality that I don't know how to use yet lol
            }
        }

        vertexBuffer = generateBuffer(vertexBufferList, 3);
        uvBuffer = generateBuffer(uvBufferList, 2);
        normalBuffer = generateBuffer(normalBufferList, 3);

        vertexIndexBuffer = generateIndexBuffer(vertexIndexBufferList, 3);
        uvIndexBuffer = generateIndexBuffer(uvIndexBufferList, 3);
        normalIndexBuffer = generateIndexBuffer(normalIndexBufferList, 3);
        barycentricIndexBuffer = generateIndexBuffer(baryBufferIndexList, 3);

        // this ties together the two index buffers into one buffer that openGL can use
        // (it also modifies the orderings in the vertex and UV buffers so they match the
        // new unified buffer)
        generateIndexBuffer();
    }

    private List<String> parseLine(String line, char delimiter) {
        List<String> tokens = new ArrayList<>();
        String current = "";
        for (char c : line.toCharArray()) {
            if (c == delimiter) {
                tokens.add(current);
                current = "";
            } else {
                current += c;
            }
        }
        tokens.add(current);
        return tokens;
    }

    private float[] generateBuffer(List<float[]> l, int elementWidth) {
        float[] vbo = new float[l.size() * elementWidth];
        for (int i = 0; i < l.size(); i++) {
            for (int j = 0; j < elementWidth; j++) {
                vbo[elementWidth * i + j] = l.get(i)[j];
            }
        }
        return vbo;
    }

    private int[] generateIndexBuffer(List<int[]> l, int elementWidth) {
        int[] vbo = new int[l.size() * elementWidth];
        for (int i = 0; i < l.size(); i++) {
            for (int j = 0; j < elementWidth; j++) {
                vbo[elementWidth * i + j] = l.get(i)[j];
            }
        }
        return vbo;
    }

    private void generateIndexBuffer() {

        List<float[]> vertexBufferList = new ArrayList<>();
        List<float[]> uvBufferList = new ArrayList<>();
        List<float[]> normalBufferList = new ArrayList<>();
        List<float[]> barycentricBufferList = new ArrayList<>();

        List<Integer> newIndexBuffer = new ArrayList<>();

        Map<Integer, int[]> foundIndeces = new HashMap<>();
        int[] foundIndex;

        int count = 0;
        int v, t, n, b;
        boolean found;

        for (int i = 0; i < vertexIndexBuffer.length; i++) {
            v = vertexIndexBuffer[i];
            t = uvIndexBuffer[i];
            n = normalIndexBuffer[i];
            b = barycentricIndexBuffer[i];

            found = false;

            // if index hasn't been found yet
            for (int j = 0; j < foundIndeces.size(); j++) {
                foundIndex = foundIndeces.get(j);
                if (foundIndex[0] == v && foundIndex[1] == t && foundIndex[2] == n && foundIndex[3] == b) {
                    found = true;
                    // pair found previously
                    newIndexBuffer.add(j);
                }
            }
            if (!found) {// new pair found
                vertexBufferList.add(new float[]{
                        vertexBuffer[3 * v],
                        vertexBuffer[3 * v + 1],
                        vertexBuffer[3 * v + 2]
                });
                uvBufferList.add(new float[]{
                        uvBuffer[2 * t],
                        uvBuffer[2 * t + 1]
                });
                normalBufferList.add(new float[]{
                        normalBuffer[3 * n],
                        normalBuffer[3 * n + 1],
                        normalBuffer[3 * n + 2]
                });
                barycentricBufferList.add(new float[]{
                        barycentricBuffer[3 * b],
                        barycentricBuffer[3 * b + 1],
                        barycentricBuffer[3 * b + 2]
                });

                // index and memoise new pair location
                newIndexBuffer.add(count);
                foundIndeces.put(count, new int[]{v, t, n, b});
                count++;
            }
        }

        vertexBuffer = generateBuffer(vertexBufferList, 3);
        uvBuffer = generateBuffer(uvBufferList, 2);
        normalBuffer = generateBuffer(normalBufferList, 3);
        barycentricBuffer = generateBuffer(barycentricBufferList, 3);

        int[] vbo = new int[newIndexBuffer.size()];
        for (int i = 0; i < newIndexBuffer.size(); i++) {
            vbo[i] = newIndexBuffer.get(i);
        }

        indexBuffer = vbo;
    }

    public float[] getVertexBuffer() {
        return vertexBuffer;
    }

    public float[] getUVBuffer() {
        return uvBuffer;
    }

    public float[] getNormalBuffer() {
        return normalBuffer;
    }

    public float[] getBarycentricBuffer() {
        return barycentricBuffer;
    }

    public int[] getIndexBuffer() {
        return indexBuffer;
    }
}
