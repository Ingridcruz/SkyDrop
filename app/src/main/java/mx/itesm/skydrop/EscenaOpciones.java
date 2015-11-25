package mx.itesm.skydrop;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

/**
 * Created by rmroman on 11/09/15.
 */
public class EscenaOpciones extends EscenaBase {
    // Regiones para imágenes
    private ITextureRegion regionFondo;
    private ITextureRegion regionScore;


    // Sprite para el fondo
    private Sprite spriteFondo;
    private Sprite spriteScore;
    private IFont fuente;
    private ButtonSprite sonido;
    private ITiledTextureRegion regionSonido;

    @Override
    public void cargarRecursos() {
        regionFondo = cargarImagen("fondos/options.jpg");
        fuente = cargarFont("san.ttf");
        regionScore = cargarImagen("botones/resetscor.png");
        regionSonido = cargarImagenMosaico("sound.png", 360, 180, 1, 2);
    }

    private IFont cargarFont(String archivo) {
        // La imagen que contiene cada símbolo
        final ITexture fontTexture = new BitmapTextureAtlas(actividadJuego.getEngine().getTextureManager(), 512, 256);
        // Carga el archivo, tamaño 56, antialias y color
        Font tipoLetra = FontFactory.createFromAsset(actividadJuego.getEngine().getFontManager(),
                fontTexture, actividadJuego.getAssets(), archivo, 80, true, 0xFFFFFFFF);
        tipoLetra.load();
        tipoLetra.prepareLetters("01234567890.".toCharArray());

        return tipoLetra;
    }

    @Override
    public void crearEscena() {
        spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA / 2, ControlJuego.ALTO_CAMARA / 2, regionFondo);
        attachChild(spriteFondo);
        agregarTextoPuntos();

        agregarSonido();
    }

    private void agregarSonido() {
        sonido = new ButtonSprite(400, 150,
                regionSonido,actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {

                if (pSceneTouchEvent.isActionDown()) {
                    actividadJuego.reproducirMusica("music.mp3", false);
                     // Cambia el índice entre 0 y 1 ed manera alternada
                    sonido.setCurrentTileIndex((sonido.getCurrentTileIndex() + 1) % 2);
                }
                // 0-NORMAL, 1-PRESIONADO
                Log.i("Estado del botón", "" + sonido.getCurrentTileIndex());
                return false; // Regresa falso para que Android no cambie el botón
            }
        };
        // El estado inicial del botón se lee desde las preferencias o se toma un valor por default
        // en este demo, siempre inicia prendido
        sonido.setCurrentTileIndex(1);
        registerTouchArea(sonido);
        attachChild(sonido);

    }




    private void agregarTextoPuntos() {
        final Text txtPuntos = new Text(400,600, fuente, "0000000000", actividadJuego.getVertexBufferObjectManager());
        attachChild(txtPuntos);

        SharedPreferences preferencias = actividadJuego.getSharedPreferences("marcadorAlto", Context.MODE_PRIVATE);
        final int valor = preferencias.getInt("puntos", 0);
        txtPuntos.setText("" + valor);
        Sprite spriteScore = new Sprite(400, 400,
                regionScore, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {

                    SharedPreferences preferencias = actividadJuego.getSharedPreferences("marcadorAlto", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putInt("puntos",0);
                    editor.commit();
                    txtPuntos.setText("" +0);
                    int valor =0 ;

                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        spriteFondo.attachChild(spriteScore);
        registerTouchArea(spriteScore);


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
