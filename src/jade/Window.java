package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private final int width;
    private final int height;
    private long glfwWindow;
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

        //make opengl context current
        glfwMakeContextCurrent(glfwWindow);

        //enable v-sync
        glfwSwapInterval(1);

        //make the window visible

        glfwShowWindow(glfwWindow);


        GL.createCapabilities();  //very important



        Window.changeScene(0);

    }
    public void loop(){

        float beginTime = Time.getTime();
        float endTime = Time.getTime();
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

            glfwSwapBuffers(glfwWindow);

            endTime=Time.getTime();
            dt = endTime-beginTime;
            beginTime=endTime;
        }
    }

    public static void changeScene(int newScene){
        switch (newScene) {
            case 0 -> {
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
            }
            case 1 -> {
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
            }
            default -> {
                assert false : "Unknown scene";
            }
        }
    }

    public static Scene getScene(){
        return get().currentScene;
    }

}
