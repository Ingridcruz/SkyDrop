package mx.itesm.skydrop;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * Created by Ingrid on 01/10/2015.
 */
public class EscenaNivel1 extends EscenaBase implements IAccelerationListener {


        private ITextureRegion regionFondo;
        private ITextureRegion regionFondo2;
    private AnimatedSprite spritePersonaje;
    private TiledTextureRegion regionPersonajeAnimado;
        // Sprite para el fondo
        private Sprite spriteFondo;
        private Sprite spriteFondo2;

        @Override
        public void cargarRecursos() {
            regionFondo = cargarImagen("escenario.jpg");
            regionFondo2 = cargarImagen("escenario2.jpg");
            regionPersonajeAnimado = cargarImagenMosaico("pajaro.jpg", 128, 29, 1, 3);
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

        float dz = pAccelerationData.getZ();
        //desplazamiento en z
        spritePersonaje.setX(spritePersonaje.getX()+dx);
        spritePersonaje.setY(spritePersonaje.getY()+ dy);



    }


    public void liberarRecurso () {
        // Detiene el acelerómetro
        actividadJuego.getEngine().disableAccelerationSensor(actividadJuego);

        regionFondo.getTexture().unload();
        regionFondo = null;
        regionPersonajeAnimado.getTexture().unload();
        regionPersonajeAnimado = null;

    }


    }
