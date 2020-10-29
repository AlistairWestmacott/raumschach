package uk.ac.cam.amw223.tinyPlanet;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import uk.ac.cam.amw223.tree_d.board;
import uk.ac.cam.amw223.tree_d.position;

import java.util.ArrayList;
import java.util.List;

public class gameUniverse {

    private List<gameObject> worldObjects;

    private camera cam;

    private int numOfObjects;
    private int modelToProcess = -1;

    private Vector3f lightSource;

    private board b;
    private int toProcX, toProcY, toProcZ;

    public gameUniverse() {
        worldObjects = new ArrayList<>();
        lightSource = new Vector3f();
    }

    public void init() {
        cam = new camera();
        cam.setUniverse(this);
        b.initialiseGraphics();
    }

    public void linkBoard(board b) {
        this.b = b;
    }

    public void nextFrame(float dt) {
        for (gameObject o : worldObjects) {
            o.nextFrame(dt);
        }
        cam.nextFrame(dt);
    }

    public int addObject(String modelName, String textureName, boolean setMainObject) {
        gameObject o = new gameObject(modelName, textureName);

        return addObject(o, setMainObject);
    }

    public int addObject(gameObject o, boolean setMainObject) {
        worldObjects.add(o);
        o.setUniverse(this);

        numOfObjects++;

        int index = worldObjects.size() - 1;
        if (setMainObject) {
            cam.attach(o);
        }
        return index;
    }

//    public gameObject getMainObject() { return worldObjects.get(mainObjectIndex); }

    public boolean nextObject() {
        for (int i = toProcX; i < board.boardSize(); i++) {
            for (int j = toProcY; j < board.boardSize(); j++) {
                for (int k = toProcZ + 1; k < board.boardSize(); k++) {
                    if (b.getPiece(position.fromCoordinates(i, j, j)) != null) {
                        toProcX = i;
                        toProcY = j;
                        toProcZ = k;
                        return true;
                    }
                }
                toProcZ = -1;
            }
            toProcY = 0;
        }
        toProcX = 0;
        return false;
    }

    public Matrix4f currentModel() {
        return new Matrix4f(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                2 * toProcY, 2 * toProcX, 2 * toProcZ, 1
        );
    }

    public String currentTexPath() {
        return b.getPiece(position.fromCoordinates(toProcX, toProcY, toProcZ)).getGameObject().getTexPath();
    }

    public float[] currentVertexBuffer() {
        return b.getPiece(position.fromCoordinates(toProcX, toProcY, toProcZ)).getGameObject().getVertexBuffer();
    }

    public float[] currentUVBuffer() {
        return b.getPiece(position.fromCoordinates(toProcX, toProcY, toProcZ)).getGameObject().getUVBuffer();
    }

    public float[] currentNormalBuffer() {
        return b.getPiece(position.fromCoordinates(toProcX, toProcY, toProcZ)).getGameObject().getNormalBuffer();
    }

    public float[] currentBarycentricBuffer() {
        return b.getPiece(position.fromCoordinates(toProcX, toProcY, toProcZ)).getGameObject().getBarycentricBuffer();
    }

    public int[] currentIndexBuffer() {
        return b.getPiece(position.fromCoordinates(toProcX, toProcY, toProcZ)).getGameObject().getIndexBuffer();
    }

    public String currentObjectName() {
        return b.getPiece(position.fromCoordinates(toProcX, toProcY, toProcZ)).getGameObject().name;
    }

    public Vector3f getLightSource() { return lightSource; }

    public void setLightSource(Vector3f value) { lightSource = value; }

    public camera getCam() {
        return cam;
    }

}
