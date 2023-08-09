package jade;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene{

    private float[] vertexArray={
            //position ,         color
            100.0f,0.0f,0.0f,    1.0f,0.0f,0.0f,1.0f,  //bottom right 0
            0.0f,100.0f,0.0f,    0.0f,1.0f,0.0f,1.0f, //top left      1
            100.0f,100.0f,0.0f,    0.0f,0.0f,1.0f,1.0f,  //top right     2
            0.0f,0.0f,0.0f,    1.0f,1.0f,0.0f,1.0f //bottom left  3
    };


    // IMPORTANT MUST BE IN COUNTERCLOCKWISE ORDER
    private int[] elementArray={
            /*
                    x  <--  x
                    |       ''
                    ''      |
                    x   --> x
             */
    2,1,0, //top-right triangle
    0,1,3 // bottom left triangle
    };

    private  int vaoID,vboID,eboID;

    private Shader defaultShader;

    public LevelEditorScene(){


    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());

        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

        //***************************************************************
        // GENERATING VAO, VBO, EBO BUFFER OBJECT and send it to gpu
        //***************************************************************

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //Create VBO and upload vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboID);
        glBufferData(GL_ARRAY_BUFFER,vertexBuffer,GL_STATIC_DRAW);

        //Create an int buffer of element
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,elementBuffer,GL_STATIC_DRAW);

        //add vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;

        int vertexSizeBytes = (positionsSize + colorSize)*floatSizeBytes;
        glVertexAttribPointer(0,positionsSize,GL_FLOAT,false,vertexSizeBytes,0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1,colorSize,GL_FLOAT,false,vertexSizeBytes,positionsSize*floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        camera.position.x-=50*dt;
        //bind shader Program
        defaultShader.use();

        defaultShader.uploadMat4f("uProjection",camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView",camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        //bind the vao
        glBindVertexArray(vaoID);

        //enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES,elementArray.length,GL_UNSIGNED_INT,0);


        //unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0); // 0 to unbind


        defaultShader.detach();

    }

}
