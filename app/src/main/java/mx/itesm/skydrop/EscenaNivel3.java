
package mx.itesm.skydrop;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.andengine.audio.sound.Sound;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
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

public class EscenaNivel3 extends EscenaBase implements IAccelerationListener {

    private int puntos = 0;
    private int vidas= 3;

    private boolean juegoCorriendo = true;

    private boolean juegoGanado = true;
    private boolean juegoPerdido = true;

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
    private Sprite replay;

    // Escena FIN
    private CameraScene escenaFin;
    private  ITextureRegion regionBtnContinuar;
    private ITextureRegion regionBtnSalir;

    private ITextureRegion regionNube;
    private ArrayList<Nube> listaNube;
    private ITextureRegion regionBomba;
    private ArrayList<Bomba> listaBomba;
    private ITextureRegion regionGusano;
    private ArrayList<Gusano> listaGusano;

    // Tiempo para generar listaSobres
    private float tiempoEnemigos = 0;
    private float LIMITE_TIEMPO = 2.3f;
    private float tiempoNube = 0;
    private float tiempoBomba = 0f;
    private float tiempoGusano = 0f;
    private float LIMITE_TIEMPON = 1.6f;
    private CameraScene escenaPausa;    // La escena que se muestra al hacer pausa
    private ITextureRegion regionPausa;
    private ITextureRegion regionBtnPausa;
    private ITextureRegion regionBtnReplay;
    private Sound sonidoFondo;


    @Override
    public void cargarRecursos() {
        regionCorazon = cargarImagen("items/Corazon.png");
        regionFondo = cargarImagen("fondos/nieves.jpg");
        regionFondo2 = cargarImagen("fondos/nieves.jpg");
        regionPersonajeAnimado = cargarImagenMosaico("personajes/aguila.png", 960, 131, 1, 6);
        regionEnemigo=cargarImagen("items/carta.png");
        fontSan = cargarFont("san.ttf");
        regionNube=cargarImagen("enemigo/avions.png");
        regionBomba=cargarImagen("enemigo/bomb.png");
        regionGusano=cargarImagen("items/lombri.png");
        regionFin = cargarImagen("fondos/gameoverr.png");
        regionWin = cargarImagen("fondos/winn.png");
        regionBtnPausa = cargarImagen("botones/btnpausa.png");
        regionPausa = cargarImagen("fondos/pause.png");
        regionBtnContinuar = cargarImagen("botones/play.png");
        regionBtnSalir = cargarImagen("botones/back.png");
        regionBtnReplay=cargarImagen("botones/replay.png");


    }

