package jade;

import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20C.GL_MAX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private  int width;
    private  int height;
    private long glfwWindow;

    private ImGuiLayer imguiLayer;
    private String title;

    private static Scene currentScene;

    private static  Window window=null;


    public float r=1.0f,g=1.0f,b=1.0f;
    private Window(){
        this.width = 800;
        this.height = 600;
        this.title = "Mario";
    }

    public static  Window get(){
        if(Window.window == null){
            Window.window = new Window();
        }

        return Window.window;
    }

    public  void run(){
        System.out.println("Hello LWJGL "+ Version.getVersion()+"!");

        init();
        loop();

        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);


        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init(){
        GLFWErrorCallback.createPrint(System.err).set();

        //Initialise GLFW
        if(!glfwInit()){
            throw  new IllegalStateException("Unable to initialize GLFW.");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE,GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED,GLFW_TRUE);







        glfwWindow  = glfwCreateWindow(this.width,this.height,this.title,NULL,NULL);

        if(glfwWindow == NULL){
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        glfwSetCursorPosCallback(glfwWindow,MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow,MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow,MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow,KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow,(w,newWidth,newHeight)->{
            Window.setWindowSize(newWidth,newHeight);
        });

        {
            int[] width = {0};
            int[] height = {0};
            glfwGetWindowSize(glfwWindow, width, height);
            Window.setWindowSize(width[0],height[0]);
        }

        //make opengl context current
        glfwMakeContextCurrent(glfwWindow);

        //enable v-sync
        glfwSwapInterval(1);

        //make the window visible
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();  //very important

        glEnable(GL_BLEND);

        glBlendFunc(GL_ONE,GL_ONE_MINUS_SRC_ALPHA);

        this.imguiLayer = new ImGuiLayer(glfwWindow);
        imguiLayer.initImGui();

        Window.changeScene(0);

    }
    public void loop(){

        float beginTime = (float) glfwGetTime();
        float endTime = (float) glfwGetTime();
        float dt=-1.0f;

        while(!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();

            glClearColor(Math.max(r,0.0f),Math.max(g,0.0f),Math.max(b,0.0f),1.0f);
            glClear(GL_COLOR_BUFFER_BIT);


            if(dt>=0){
                currentScene.update(dt);
            }

            if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
                System.out.println("space key is pressed");
            }

            this.imguiLayer.update(dt,currentScene);
            glfwSwapBuffers(glfwWindow);

            endTime=(float) glfwGetTime();
            dt = endTime-beginTime;
            beginTime=endTime;
        }
        currentScene.saveExit();
    }

    public static void changeScene(int newScene){
        switch (newScene) {
            case 0 -> {
                currentScene = new LevelEditorScene();
            }
            case 1 -> {
                currentScene = new LevelScene();
            }
            default -> {
                assert false : "Unknown scene";
            }
        }
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static int getWidth(){
        return get().width;
    }

    public static int getHeight(){
        return get().height;
    }

    public static void setWidth(int width){
        get().width=width;
    }

    public static void setWindowSize(int width,int height){
        get().width=width;
        get().height = height;
    }

    public static void setHeight(int height){
        get().height=height;
    }

    public static Scene getScene(){
        return get().currentScene;
    }

}
