package mx.itesm.skydrop;

import android.util.Log;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import java.util.ArrayList;

/**
 * Created by Ingrid on 01/10/2015.
 */
public class EscenaNivel1 extends EscenaBase implements IAccelerationListener {

    private int puntos = 0;

    private boolean juegoCorriendo = true;

    // Fin del juego
    private ITextureRegion regionFin;
        private ITextureRegion regionFondo;
        private ITextureRegion regionFondo2;
    private AnimatedSprite spritePersonaje;
    private TiledTextureRegion regionPersonajeAnimado;
    private Text txtMarcador; // Por ahora con valorMarcador
    private IFont fontSan;
    // Marcador (valorMarcador)
    private float valorMarcador;    // Aumenta 100 puntos por cada moneda
    // HUD (Heads-Up Display)
    private HUD hud;
    // Sprite para el fondo
        private Sprite spriteFondo;
        private Sprite spriteFondo2;
    private ArrayList<Enemigo> listaSobres;
    private ITextureRegion regionEnemigo;

    // Tiempo para generar listaSobres
    private float tiempoEnemigos = 0;
    private float LIMITE_TIEMPO = 2.5f;


        @Override
        public void cargarRecursos() {
            regionFondo = cargarImagen("escenario.jpg");
            regionFondo2 = cargarImagen("escenario2.jpg");
            regionPersonajeAnimado = cargarImagenMosaico("pajaro.jpg", 128, 29, 1, 3);
            regionEnemigo=cargarImagen("carta.png");
           fontSan = cargarFont("san.ttf");


        }

         private IFont cargarFont(String archivo) {
        // La imagen que contiene cada símbolo
         final ITexture fontTexture = new BitmapTextureAtlas(actividadJuego.getEngine().getTextureManager(),512,256);
        // Carga el archivo, tamaño 56, antialias y color
         Font tipoLetra = FontFactory.createFromAsset(actividadJuego.getEngine().getFontManager(),
              fontTexture, actividadJuego.getAssets(), archivo, 56, true, 0xFF00FF00);
        tipoLetra.load();
        tipoLetra.prepareLetters("Puntos: 01234567890.".toCharArray());

         return tipoLetra;
   }


    @Override
        public void crearEscena() {
        spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA / 2, regionFondo.getHeight() / 2, regionFondo);
        attachChild(spriteFondo);
        spriteFondo2 = cargarSprite(ControlJuego.ANCHO_CAMARA / 2, regionFondo2.getHeight() * 1.5f, regionFondo2);
        attachChild(spriteFondo2);
        spritePersonaje = new AnimatedSprite(ControlJuego.ANCHO_CAMARA / 2, 18,
                regionPersonajeAnimado, actividadJuego.getVertexBufferObjectManager());
        spritePersonaje.animate(150);   // 200ms entre frames, 1000/200 fps
        attachChild(spritePersonaje);
        actividadJuego.getEngine().enableAccelerationSensor(actividadJuego, this);

        // Lista de enemigos que aparecen del lado derecho
        listaSobres = new ArrayList<>();
        
        // Agregar flechas y el txtMarcador/valorMarcador
      agregarHUD();


    }

    private void agregarHUD() {
        hud = new HUD();
        txtMarcador = new Text(ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA-100,
               fontSan,"    0000    ",actividadJuego.getVertexBufferObjectManager());
       hud.attachChild(txtMarcador);
       valorMarcador = 0;

        actividadJuego.camara.setHUD(hud);
    }


    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);
        spriteFondo.setY(spriteFondo.getY() - 5);
        spriteFondo2.setY(spriteFondo2.getY()-5);
        if (spriteFondo.getY()<-spriteFondo.getHeight()/2){
            spriteFondo.setY(1.5f*spriteFondo.getHeight()-5);
        }
        if (spriteFondo2.getY()<-spriteFondo2.getHeight()/2){
            spriteFondo2.setY(1.5f*spriteFondo2.getHeight()-5);
        }

        if (!juegoCorriendo) {
            return;
        }
        // Acumular tiempo
        tiempoEnemigos += pSecondsElapsed;
        if (tiempoEnemigos > LIMITE_TIEMPO) {
            // Se cumplió el tiempo
            tiempoEnemigos = 0;
            if (LIMITE_TIEMPO > 0.5f) {
                LIMITE_TIEMPO -= 0.15f;
            }
            Sprite spriteEnemigo = cargarSprite((float) (Math.random() * ControlJuego.ANCHO_CAMARA - regionEnemigo.getWidth()) + regionEnemigo.getWidth(), ControlJuego.ALTO_CAMARA + regionEnemigo.getHeight(), regionEnemigo);
            Enemigo nuevoEnemigo = new Enemigo(spriteEnemigo);
            listaSobres.add(nuevoEnemigo);
            attachChild(nuevoEnemigo.getSpriteEnemigo());
            Log.i("Tamaño", "Datos: " + listaSobres.size());
        }


        // Actualizar cada uno de los listaSobres y ver si alguno ya salió de la pantalla
        for (int i = listaSobres.size() - 1; i >= 0; i--) {
            Enemigo enemigo = listaSobres.get(i);
            enemigo.mover(0, -10);
            if (enemigo.getSpriteEnemigo().getY() < -enemigo.getSpriteEnemigo().getHeight()) {
                detachChild(enemigo.getSpriteEnemigo());
                listaSobres.remove(enemigo);
            }
            if (spritePersonaje.collidesWith(enemigo.getSpriteEnemigo())) {
                detachChild(enemigo.getSpriteEnemigo());
                listaSobres.remove(enemigo);
                puntos = puntos+10;
                Log.i("ENERGIA", "Puntos: " + puntos);

            }
        }

       txtMarcador.setText("Puntos : " + (valorMarcador));

    }
    // Recude el tamaño hasta desaparecer
    private void desaparecerSobre(final Sprite sobreD) {
        ScaleModifier escala = new ScaleModifier(0.3f,1,0) {
            @Override
            protected void onModifierFinished(IEntity pItem) {
                unregisterEntityModifier(this);
                super.onModifierFinished(pItem);
            }
        };
        sobreD.registerEntityModifier(escala);
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

    @Override
    public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {

    }

    @Override
    public void onAccelerationChanged(AccelerationData pAccelerationData) {
        float dx = pAccelerationData.getX();
        //desplazamiento en x

        float dy = 0;
        //desplazamiento en y

        if(dx>=0) {
            if(spritePersonaje.getX()<=800) {
                spritePersonaje.setX(spritePersonaje.getX() + dx);
            }
        }
        if(dx<=0) {
            if(spritePersonaje.getX()>=0) {
                spritePersonaje.setX(spritePersonaje.getX() + dx);
            }
        }
    }
    public void liberarRecurso () {
        // Detiene el acelerómetro
        actividadJuego.getEngine().disableAccelerationSensor(actividadJuego);

        regionFondo.getTexture().unload();
        regionFondo = null;
        regionPersonajeAnimado.getTexture().unload();
        regionPersonajeAnimado = null;
        regionEnemigo.getTexture().unload();
        regionEnemigo = null;
        regionFin.getTexture().unload();
        regionFin = null;

    }


    }
