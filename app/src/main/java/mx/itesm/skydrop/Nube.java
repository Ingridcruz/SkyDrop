package mx.itesm.skydrop;

import org.andengine.entity.sprite.Sprite;

/**
 * Created by Ingrid on 21/10/2015.
 */
public class Nube {
    private Sprite spriteNube;

    public Nube(Sprite spriteNube) {
        this.spriteNube = spriteNube;
    }

    public Sprite getSpriteNube() {
        return spriteNube;
    }

    public void setSpriteNube(Sprite spriteNube) {
        this.spriteNube = spriteNube;
    }

    public void mover(int dx, int dy) {
        spriteNube.setPosition(spriteNube.getX() + dx, spriteNube.getY() + dy);
    }
}


