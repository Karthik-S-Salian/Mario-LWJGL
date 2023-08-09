package renderer;
import org.joml.*;
import org.lwjgl.BufferUtils;

import javax.swing.plaf.basic.BasicButtonUI;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;

public class Shader {

    private int shaderProgramID;

    private String filePath;
    private  String vertexSource;
    private  String fragmentSource;

    private boolean beingUsed = false;
    public Shader(String filePath){
        this.filePath = filePath;

        try {
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String [] splitStrings = source.split("(#type)( )+([A-z]+)");

            //TODO: make glsl parsing more robust

            //find first pattern after #type
            int firstIndex = source.indexOf("#type")+6;
            int firstEol = source.indexOf("\r\n",firstIndex);
            String firstPattern = source.substring(firstIndex,firstEol).trim();

            //find second pattern after #type
            int secondIndex = source.indexOf("#type",firstEol)+6;
            int secondEol = source.indexOf("\r\n",secondIndex);
            String secondPattern = source.substring(secondIndex,secondEol).trim();

            if(firstPattern.equals("vertex")){
                vertexSource = splitStrings[1];

            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitStrings[1];
            }else {
                throw new IOException("Invalid type expected 'vertex' or 'fragment'");
            }

            if(secondPattern.equals("vertex")){
                vertexSource = splitStrings[2];

            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitStrings[2];
            }else {
                throw new IOException("Invalid type expected 'vertex' or 'fragment'");
            }

        }catch (IOException e){

            e.printStackTrace();
            assert false: "Could not open file for shader '"+ filePath+ "' ";

        }

    }

    public void compile(){

        // Compile and Link Shaders

        int vertexID,fragmentID;
        //VERTEX SHADER
        //load and compile vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        //pass the shader source to GPU
        glShaderSource(vertexID,vertexSource);
        glCompileShader(vertexID);

        // check for errors in compilation
        int success = glGetShaderi(vertexID,GL_COMPILE_STATUS);

        if(success == GL_FALSE){
            int len = glGetShaderi(vertexID,GL_INFO_LOG_LENGTH);

            System.out.println("ERROR \n\t Vertex Shader Compile failed");

            System.out.println(glGetShaderInfoLog(vertexID,len));
            assert false : "";
        }



        //FRAGMENT SHADER
        //load and compile fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        //pass the shader source to GPU
        glShaderSource(fragmentID,fragmentSource);
        glCompileShader(fragmentID);

        // check for errors in compilation
        success = glGetShaderi(fragmentID,GL_COMPILE_STATUS);

        if(success == GL_FALSE){
            int len = glGetShaderi(fragmentID,GL_INFO_LOG_LENGTH);

            System.out.println("ERROR \n\t Fragment Shader Compile failed");
            System.out.println(glGetShaderInfoLog(fragmentID,len));
            assert false : "";
        }


        //link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID,vertexID);
        glAttachShader(shaderProgramID,fragmentID);
        glLinkProgram(shaderProgramID);

        success = glGetProgrami(shaderProgramID,GL_LINK_STATUS);
        if(success == GL_FALSE){
            int len = glGetProgrami(shaderProgramID,GL_INFO_LOG_LENGTH);

            System.out.println("ERROR \n\t Shaders Link failed");
            System.out.println(glGetProgramInfoLog(shaderProgramID,len));
            assert false : "";
        }


    }

    public void use(){
        if(!beingUsed) {
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    public void detach(){
        glUseProgram(0);
        beingUsed=false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4){
        int varLocation =  glGetUniformLocation(shaderProgramID,varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation,false,matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3){
        int varLocation =  glGetUniformLocation(shaderProgramID,varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation,false,matBuffer);
    }
    public void uploadVec4f(String varName, Vector4f vec){
        int varLocation =  glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform4f(varLocation,vec.x,vec.y,vec.z,vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec){
        int varLocation =  glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform3f(varLocation,vec.x,vec.y,vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec){
        int varLocation =  glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform2f(varLocation,vec.x,vec.y);
    }


    public void uploadFloat(String varName, float val){
        int varLocation =  glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform1f(varLocation,val);
    }

    public void uploadInt(String varName, int val){
        int varLocation =  glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform1i(varLocation,val);
    }


}
