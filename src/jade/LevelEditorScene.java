package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.SpriteRenderer;
import components.SpriteSheet;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene{
    private  GameObject obj1;
    public LevelEditorScene(){

    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f());

        if(levelLoaded){
            return;
        }

        //SpriteSheet sprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

        obj1 = new GameObject("object 1",new Transform(new Vector2f(100,100),new Vector2f(256,256)),1);
        SpriteRenderer obj1SpriteRenderer = new SpriteRenderer();
        obj1SpriteRenderer.setColor(new Vector4f(0,1,0,1.0f));
        obj1.addComponent(obj1SpriteRenderer);
        this.addGameObjectToScene(obj1);
        this.activeGameObject=obj1;

        GameObject obj2 = new GameObject("object 1",new Transform(new Vector2f(300,100),new Vector2f(256,256)),0);
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        obj2SpriteRenderer.setColor(new Vector4f(1,0,0,0.7f));
        obj2.addComponent(obj2SpriteRenderer);
        this.addGameObjectToScene(obj2);
    }

    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spritesheet.png",new SpriteSheet(AssetPool.getTexture("assets/images/spritesheet.png"),16,16,26,0));
    }

    @Override
    public void update(float dt) {
        //obj1.transform.position.x += 710*dt;
        for(GameObject go:this.gameObjects){
            go.update(dt);
        }
    this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("test window");
        ImGui.text("lorem cojcoj");
        ImGui.end();
    }
}
