package mx.itesm.skydrop;

import org.andengine.entity.sprite.Sprite;

/**
 * Created by Ingrid on 21/10/2015.
 */
public class Gusano {
    private Sprite spriteGusano;

    public Gusano(Sprite spriteGusano) {
        this.spriteGusano = spriteGusano;
    }

    public Sprite getSpriteGusano() {
        return spriteGusano;
    }

    public void setSpriteGusano(Sprite spriteGusano) {
        this.spriteGusano = spriteGusano;
    }

    public void mover(int dx, int dy) {
        spriteGusano.setPosition(spriteGusano.getX() + dx, spriteGusano.getY() + dy);
    }



}


