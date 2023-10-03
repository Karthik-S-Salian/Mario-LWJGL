package components;

import jade.Component;

public class FontRenderer extends Component {

    @Override
    public void start() {
        super.start();
        if(gameObject.getComponent(SpriteRenderer.class)!=null){
            System.out.println("Found Font Renderer!");
        }
    }
}
