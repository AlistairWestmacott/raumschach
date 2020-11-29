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

    private boolean processed;

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
        if (processed) {
            processed = false;
        } else {
            b.generateModel();
            processed = true;
        }
        return processed;
    }

    public Matrix4f currentModel() {
        return new Matrix4f();
    }

    public String currentTexPath() {
        return "resources/textures/checkerboard.png";
    }

    public float[] currentVertexBuffer() {
        return b.getVertexData();
    }

    public float[] currentUVBuffer() {
      return b.getTextureData();
    }

    public float[] currentNormalBuffer() {
      return b.getNormalData();
    }

    public int[] currentIndexBuffer() {
      return b.getIndexData();
    }

    public Vector3f getLightSource() { return lightSource; }

    public void setLightSource(Vector3f value) { lightSource = value; }

    public camera getCam() {
        return cam;
    }

}
