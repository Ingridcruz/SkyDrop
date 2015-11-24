package mx.itesm.skydrop;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;


public class EscenaHistoria extends EscenaBase
{
    // Regiones para imÃ¡genes
    private ITextureRegion regionFondo;
    // Sprite para el fondo
    private Sprite spriteFondo;

    @Override
    public void cargarRecursos() {
        regionFondo = cargarImagen("hitoria.jpg");

    }

    @Override
    public void crearEscena() {
        spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2, regionFondo);
        attachChild(spriteFondo);
    }

    @Override
    public void onBackKeyPressed() {
        // Regresar al menÃº principal
        admEscenas.crearEscenaMenu();
        admEscenas.setEscena(TipoEscena.ESCENA_MENU);
        admEscenas.liberarEscenaRules();
    }

    @Override
    public TipoEscena getTipoEscena() {
        return TipoEscena.ESCENA_HISTORIA;
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
