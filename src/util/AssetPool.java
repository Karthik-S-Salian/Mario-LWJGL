package util;

import components.SpriteSheet;
import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    public static Shader getShader(String resourceName){
        File file = new File(resourceName);

        if(AssetPool.shaders.containsKey(file.getAbsolutePath())){
            return AssetPool.shaders.get(file.getAbsolutePath());
        }else {
            Shader shader = new Shader(resourceName);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName){
        File file = new File(resourceName);

        if(AssetPool.textures.containsKey(file.getAbsolutePath())){
            return AssetPool.textures.get(file.getAbsolutePath());
        }else {
            Texture texture = new Texture(resourceName);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }


    public static void addSpriteSheet(String resourceName, SpriteSheet spriteSheet){
        File file = new File(resourceName);
        AssetPool.spriteSheets.putIfAbsent(file.getAbsolutePath(),spriteSheet);
    }


    public static SpriteSheet getSpriteSheet(String resourceName){
        File file = new File(resourceName);
        assert AssetPool.spriteSheets.containsKey(file.getAbsolutePath()) : "Error: Tried to access sprite sheet"+resourceName+"which has not yet been added to assetpool";
        return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(),null);
    }


}
