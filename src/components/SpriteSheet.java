package components;

import org.joml.Vector2f;
import renderer.Texture;

import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {
    private Texture texture;

    private List<Sprite> sprites;


    public SpriteSheet(Texture texture,int spriteWidth,int spriteHeight,int numberOfSprites,int spacing){
        this.sprites = new ArrayList<>();

        this.texture = texture;
        int currentX = 0;
        int currentY = texture.getHeight() - spriteHeight;

        for(int i=0;i<numberOfSprites;i++){
            float topY = (currentY+spriteHeight)/(float)texture.getHeight();
            float bottomY =currentY /(float)texture.getHeight();
            float leftX =currentX /(float)texture.getWidth();
            float rightX = (currentX+spriteWidth)/(float)texture.getWidth();


            Vector2f[] texCoords = new Vector2f[]{
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY),
            };

            Sprite sprite = new Sprite();
            sprite.setTexture(texture);
            sprite.setTexCoords(texCoords);
            sprite.setWidth(spriteWidth);
            sprite.setHeight(spriteHeight);
            this.sprites.add(sprite);


            currentX += spriteWidth + spacing;
            if(currentX>=texture.getWidth()){
                currentY-=spriteHeight+spacing;
                currentX = 0;
            }
        }
    }


    public Sprite getSprite(int index){
        return this.sprites.get(index);
    }

    public int size(){
        return sprites.size();
    }
}
