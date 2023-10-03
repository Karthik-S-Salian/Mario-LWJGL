package jade;

import components.Sprite;
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

        //SpriteSheet sprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

        obj1 = new GameObject("object 1",new Transform(new Vector2f(100,100),new Vector2f(256,256)),1);
        obj1.addComponent(new SpriteRenderer(new Vector4f(0,1,0,1.0f)));
        this.addGameObjectToScene(obj1);
        this.activeGameObject=obj1;

        GameObject obj2 = new GameObject("object 1",new Transform(new Vector2f(300,100),new Vector2f(256,256)),0);
        obj2.addComponent(new SpriteRenderer(new Vector4f(1,0,0,0.7f)));
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
