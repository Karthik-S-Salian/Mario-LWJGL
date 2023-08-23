package renderer;

import components.SpriteRenderer;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.w3c.dom.Text;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch>{


    // vao
    //  vertex-coords       color r,g,b,a           texture-coords   texture-id
    // float float      float float float float    float float       float

    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int POS_OFFSET = 0;

    private final int TEX_COORDS_SIZE = 2;
    private  final int TEX_ID_SIZE = 1;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE*Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE*Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE*Float.BYTES;
    private final int VERTEX_SIZE =POS_SIZE + COLOR_SIZE + TEX_COORDS_SIZE + TEX_ID_SIZE;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;


    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;

    private List<Texture> textures;

    private int[] texSlots = {0,1,2,3,4,5,6,7};


    private int vaoID,vboID;
    private int maxBatchSize;
    private int maxTextureBatchSize;
    private Shader shader;
    private int zIndex;

    public RenderBatch(int maxBatchSize,int maxTextureBatchSize,int zIndex){
        this.zIndex = zIndex;
        this.shader = AssetPool.getShader("assets/shaders/default.glsl");

        this.sprites = new SpriteRenderer[maxBatchSize];

        this.maxBatchSize = maxBatchSize;
        this.maxTextureBatchSize = maxTextureBatchSize;

        //4 vertices quads
        this.vertices = new float[maxBatchSize* 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;

        this.textures = new ArrayList<>();
    }


    public void addSprite(SpriteRenderer spr){
        //get add and index renderObject
        int index = this.numSprites;
        this.sprites[index] = spr;
        numSprites++;

        if(spr.getTexture()!=null){
            if(!textures.contains(spr.getTexture())){
                textures.add(spr.getTexture());
            }
        }

        //add properties to local vertex array
        loadVertexProperties(index);

        if(this.numSprites>= maxBatchSize){
            hasRoom = false;
        }
    }

    private void loadVertexProperties(int index){
        SpriteRenderer sprite = this.sprites[index];

        //find the offset within the array ( 4 vertices per sprite)
        int offset =  index*4*VERTEX_SIZE;
        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoords();

        int texID =0;

        if(sprite.getTexture()!= null){
            texID = textures.indexOf(sprite.getTexture())+1;
        }

        //add vertices with appropriate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;


        for(int i=0;i<4;i++){
            if(i==1){
                yAdd=0.0f;
            } else if (i==2) {
                xAdd = 0.0f;
            } else if (i==3) {
                yAdd = 1.0f;
            }

            vertices[offset] = sprite.gameObject.transform.position.x + xAdd*sprite.gameObject.transform.scale.x;
            vertices[offset +1] = sprite.gameObject.transform.position.y + yAdd*sprite.gameObject.transform.scale.y;

            //load color
            vertices[offset+2] = color.x;
            vertices[offset+3] = color.y;
            vertices[offset+4] = color.z;
            vertices[offset+5] = color.w;

            //load texture coordinates
            vertices[offset+6] = texCoords[i].x;
            vertices[offset+7] = texCoords[i].y;

            //load texture id
            vertices[offset+8] = (float) texID;

            offset+=VERTEX_SIZE;
        }


    }

    public void start(){
        //generate and bing vao
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length *Float.BYTES,GL_DYNAMIC_DRAW);

        //create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices,GL_STATIC_DRAW);

        //enable the buffer attribute pointer
        glVertexAttribPointer(0,POS_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1,COLOR_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2,TEX_COORDS_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3,TEX_ID_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

    }

    private int[] generateIndices(){
        //6 indices per quad (3 per triangle)
        int[] elements = new int[6*maxBatchSize];

        for(int i=0;i<maxBatchSize;i++){
            loadElementIndices(elements,i);
        }

        return elements;
    }

    private void  loadElementIndices(int[] elements,int index){
        int offsetArrayIndex = 6*index;
        int offset = 4*index;

        // 3 2 0 0 2 1      7 6 4 4 6 5

        //triangle 1
        elements[offsetArrayIndex] = offset+3;
        elements[offsetArrayIndex +1] = offset + 2;
        elements[offsetArrayIndex +2] = offset;

        // triangle 2
        elements[offsetArrayIndex+ 3] = offset;
        elements[offsetArrayIndex +4] = offset + 2;
        elements[offsetArrayIndex +5] = offset + 1;
    }


    public void render(){


        boolean reBufferData = false;

        for(int i=0;i<numSprites;i++){
            SpriteRenderer spr = sprites[i];
            if(spr.isDirty()){
                loadVertexProperties(i);
                spr.setClean();
                reBufferData=true;
            }
        }

        if(reBufferData){
            glBindBuffer(GL_ARRAY_BUFFER,vboID);
            glBufferSubData(GL_ARRAY_BUFFER,0,vertices);
        }


        shader.use();

        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView",Window.getScene().camera().getViewMatrix());


        for(int i=0;i<textures.size();i++){
            glActiveTexture(GL_TEXTURE0+i+1);
            textures.get(i).bind();
        }

        shader.uploadIntArray("uTextures",texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES,numSprites*6,GL_UNSIGNED_INT,0);


        //unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0); // 0 to unbind


        for (Texture texture : textures) {
            texture.unBind();
        }

        shader.detach();
    }

    public boolean hasRoom(){
        return hasRoom;
    }

    public boolean hasTextureRoom(){
        return this.textures.size()<this.maxTextureBatchSize;
    }

    public boolean hasTexture(Texture tex){
        return this.textures.contains(tex);
    }

    public int zIndex(){return this.zIndex;}

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex,o.zIndex);
    }
}
