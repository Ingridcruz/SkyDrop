package mx.itesm.skydrop;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

/**
 * Created by Ingrid on 01/10/2015.
 */
public class EscenaNivel1 extends EscenaBase {

    // Regiones para imágenes
    private ITextureRegion regionFondo;
    private ITextureRegion regionFondo2;
    // Sprite para el fondo
    private Sprite spriteFondo;
    private Sprite spriteFondo2;


    @Override
    public void cargarRecursos() {
        regionFondo = cargarImagen("escenario.jpg");
        regionFondo2 = cargarImagen("escenario2.jpg");
    }

    @Override
    public void crearEscena() {
        spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA / 2,regionFondo.getHeight()/2, regionFondo);
        attachChild(spriteFondo);

        spriteFondo2 = cargarSprite(ControlJuego.ANCHO_CAMARA/2,regionFondo2.getHeight()*1.5f, regionFondo2);
        attachChild(spriteFondo2);
    }

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);
        spriteFondo.setY(spriteFondo.getY()-5);
        spriteFondo2.setY(spriteFondo2.getY()-5);
        if (spriteFondo.getY()<-spriteFondo.getHeight()/2){
            spriteFondo.setY(1.5f*spriteFondo.getHeight()-5);

        }
        if (spriteFondo2.getY()<-spriteFondo2.getHeight()/2){
            spriteFondo2.setY(1.5f*spriteFondo2.getHeight()-5);

        }

    }

    @Override
    public void onBackKeyPressed() {
        // Regresar al menú principal
        admEscenas.crearEscenaMenu();
        admEscenas.setEscena(TipoEscena.ESCENA_MENU);
        admEscenas.liberarEscenaNivel1();
    }


    @Override
    public TipoEscena getTipoEscena() {
        return TipoEscena.ESCENA_NIVEL1;
    }

    @Override
    public void liberarEscena() {
        this.detachSelf();
        this.dispose();
    }

    @Override
    public void liberarRecursos() {
        regionFondo.getTexture().unload();
        regionFondo = null;
    }
}
