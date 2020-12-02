package uk.ac.cam.amw223.tinyPlanet;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import uk.ac.cam.amw223.raumschach.board;

import java.io.IOException;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class graphicsApplication {

    private static final int HEIGHT = 1000;
    private static final int WIDTH = 1000;
    private static final boolean printfps = false;

    // The window handle
    private long window;
    private int programID;

    // locations for various uniforms in the vertex shader (N.B. not the location in world space)
    private int mvpLocation;
    private int mvpNormalLocation;
    private int mvLocation;
    private int cameraLocation;
    private int lightSourceLocation;
    private int shadeModeLocation;

    private int vao;
    private int vertexBuffer;
    private int uvBuffer;
    private int normalBuffer;
    private int indexBuffer;

    gameObject light;

    private Matrix4f projection;
    private Matrix4f mvp = new Matrix4f();

    double lastTime;
    double currentTime;

    private gameUniverse universe = new gameUniverse();

    int shadingMode;
    final static int shadingModes = 4;

    public void run() {
        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        // todo: should this be an option?
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "Raumschach", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        glfwShowWindow(window);

        GL.createCapabilities();

        try {
            programID = LoadShaders("resources/shaders/textureVertexShader.glsl", "resources/shaders/toonFragmentShader.glsl");
        } catch (IOException e) {
            System.err.println("Unable to load the shaders: " + e.getMessage());
        }

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        // enable transparency for wireframe
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_CULL_FACE);

        final float clearCol = 0.95f;
        glClearColor(clearCol, clearCol, clearCol, clearCol);

        glUseProgram(programID);

        int[] windowWidth = new int[1];
        int[] windowHeight = new int[1];
        glfwGetWindowSize(window, windowWidth, windowHeight);

        universe.init();
        universe.setLightSource(new Vector3f(5, 20, 5)); // above the middle of the board

        projection = new Matrix4f().perspective((float)Math.toRadians(45.0f),
                (float) windowWidth[0] / (float) windowHeight[0],
                0.1f,
                100.0f);

        mvpLocation = glGetUniformLocation(programID, "MVP");
        mvpNormalLocation = glGetUniformLocation(programID, "normalMVP");
        mvLocation = glGetUniformLocation(programID, "MV");
        cameraLocation = glGetUniformLocation(programID, "cameraV");
        lightSourceLocation = glGetUniformLocation(programID, "lightV");
        shadeModeLocation = glGetUniformLocation(programID, "shaderMode");

        vertexBuffer = glGenBuffers();
        normalBuffer = glGenBuffers();
        uvBuffer = glGenBuffers();
        indexBuffer = glGenBuffers();

        loadTexture(universe.currentTexPath());

        currentTime = glfwGetTime();
    }

    private void loop() {

        Matrix4f model, view, mv = new Matrix4f();
        Vector4f cameraHomo;

        Matrix3f mvpNormal;

        float deltaTime;

        boolean cameraToggle = false, shadingToggle = false;

        // todo: how is the callback different to the getKey part of the while condition? Can one be trimmed? why?
        while ( !glfwWindowShouldClose(window) && glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS) {

            Vector3f velocity = new Vector3f();
            float dv = 1.5f;
            if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
                dv *= 2;
            }
            double dtheta = 0.5f;

            // movement
            if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
                velocity.add(new Vector3f(0, 0, dv));
            }
            if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
                velocity.add(new Vector3f(0, 0, -dv));
            }
            if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
                velocity.add(new Vector3f(dv, 0, 0));
            }
            if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
                velocity.add(new Vector3f(-dv, 0, 0));
            }
            if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
                velocity.add(new Vector3f(0, dv, 0));
            }
            if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
                velocity.add(new Vector3f(0, -dv, 0));
            }
            velocity.mul(universe.getCam().getRotation());
            universe.getCam().setVelocity(velocity);
            // rotation
            if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) {
                universe.getCam().setRotationSpeed(dtheta);
            } else if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS) {
                universe.getCam().setRotationSpeed(-dtheta);
            } else {
                universe.getCam().setRotationSpeed(0);
            }

            // shading mode
            if (glfwGetKey(window, GLFW_KEY_X) == GLFW_PRESS) {
                if (!shadingToggle) {
                    shadingMode = (shadingMode + 1) % shadingModes;
                    shadingToggle = true;
                }
            } else {
                shadingToggle = false;
            }

            glUniform1i(shadeModeLocation, shadingMode);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            lastTime = currentTime;
            currentTime = glfwGetTime();
            deltaTime = (float)(currentTime - lastTime);

            universe.nextFrame(deltaTime);

            view = universe.getCam().viewMatrix();

            cameraHomo = new Vector4f(universe.getCam().position(), 1);
            cameraHomo.mul(view);

            // camera should never get to w = 0 so it should throw an exception
            cameraHomo.mul(cameraHomo.w);

            glUniform3fv(cameraLocation, new float[]{
                    cameraHomo.x,
                    cameraHomo.y,
                    cameraHomo.z
            });
            glUniform3fv(lightSourceLocation, new float[]{
                    universe.getLightSource().x,
                    universe.getLightSource().y,
                    universe.getLightSource().z
            });

            while (universe.nextObject()) {

                model = universe.currentModel();
                projection.mul(view, mvp);
                mvp.mul(model);

                glUniformMatrix4fv(mvpLocation, false, mvp.get(new float[16]));

                // Transformation by a non-orthogonal matrix does not preserve angles

                mvpNormal = new Matrix3f();
                model.get3x3(mvpNormal);
                mvpNormal = mvpNormal.invert();
                mvpNormal = mvpNormal.transpose();

                glUniformMatrix3fv(mvpNormalLocation, false, mvpNormal.get(new float[9]));

                view.mul(model, mv);

                glUniformMatrix4fv(mvLocation, false, mv.get(new float[16]));

                vao = glGenVertexArrays();
                glBindVertexArray(vao);

                glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
                glBufferData(GL_ARRAY_BUFFER, universe.currentVertexBuffer(), GL_STATIC_DRAW);
                glVertexAttribPointer(0, 3, GL_FLOAT, false,0, 0);
                glEnableVertexAttribArray(0);

                glBindBuffer(GL_ARRAY_BUFFER, uvBuffer);
                glBufferData(GL_ARRAY_BUFFER, universe.currentUVBuffer(), GL_STATIC_DRAW);
                glVertexAttribPointer(1, 2, GL_FLOAT, false,0, 0);
                glEnableVertexAttribArray(1);

                glBindBuffer(GL_ARRAY_BUFFER, normalBuffer);
                glBufferData(GL_ARRAY_BUFFER, universe.currentNormalBuffer(), GL_STATIC_DRAW);
                glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
                glEnableVertexAttribArray(2);

                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, universe.currentIndexBuffer(), GL_STATIC_DRAW);

                glDrawElements(
                        GL_TRIANGLES,
                        universe.currentIndexBuffer().length,
                        GL_UNSIGNED_INT,
                        0
                );
            }

            if (printfps)
                System.out.printf("FPS: %f\n", 1.0/deltaTime);

            glfwSwapBuffers(window);

            glfwPollEvents();
        }
    }

    public void uploadMatrix4f(Matrix4f m, String target) {
        int location = glGetUniformLocation(programID, target);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        m.get(buffer);
        glUniformMatrix4fv(location, false, buffer);
    }


    static int LoadShaders(String vertex_file_path, String fragment_file_path) throws IOException {

        int VertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        int FragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);

        String VertexShaderCode = new String(Files.readAllBytes(Path.of(vertex_file_path)));
        String FragmentShaderCode = new String(Files.readAllBytes(Path.of(fragment_file_path)));

        int InfoLogLength;

        glShaderSource(VertexShaderID, VertexShaderCode);
        glCompileShader(VertexShaderID);

        // Check Vertex Shader
        glGetShaderi(VertexShaderID, GL_COMPILE_STATUS);
        InfoLogLength = glGetShaderi(VertexShaderID, GL_INFO_LOG_LENGTH);
        if ( InfoLogLength > 0 ){
            System.err.println(glGetShaderInfoLog(VertexShaderID));
        }

        glShaderSource(FragmentShaderID, FragmentShaderCode);
        glCompileShader(FragmentShaderID);

        // Check Fragment Shader
        glGetShaderi(FragmentShaderID, GL_COMPILE_STATUS);
        InfoLogLength = glGetShaderi(FragmentShaderID, GL_INFO_LOG_LENGTH);
        if ( InfoLogLength > 0 ){
            System.err.println(glGetShaderInfoLog(FragmentShaderID));
        }

        // Link the program
        int ProgramID = glCreateProgram();
        glAttachShader(ProgramID, VertexShaderID);
        glAttachShader(ProgramID, FragmentShaderID);
        glLinkProgram(ProgramID);

        // Check the program
        glGetProgrami(ProgramID, GL_LINK_STATUS);
        InfoLogLength = glGetProgrami(ProgramID, GL_INFO_LOG_LENGTH);
        if ( InfoLogLength > 0 ){
            System.err.println(glGetProgramInfoLog(ProgramID));
        }

        glDetachShader(ProgramID, VertexShaderID);
        glDetachShader(ProgramID, FragmentShaderID);

        glDeleteShader(VertexShaderID);
        glDeleteShader(FragmentShaderID);
        return ProgramID;
    }

    private int loadTexture(String path) {

        int textureID;

        textureLoader tex = new textureLoader(path);

        textureID = glGenTextures();

        glEnable(GL_TEXTURE_2D);

        // "Bind" the newly created texture : all future texture functions will modify this texture
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Give the image to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0,GL_RGB, tex.getWidth(), tex.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, tex.buffer());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        return textureID;
    }

    public void linkBoard(board b) {
        universe.linkBoard(b);
    }
}