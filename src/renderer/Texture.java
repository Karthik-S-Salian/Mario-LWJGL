package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private String filePath;

    private  int textureID;

    private int width;
    private int height;

    public void init(String filePath){
        this.filePath = filePath;

        //Generate Texture on GPU
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,textureID);

        //Set texture Parameters
        //Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);

        //when stretching the image pixelate
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        //when shrinking the image pixelate
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);


        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filePath,width,height,channels,0);

        if(image !=null){
            this.width = width.get(0);
            this.height = height.get(0);
            if(channels.get(0)==4){
                glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,width.get(0),height.get(0),0,GL_RGBA,GL_UNSIGNED_BYTE,image);
            } else if (channels.get(0)==3) {
                glTexImage2D(GL_TEXTURE_2D,0,GL_RGB,width.get(0),height.get(0),0,GL_RGB,GL_UNSIGNED_BYTE,image);
            } else{
                assert false : "(Texture) unknown no of channels '"+ channels.get(0) +"'";
            }

            //Free  loaded image
            stbi_image_free(image);
        }else{
            assert false : "(Texture) Could not load image '"+ filePath+"'";
        }
    }



    public void bind(){
        glBindTexture(GL_TEXTURE_2D,textureID);
    }

    public void unBind(){
        glBindTexture(GL_TEXTURE_2D,0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getId(){
        return textureID;
    }
}
