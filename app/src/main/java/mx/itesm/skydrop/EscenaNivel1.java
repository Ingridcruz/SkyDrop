package mx.itesm.skydrop;

import android.util.Log;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
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
    private int vidas= 3;

    private boolean juegoCorriendo = true;

    // Fin del juego
    private ITextureRegion regionWin;
    private ITextureRegion regionFin;
        private ITextureRegion regionFondo;
        private ITextureRegion regionFondo2;
    private ITextureRegion regionCorazon;
    private AnimatedSprite spritePersonaje;
    private TiledTextureRegion regionPersonajeAnimado;
    private Text txtMarcador; // Por ahora con valorMarcador
    private Text txtVidas;
    private IFont fontSan;
    // Marcador (valorMarcador)
    private int valorMarcador= 0;    // Aumenta 100 puntos por cada moneda
    private int valorVidas= 3;
    // HUD (Heads-Up Display)
    private HUD hud;
    // Sprite para el fondo
        private Sprite spriteFondo;
        private Sprite spriteFondo2;
    private Sprite spriteCorazon;
    private ArrayList<Enemigo> listaSobres;
    private ITextureRegion regionEnemigo;

    private ITextureRegion regionNube;
    private ArrayList<Nube> listaNube;

    // Tiempo para generar listaSobres
    private float tiempoEnemigos = 0;
    private float LIMITE_TIEMPO = 7.5f;
    private float tiempoNube = 0;
    private float LIMITE_TIEMPON = 5.5f;
    private CameraScene escenaPausa;    // La escena que se muestra al hacer pausa
    private ITextureRegion regionPausa;
    private ITextureRegion regionBtnPausa;

        @Override
        public void cargarRecursos() {
            regionCorazon = cargarImagen("Corazon.png");
            regionFondo = cargarImagen("fond1.jpg");
            regionFondo2 = cargarImagen("fond1.jpg");
            regionPersonajeAnimado = cargarImagenMosaico("line.png", 488, 101, 1, 4);
            regionEnemigo=cargarImagen("carta.png");
           fontSan = cargarFont("san.ttf");
            regionNube=cargarImagen("nubeobscura.png");
            regionFin = cargarImagen("gameover.png");
            regionWin = cargarImagen("win.png");
            regionBtnPausa = cargarImagen("pause.png");
            regionPausa = cargarImagen("gameover.png");

        }

         private IFont cargarFont(String archivo) {
        // La imagen que contiene cada símbolo
         final ITexture fontTexture = new BitmapTextureAtlas(actividadJuego.getEngine().getTextureManager(),512,256);
        // Carga el archivo, tamaño 56, antialias y color
         Font tipoLetra = FontFactory.createFromAsset(actividadJuego.getEngine().getFontManager(),
        fontTexture, actividadJuego.getAssets(), archivo, 40, true, 0xFF00FF00);
        tipoLetra.load();
        tipoLetra.prepareLetters("Score: 01234567890.".toCharArray());

         return tipoLetra;
   }


    @Override
        public void crearEscena() {
        spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA / 2, regionFondo.getHeight() / 2, regionFondo);
        attachChild(spriteFondo);
        spriteFondo2 = cargarSprite(ControlJuego.ANCHO_CAMARA / 2, regionFondo2.getHeight() * 1.5f, regionFondo2);
        attachChild(spriteFondo2);
        spriteCorazon = cargarSprite(55, 1230, regionCorazon);
        attachChild(spriteCorazon);
        spritePersonaje = new AnimatedSprite(ControlJuego.ANCHO_CAMARA / 2, 50,
                regionPersonajeAnimado, actividadJuego.getVertexBufferObjectManager());
        spritePersonaje.animate(150);   // 200ms entre frames, 1000/200 fps
        attachChild(spritePersonaje);
        actividadJuego.getEngine().enableAccelerationSensor(actividadJuego, this);

        // Lista de enemigos que aparecen del lado derecho
        listaSobres = new ArrayList<>();
        listaNube = new ArrayList<>();

        // Agregar flechas y el txtMarcador/valorMarcador
      agregarHUD();

        // Crea el botón de PAUSA y lo agrega a la escena
        Sprite btnPausa = new Sprite(700, ControlJuego.ALTO_CAMARA - regionBtnPausa.getHeight(),
                regionBtnPausa, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    pausarJuego();
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        attachChild(btnPausa);
        registerTouchArea(btnPausa);

        // Crear la escena de PAUSA, pero NO lo agrega a la escena
        escenaPausa = new CameraScene(actividadJuego.camara);
        Sprite fondoPausa = cargarSprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2,
                regionPausa);
        escenaPausa.attachChild(fondoPausa);
        escenaPausa.setBackgroundEnabled(false);
    }

    private void pausarJuego() {
        if (juegoCorriendo) {
            setChildScene(escenaPausa,false,true,false);
            juegoCorriendo = false;
        } else {
            clearChildScene();
            juegoCorriendo = true;
        }
    }



    private void agregarHUD() {
        hud = new HUD();
        txtMarcador = new Text(ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA-100,
               fontSan,"Puntos: 00000      ",actividadJuego.getVertexBufferObjectManager());
       hud.attachChild(txtMarcador);
        txtVidas = new Text(42,1230,
                fontSan,"Puntos: 00000      ",actividadJuego.getVertexBufferObjectManager());
        hud.attachChild(txtVidas);
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
                puntos = puntos+500;
                Log.i("ENERGIA", "Score: " + puntos);
                valorMarcador=puntos;
                if (puntos==5000) {
                    juegoCorriendo=false;
                    // Agrega pantalla de fin
                    Sprite spriteWin = new Sprite(ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA/2,
                            regionWin,actividadJuego.getVertexBufferObjectManager()) ;
                    attachChild(spriteWin);
                }


            }
        }


        // Acumular tiempo
        tiempoNube += pSecondsElapsed;
        if (tiempoNube > LIMITE_TIEMPON) {
            // Se cumplió el tiempo
            tiempoNube = 0;
            if (LIMITE_TIEMPON > 0.5f) {
                LIMITE_TIEMPON -= 0.15f;
            }
            Sprite spriteNube = cargarSprite((float) (Math.random() * ControlJuego.ANCHO_CAMARA - regionNube.getWidth()) + regionNube.getWidth(), ControlJuego.ALTO_CAMARA + regionNube.getHeight(), regionNube);
            Nube nuevoNube = new Nube(spriteNube);
            listaNube.add(nuevoNube);
            attachChild(nuevoNube.getSpriteNube());
            Log.i("Tamaño", "Datos: " + listaNube.size());


        }


        // Actualizar cada uno de los listaSobres y ver si alguno ya salió de la pantalla
        for (int i = listaNube.size() - 1; i >= 0; i--) {
            Nube nube = listaNube.get(i);
            nube.mover(0, -10);
            if (nube.getSpriteNube().getY() < -nube.getSpriteNube().getHeight()) {
                detachChild(nube.getSpriteNube());
                listaNube.remove(nube);
            }
            if (spritePersonaje.collidesWith(nube.getSpriteNube())) {
                detachChild(nube.getSpriteNube());
                vidas=vidas-1;
                listaNube.remove(nube);
                valorVidas=vidas;
                if (vidas==0) {
                    juegoCorriendo=false;
                    // Agrega pantalla de fin
                    Sprite spriteFin = new Sprite(ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA/2,
                            regionFin,actividadJuego.getVertexBufferObjectManager()) ;
                    attachChild(spriteFin);
                }
            }
        }

       txtMarcador.setText("Score : " + (valorMarcador));
        txtVidas.setText(" "+valorVidas+" ");
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
            liberarRecursos();
            this.detachSelf();
            this.dispose();
        }
        @Override
        public void liberarRecursos() {
            actividadJuego.camara.setHUD(null);
            actividadJuego.getEngine().disableAccelerationSensor(actividadJuego);
            regionFondo.getTexture().unload();
            regionFondo = null;
            regionPersonajeAnimado.getTexture().unload();
            regionPersonajeAnimado = null;
            regionEnemigo.getTexture().unload();
            regionEnemigo = null;
            regionFin.getTexture().unload();
            regionFin = null;
            regionWin.getTexture().unload();
            regionWin = null;
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
        regionWin.getTexture().unload();
        regionWin = null;
        regionBtnPausa.getTexture().unload();
        regionBtnPausa = null;
        regionPausa.getTexture().unload();
        regionPausa = null;

    }


    }
