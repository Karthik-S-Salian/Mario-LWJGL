package scenes;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import jade.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene {
    private SpriteSheet sprites;

    private MouseControls mouseControls = new MouseControls();

    @Override
    public void init() {
        loadResources();
        sprites = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
        this.camera = new Camera(new Vector2f());

        if(levelLoaded){
            this.activeGameObject=gameObjects.get(0);
            return;
        }

        //SpriteSheet sprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

        GameObject obj1 = new GameObject("object 1",new Transform(new Vector2f(100,100),new Vector2f(256,256)),1);
        SpriteRenderer obj1SpriteRenderer = new SpriteRenderer();
        obj1SpriteRenderer.setColor(new Vector4f(0,1,0,1.0f));
        obj1.addComponent(obj1SpriteRenderer);
        //obj1.addComponent(new RigidBody());
        this.addGameObjectToScene(obj1);
        this.activeGameObject=obj1;

        GameObject obj2 = new GameObject("object 2",new Transform(new Vector2f(300,100),new Vector2f(256,256)),0);
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        obj2SpriteRenderer.setColor(new Vector4f(1,0,0,0.7f));
        obj2.addComponent(obj2SpriteRenderer);
        this.addGameObjectToScene(obj2);
    }

    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",new SpriteSheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),16,16,81,0));
    }

    @Override
    public void update(float dt) {
        //obj1.transform.position.x += 710*dt;
        mouseControls.update(dt);
        for(GameObject go:this.gameObjects){
            go.update(dt);
        }
    this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("test window");
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x+windowSize.x;

//        ImVec2 lastButtonPos = new ImVec2(0,0);
//        for(int i=0;i<sprites.size();i++){
//            Sprite sprite = sprites.getSprite(i);
//            float spriteWidth = sprite.getWidth();
//            float spriteHeight = sprite.getHeight();
//            int id = sprite.getTextureId();
//            Vector2f[] texCoords = sprite.getTexCoords();
//
//            if(i!=0){
//                ImGui.sameLine();
//            }
//
//            float lastButtonX2 = lastButtonPos.x;
//            if(lastButtonPos.x+ itemSpacing.x + spriteWidth>windowX2){
//                ImGui.newLine();
//            }
//
//            if(ImGui.imageButton(id,spriteWidth,spriteHeight,texCoords[0].x,texCoords[0].y,texCoords[2].x,texCoords[2].y)){
//                System.out.println("Button "+i+" clicked");
//            }
//            ImGui.getItemRectMax(lastButtonPos);
//        }

        for(int i=0;i<sprites.size();i++){
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth();
            float spriteHeight = sprite.getHeight();
            int id = sprite.getTextureId();
            Vector2f[] texCoords = sprite.getTexCoords();


            ImGui.pushID(i);
            if(ImGui.imageButton(id,spriteWidth,spriteHeight,texCoords[0].x,texCoords[0].y,texCoords[2].x,texCoords[2].y)){
                GameObject gameObject = Prefabs.generateSpriteObject(sprite,spriteWidth,spriteHeight);
                mouseControls.pickupObject(gameObject);
            }

            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);

            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if(i+1<sprites.size() && nextButtonX2<windowX2){
                ImGui.sameLine();
            }
        }
        ImGui.end();
    }
}


