package mx.itesm.skydrop;

import org.andengine.entity.sprite.Sprite;

/**
 * Created by Ingrid on 12/10/2015.
 */
public class Enemigo extends EscenaNivel1{

        private Sprite spriteEnemigo;

        public Enemigo(Sprite sprite) {
            spriteEnemigo = sprite;
        }

        public Sprite getSpriteEnemigo() {
            return spriteEnemigo;
        }

        public void setSpriteEnemigo(Sprite spriteEnemigo) {
            this.spriteEnemigo = spriteEnemigo;
        }

        public void mover(int dx, int dy) {
            spriteEnemigo.setPosition( spriteEnemigo.getX()+dx, spriteEnemigo.getY()+dy );
        }
    }