    private IFont cargarFont(String archivo) {
        // La imagen que contiene cada sÃ­mbolo
        final ITexture fontTexture = new BitmapTextureAtlas(actividadJuego.getEngine().getTextureManager(),512,256);
        // Carga el archivo, tamaÃ±o 56, antialias y color
        Font tipoLetra = FontFactory.createFromAsset(actividadJuego.getEngine().getFontManager(),
                fontTexture, actividadJuego.getAssets(), archivo, 40, true, 0xFFFFFFFF);
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
        spritePersonaje = new AnimatedSprite(ControlJuego.ANCHO_CAMARA / 2, 80,
                regionPersonajeAnimado, actividadJuego.getVertexBufferObjectManager());
        spritePersonaje.setScale(1.5f);
        spritePersonaje.animate(150);   // 200ms entre frames, 1000/200 fps
        attachChild(spritePersonaje);
        actividadJuego.getEngine().enableAccelerationSensor(actividadJuego, this);

        // Lista de enemigos que aparecen del lado derecho
        listaSobres = new ArrayList<>();
        listaNube = new ArrayList<>();
        listaBomba = new ArrayList<>();
        listaGusano=new ArrayList<>();


        // Agregar flechas y el txtMarcador/valorMarcador
        agregarHUD();

        // Crea el botÃ³n de PAUSA y lo agrega a la escena
        Sprite btnPausa = new Sprite(720, 1230,
                regionBtnPausa, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    //  if (juegoCorriendo) {
                    pausarJuego();
                    //  }
                    if(juegoGanado==false){

                    }
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        btnPausa.setScale(0.80f);
        attachChild(btnPausa);
        registerTouchArea(btnPausa);

        // Crear la escena de PAUSA, pero NO lo agrega a la escena
        escenaPausa = new CameraScene(actividadJuego.camara);
        Sprite fondoPausa = cargarSprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2,
                regionPausa);
        escenaPausa.attachChild(fondoPausa);
        escenaPausa.setBackgroundEnabled(false);
        Sprite spriteReanudar = new Sprite(ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA/2,
                regionBtnReplay,actividadJuego.getVertexBufferObjectManager())  {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    liberarEscena();
                    admEscenas.crearEscenaNivel3();
                    admEscenas.setEscena(TipoEscena.ESCENA_NIVEL3);
                    return true;
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        //btnContinuar.setAlpha(0.4f);
        escenaPausa.attachChild(spriteReanudar);
        registerTouchArea(spriteReanudar);
        Sprite btnSalir = new Sprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/4,
                regionBtnSalir, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    onBackKeyPressed();
                    return true;
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        //btnContinuar.setAlpha(0.4f);
        escenaPausa.attachChild(btnSalir);
        registerTouchArea(btnSalir);
        agregarFinJuego();
        actividadJuego.reproducirMusica("music.mp3", true);

    }

    private void agregarFinJuego() {
        // Crear la escena de FIN, pero NO lo agrega a la escena
        escenaFin = new CameraScene(actividadJuego.camara);
        //Sprite fondoPausa = cargarSprite(ControlJuego.ANCHO_CAMARA / 2, ControlJuego.ALTO_CAMARA / 2,
        //regionPausa);
        Rectangle fondoFin = new Rectangle(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2,ControlJuego.ANCHO_CAMARA, ControlJuego.ALTO_CAMARA, actividadJuego.getVertexBufferObjectManager());
        fondoFin.setColor(0xAA000000);
        escenaFin.attachChild(fondoFin);
        Sprite spriteWin = new Sprite(ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA/2,
                regionWin,actividadJuego.getVertexBufferObjectManager());
        escenaFin.attachChild(spriteWin);

        // Crea el botÃ³n de CONTINUE y lo agrega a la escena
        Sprite btnContinuar = new Sprite(240, 300,
                regionBtnContinuar, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    liberarEscena();
                    admEscenas.crearEscenaNivel3();
                    admEscenas.setEscena(TipoEscena.ESCENA_NIVEL3);
                    return true;
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        //btnContinuar.setAlpha(0.4f);
        escenaFin.attachChild(btnContinuar);
        escenaFin.registerTouchArea(btnContinuar);

        // Crea el botÃ³n de SALIR y lo agrega a la escena
        Sprite btnSalir = new Sprite(500, 300,
                regionBtnSalir, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    onBackKeyPressed();

                }
                return true; //super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        //btnContinuar.setAlpha(0.4f);
        escenaFin.attachChild(btnSalir);
        escenaFin.registerTouchArea(btnSalir);

        escenaFin.setBackgroundEnabled(false);
    }

    private void reiniciarJuego() {
        liberarEscena();
        admEscenas.crearEscenaNivel3();
        admEscenas.setEscena(TipoEscena.ESCENA_NIVEL3);
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

    private void terminarJuego() {
        // Mostrar la pantalla de fin
        setChildScene(escenaFin, false, true, false);
        juegoCorriendo = false;
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
            // Se cumpliÃ³ el tiempo
            tiempoEnemigos = 0;

            Sprite spriteEnemigo = cargarSprite((float) (Math.random() * ControlJuego.ANCHO_CAMARA - regionEnemigo.getWidth()) + regionEnemigo.getWidth(), ControlJuego.ALTO_CAMARA + regionEnemigo.getHeight(), regionEnemigo);
            Enemigo nuevoEnemigo = new Enemigo(spriteEnemigo);
            spriteEnemigo.setScale(0.75f);
            listaSobres.add(nuevoEnemigo);
            attachChild(nuevoEnemigo.getSpriteEnemigo());
            Log.i("TamaÃ±o", "Datos: " + listaSobres.size());

        }


        // Actualizar cada uno de los listaSobres y ver si alguno ya saliÃ³ de la pantalla
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
                puntos = puntos+250;
                Log.i("ENERGIA", "Score: " + puntos);
                valorMarcador=puntos;
                if (puntos==5000) {
                    juegoCorriendo=false;
                    guardarMarcadorAlto();
                    // Agrega pantalla de fin
                    Sprite spriteWin = new Sprite(ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA/2,
                            regionWin,actividadJuego.getVertexBufferObjectManager());
                    attachChild(spriteWin);
                    Sprite btnContinuar = new Sprite(240, 270,
                            regionBtnContinuar, actividadJuego.getVertexBufferObjectManager()) {
                        @Override
                        public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                            if (pSceneTouchEvent.isActionDown()) {
                                liberarEscena();
                                admEscenas.crearEscenaNivel3();
                                admEscenas.setEscena(TipoEscena.ESCENA_NIVEL3);
                                return true;
                            }
                            return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
                        }
                    };
                    //btnContinuar.setAlpha(0.4f);
                    spriteWin.attachChild(btnContinuar);
                    registerTouchArea(btnContinuar);

                    // Crea el botÃ³n de SALIR y lo agrega a la escena
                    Sprite btnSalir = new Sprite(500, 270,
                            regionBtnSalir, actividadJuego.getVertexBufferObjectManager()) {
                        @Override
                        public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                            if (pSceneTouchEvent.isActionUp()) {
                                onBackKeyPressed();

                            }
                            return true; //super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
                        }
                    };
                    //btnContinuar.setAlpha(0.4f);
                    spriteWin.attachChild(btnSalir);
                    registerTouchArea(btnSalir);




                }


            }
        }


        // Acumular tiempo
        tiempoNube += pSecondsElapsed;
        if (tiempoNube > LIMITE_TIEMPON) {
            // Se cumpliÃ³ el tiempo
            tiempoNube = 0;

            Sprite spriteNube = cargarSprite((float) (Math.random() * ControlJuego.ANCHO_CAMARA - regionNube.getWidth()) + regionNube.getWidth(), ControlJuego.ALTO_CAMARA + regionNube.getHeight(), regionNube);
            Nube nuevoNube = new Nube(spriteNube);
            spriteNube.setScale(0.5f);
            listaNube.add(nuevoNube);
            attachChild(nuevoNube.getSpriteNube());
            Log.i("TamaÃ±o", "Datos: " + listaNube.size());


        }


        // Actualizar cada uno de los listaSobres y ver si alguno ya saliÃ³ de la pantalla
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
                    Sprite spriteReanudar = new Sprite(240,300,
                            regionBtnReplay,actividadJuego.getVertexBufferObjectManager())  {
                        @Override
                        public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                            if (pSceneTouchEvent.isActionDown()) {
                                liberarEscena();
                                admEscenas.crearEscenaNivel3();
                                admEscenas.setEscena(TipoEscena.ESCENA_NIVEL3);
                                return true;
                            }
                            return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
                        }
                    };
                    //btnContinuar.setAlpha(0.4f);
                    spriteFin.attachChild(spriteReanudar);
                    registerTouchArea(spriteReanudar);
                    Sprite btnSalir = new Sprite(500, 300,
                            regionBtnSalir, actividadJuego.getVertexBufferObjectManager()) {
                        @Override
                        public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                            if (pSceneTouchEvent.isActionUp()) {
                                onBackKeyPressed();

                            }
                            return true; //super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
                        }
                    };
                    //btnContinuar.setAlpha(0.4f);
                    spriteFin.attachChild(btnSalir);
                    registerTouchArea(btnSalir);
                }
            }
        }
        tiempoGusano += pSecondsElapsed;
        if (tiempoGusano > LIMITE_TIEMPON) {
            // Se cumpliÃ³ el tiempo
            tiempoGusano =-3.8f;

            Sprite spriteGusano = cargarSprite((float) (Math.random() * ControlJuego.ANCHO_CAMARA - regionGusano.getWidth()) + regionGusano.getWidth(), ControlJuego.ALTO_CAMARA + regionGusano.getHeight(), regionGusano);
            Gusano nuevoGusano = new Gusano(spriteGusano);
            spriteGusano.setScale(0.5f);
            listaGusano.add(nuevoGusano);
            attachChild(nuevoGusano.getSpriteGusano());
            Log.i("TamaÃ±o", "Datos: " + listaGusano.size());


        }


        // Actualizar cada uno de los listaSobres y ver si alguno ya saliÃ³ de la pantalla
        for (int i = listaGusano.size() - 1; i >= 0; i--) {
            Gusano gusano = listaGusano.get(i);
            gusano.mover(0, -10);
            if (gusano.getSpriteGusano().getY() < -gusano.getSpriteGusano().getHeight()) {
                detachChild(gusano.getSpriteGusano());
                listaGusano.remove(gusano);
            }
            if (spritePersonaje.collidesWith(gusano.getSpriteGusano())) {
                detachChild(gusano.getSpriteGusano());
                vidas=vidas+1;
                listaGusano.remove(gusano);
                valorVidas=vidas;

            }
        }
        tiempoBomba += pSecondsElapsed;
        if (tiempoBomba > LIMITE_TIEMPON) {
            // Se cumpliÃ³ el tiempo
            tiempoBomba =-2.3f;

            Sprite spriteBomba = cargarSprite((float) (Math.random() * ControlJuego.ANCHO_CAMARA - regionBomba.getWidth()) + regionBomba.getWidth(), ControlJuego.ALTO_CAMARA + regionBomba.getHeight(), regionBomba);
            Bomba nuevoBomba = new Bomba(spriteBomba);
            spriteBomba.setScale(0.5f);
            listaBomba.add(nuevoBomba);
            attachChild(nuevoBomba.getSpriteBomba());
            Log.i("TamaÃ±o", "Datos: " + listaBomba.size());


        }


        // Actualizar cada uno de los listaSobres y ver si alguno ya saliÃ³ de la pantalla
        for (int i = listaBomba.size() - 1; i >= 0; i--) {
            Bomba bomba = listaBomba.get(i);
            bomba.mover(0, -10);
            if (bomba.getSpriteBomba().getY() < -bomba.getSpriteBomba().getHeight()) {
                detachChild(bomba.getSpriteBomba());
                listaBomba.remove(bomba);
            }
            if (spritePersonaje.collidesWith(bomba.getSpriteBomba())) {
                detachChild(bomba.getSpriteBomba());
                vidas=vidas-2;
                listaBomba.remove(bomba);
                valorVidas=vidas;
                if (vidas<=0) {
                    valorVidas=0;
                    juegoCorriendo=false;
                    // Agrega pantalla de fin
                    Sprite spriteFin = new Sprite(ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA/2,
                            regionFin,actividadJuego.getVertexBufferObjectManager()) ;
                    attachChild(spriteFin);
                    Sprite spriteReanudar = new Sprite(240,300,
                            regionBtnReplay,actividadJuego.getVertexBufferObjectManager())  {
                        @Override
                        public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                            if (pSceneTouchEvent.isActionDown()) {
                                liberarEscena();
                                admEscenas.crearEscenaNivel3();
                                admEscenas.setEscena(TipoEscena.ESCENA_NIVEL3);
                                return true;
                            }
                            return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
                        }
                    };
                    //btnContinuar.setAlpha(0.4f);
                    spriteFin.attachChild(spriteReanudar);
                    registerTouchArea(spriteReanudar);
                    Sprite btnSalir = new Sprite(500, 300,
                            regionBtnSalir, actividadJuego.getVertexBufferObjectManager()) {
                        @Override
                        public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                            if (pSceneTouchEvent.isActionUp()) {
                                onBackKeyPressed();

                            }
                            return true; //super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
                        }
                    };
                    //btnContinuar.setAlpha(0.4f);
                    spriteFin.attachChild(btnSalir);
                    registerTouchArea(btnSalir);
                }
            }
        }
        txtMarcador.setText("Score : " + (valorMarcador));
        txtVidas.setText(" "+valorVidas+" ");
    }







    // Recude el tamaÃ±o hasta desaparecer
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
        // Regresar al menÃº principal
        admEscenas.crearEscenaMenu();
        admEscenas.setEscena(TipoEscena.ESCENA_MENU);
        admEscenas.liberarEscenaNivel3();
        guardarMarcadorAlto();

    }

    private void guardarMarcadorAlto() {
        SharedPreferences preferencias = actividadJuego.getSharedPreferences("marcadorAlto", Context.MODE_PRIVATE);
        int anterior = preferencias.getInt("puntos",0);
        // if (puntos==5000) {
        // Nuevo valor mayor, guardarlo
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putInt("puntos",puntos+anterior);
        editor.commit();
    }


    @Override
    public TipoEscena getTipoEscena() {
        return TipoEscena.ESCENA_NIVEL3;
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
        float dx = pAccelerationData.getX()*1.5f;
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
        // Detiene el acelerÃ³metro
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