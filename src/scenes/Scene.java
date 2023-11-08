package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import imgui.ImGui;
import jade.Camera;
import jade.GameObject;
import jade.GameObjectDeserializer;
import renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected GameObject activeGameObject = null;

    protected Camera camera;

    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected boolean levelLoaded=false;

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

    public void saveExit(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class,new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class,new GameObjectDeserializer())
                .create();

        try {
            FileWriter writer = new FileWriter("level.json");
            writer.write(gson.toJson(this.gameObjects));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class,new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class,new GameObjectDeserializer())
                .create();
//        String serialze = gson.toJson(obj1);
//        System.out.println(serialze);
//        GameObject go =  gson.fromJson(serialze,GameObject.class);
//        System.out.println(go);


        String inFile="";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!inFile.isEmpty()){
            int maxGoId =-1;
            int maxCompId = -1;
            GameObject[] gos = gson.fromJson(inFile,GameObject[].class);
            for (GameObject go:gos){
                addGameObjectToScene(go);
                for(Component c: go.getAllComponents()){
                    if(c.getUid()>maxCompId){
                        maxCompId=c.getUid();
                    }
                }
                if(go.getUid()>maxGoId){
                    maxGoId=go.getUid();
                }
            }
            this.levelLoaded = true;

            maxGoId++;
            maxCompId++;
            GameObject.init(maxGoId);
            Component.init(maxCompId);
        }
    }

}
