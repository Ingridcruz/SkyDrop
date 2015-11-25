package mx.itesm.skydrop;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

/**
 * Created by Ingrid on 21/10/2015.
 */
public class Bomba {
    private Sprite spriteBomba;

    public Bomba(Sprite spriteBomba) {
        this.spriteBomba = spriteBomba;
    }

    public Sprite getSpriteBomba() {
        return spriteBomba;
    }

    public void setSpriteBomba(Sprite spriteBomba) {
        this.spriteBomba = spriteBomba;
    }

    public void mover(int dx, int dy) {
        spriteBomba.setPosition(spriteBomba.getX() + dx, spriteBomba.getY() + dy);
    }



}


