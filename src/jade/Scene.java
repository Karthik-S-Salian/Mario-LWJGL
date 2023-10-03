package jade;

import imgui.ImGui;
import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected GameObject activeGameObject = null;

    protected Camera camera;

    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();

    public Scene(){}

    public void init(){

    }

    public void start(){
        for(GameObject go: gameObjects){
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go){
        gameObjects.add(go);
        if(isRunning){
            go.start();
            this.renderer.add(go);
        }
    }

    public Camera camera(){
        return this.camera;
    }

    public abstract void update(float dt);


    public void sceneImgui(){
        if(activeGameObject!=null){
            ImGui.begin("Inspector");
            activeGameObject.imgui();
            ImGui.end();
        }
        imgui();
    }

    public void imgui(){

    }

}
