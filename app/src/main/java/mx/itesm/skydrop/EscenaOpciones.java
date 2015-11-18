package mx.itesm.skydrop;

import android.content.Context;
import android.content.SharedPreferences;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;

/**
 * Created by rmroman on 11/09/15.
 */
public class EscenaOpciones extends EscenaBase
{
    // Regiones para imágenes
    private ITextureRegion regionFondo;
    // Sprite para el fondo
    private Sprite spriteFondo;
    private IFont fuente;

    @Override
    public void cargarRecursos() {
        regionFondo = cargarImagen("MenuOpciones.jpg");
  fuente=cargarFont("san.ttf");
    }
    private IFont cargarFont(String archivo) {
        // La imagen que contiene cada símbolo
        final ITexture fontTexture = new BitmapTextureAtlas(actividadJuego.getEngine().getTextureManager(),512,256);
        // Carga el archivo, tamaño 56, antialias y color
        Font tipoLetra = FontFactory.createFromAsset(actividadJuego.getEngine().getFontManager(),
                fontTexture, actividadJuego.getAssets(), archivo, 40, true, 0xFF00FF00);
        tipoLetra.load();
        tipoLetra.prepareLetters("Total Scre: 01234567890.".toCharArray());

        return tipoLetra;
    }
    @Override
    public void crearEscena() {
        spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2, regionFondo);
        attachChild(spriteFondo);
        agregarTextoPuntos();
    }

    private void agregarTextoPuntos() {
        Text txtPuntos=new Text(600,400,fuente,"Total score: 00000000",actividadJuego.getVertexBufferObjectManager());
        attachChild(txtPuntos);

        SharedPreferences preferencias= actividadJuego.getSharedPreferences("marcadorAlto", Context.MODE_PRIVATE);
        int valor =preferencias.getInt("puntos",0);
        txtPuntos.setText("Total Score:"+valor);
    }

    @Override
    public void onBackKeyPressed() {
        // Regresar al menú principal
        admEscenas.crearEscenaMenu();
        admEscenas.setEscena(TipoEscena.ESCENA_MENU);
        admEscenas.liberarEscenaOpciones();
    }

    @Override
    public TipoEscena getTipoEscena() {
        return TipoEscena.ESCENA_OPCIONES;
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
