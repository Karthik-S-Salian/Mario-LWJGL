package jade;

import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX,scrollY,xPos,yPos,lastX,lastY;
    private boolean mouseButtonPressed[] =  new boolean[9];
    private boolean isDragging;

    private MouseListener(){
        scrollX=0.0;
        scrollY=0.0;
        xPos=0.0;
        yPos=0.0;
        lastX=0.0;
        lastY=0.0;

    }

    public static MouseListener get(){
        if(MouseListener.instance == null){
            MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    public static void mousePosCallback(long window,double xpos,double ypos){
        get().lastX = get().xPos;
        get().lastY = get().yPos;

        get().xPos = xpos;
        get().yPos = ypos;

        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window,int button,int action,int mods){
        if(button>=get().mouseButtonPressed.length){
            return;
        }
        if(action==GLFW_PRESS){
            get().mouseButtonPressed[button]=true;
        }else if(action==GLFW_RELEASE){
            get().mouseButtonPressed[button]=false;
            get().isDragging = false;
        }
    }

    public static void mouseScrollCallback(long window,double xOffset,double yOffset){
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame(){
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getScrollX() {
        return (float)get().scrollX;
    }

    public static float getScrollY() {
        return (float)get().scrollY;
    }

    public static float getX() {
        return (float)get().xPos;
    }

    public static float getY() {
        return (float)get().yPos;
    }


    public static float getDx() {
        return (float)(get().xPos-get().lastX);
    }

    public static float getDy() {
        return (float)(get().yPos-get().lastY);
    }

    public static boolean isDragging(){
        return get().isDragging;
    }

    public static float getOrthoX(){
        float currentX = (MouseListener.getX()/(float) Window.getWidth())*2.0f-1.0f;
        Vector4f temp = new Vector4f(currentX,0,0,1);
        temp.mul(Window.getScene().camera().getInverseProjection()).mul(Window.getScene().camera().getInverseView());
        return temp.x;
    }

    public static float getOrthoY(){
        float currentY = ((Window.getHeight()-MouseListener.getY())/(float) Window.getHeight())*2.0f-1.0f;
        Vector4f temp = new Vector4f(0,currentY,0,1);
        temp.mul(Window.getScene().camera().getInverseProjection()).mul(Window.getScene().camera().getInverseView());
        return temp.y;
    }

    public  static  boolean mouseButtonDown(int button){
        if(button<get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        }else{
            return false;
        }
    }


}
