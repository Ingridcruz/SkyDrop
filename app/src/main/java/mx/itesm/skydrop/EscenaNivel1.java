package mx.itesm.skydrop;

import android.util.Log;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import java.util.ArrayList;

/**
 * Created by Ingrid on 01/10/2015.
 */
public class EscenaNivel1 extends EscenaBase implements IAccelerationListener {

    private int energia = 100;

    private boolean juegoCorriendo = true;

    // Fin del juego
    private ITextureRegion regionFin;
        private ITextureRegion regionFondo;
        private ITextureRegion regionFondo2;
    private AnimatedSprite spritePersonaje;
    private TiledTextureRegion regionPersonajeAnimado;
        // Sprite para el fondo
        private Sprite spriteFondo;
        private Sprite spriteFondo2;
    private ArrayList<Enemigo> listaEnemigos;
    private ITextureRegion regionEnemigo;
    // Tiempo para generar listaEnemigos
    private float tiempoEnemigos = 0;
    private float LIMITE_TIEMPO = 2.5f;


        @Override
        public void cargarRecursos() {
            regionFondo = cargarImagen("escenario.jpg");
            regionFondo2 = cargarImagen("escenario2.jpg");
            regionPersonajeAnimado = cargarImagenMosaico("pajaro.jpg", 128, 29, 1, 3);
            regionEnemigo=cargarImagen("nube.png");
        }


    @Override
        public void crearEscena() {
            spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA / 2,regionFondo.getHeight()/2, regionFondo);
            attachChild(spriteFondo);
            spriteFondo2 = cargarSprite(ControlJuego.ANCHO_CAMARA/2,regionFondo2.getHeight()*1.5f, regionFondo2);
            attachChild(spriteFondo2);
        spritePersonaje = new AnimatedSprite(ControlJuego.ANCHO_CAMARA/2,18,
                regionPersonajeAnimado, actividadJuego.getVertexBufferObjectManager());
        spritePersonaje.animate(150);   // 200ms entre frames, 1000/200 fps
        attachChild(spritePersonaje);
        actividadJuego.getEngine().enableAccelerationSensor(actividadJuego, this);

        // Lista de enemigos que aparecen del lado derecho
        listaEnemigos = new ArrayList<>();
    }



        @Override
        protected void onManagedUpdate(float pSecondsElapsed) {
            super.onManagedUpdate(pSecondsElapsed);
            spriteFondo.setY(spriteFondo.getY() - 5);
            spriteFondo2.setY(spriteFondo2.getY() - 5);
            if (spriteFondo.getY()<-spriteFondo.getHeight()/2){
                spriteFondo.setY(1.5f*spriteFondo.getHeight()-5);
            }
            if (spriteFondo2.getY()<-spriteFondo2.getHeight()/2){
                spriteFondo2.setY(1.5f*spriteFondo2.getHeight()-5);
            }
            //manejo enemigos
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
                Sprite spriteEnemigo = cargarSprite((float) (Math.random() * ControlJuego.ANCHO_CAMARA - regionEnemigo.getWidth()) + regionEnemigo.getWidth(),ControlJuego.ALTO_CAMARA + regionEnemigo.getHeight(), regionEnemigo);
                Enemigo nuevoEnemigo = new Enemigo(spriteEnemigo);
                listaEnemigos.add(nuevoEnemigo);
                attachChild(nuevoEnemigo.getSpriteEnemigo());
                Log.i("Tamaño", "Datos: " + listaEnemigos.size());
            }
            // Actualizar cada uno de los listaEnemigos y ver si alguno ya salió de la pantalla
            for (int i = listaEnemigos.size() - 1; i >= 0; i--) {
                Enemigo enemigo = listaEnemigos.get(i);
                enemigo.mover(0, -10);
                if (enemigo.getSpriteEnemigo().getY() < -enemigo.getSpriteEnemigo().getHeight()) {
                    detachChild(enemigo.getSpriteEnemigo());
                    listaEnemigos.remove(enemigo);
                }
                if (spritePersonaje.collidesWith(enemigo.getSpriteEnemigo())) {
                    detachChild(enemigo.getSpriteEnemigo());
                    listaEnemigos.remove(enemigo);
                    energia = 10;
                    Log.i("ENERGIA", "Energia: " + energia);
                    if (energia <= 0) {
                        juegoCorriendo = false;
                        // Agrega pantalla de fin
                        Sprite spriteFin = new Sprite(ControlJuego.ANCHO_CAMARA / 2, ControlJuego.ALTO_CAMARA / 2,
                                regionFin, actividadJuego.getVertexBufferObjectManager()) {
                            @Override
                            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                                if (pSceneTouchEvent.isActionUp()) {
                                    onBackKeyPressed();
                                }
                                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
                            }
                        };
                        registerTouchArea(spriteFin);
                        attachChild(spriteFin);
                    };
                }
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
