package mx.itesm.skydrop;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

/**
 * Representa la escena del MENU PRINCIPAL
 *
 * @author Roberto Martínez Román
 */
public class EscenaMenu extends EscenaBase
{
    // Regiones para las imágenes de la escena
    private ITextureRegion regionFondo;
    private ITextureRegion regionBtnAcercaDe;
    private ITextureRegion regionBtnJugar;
    private ITextureRegion regionBtnOpciones;
    private ITextureRegion regionBtnRules;

    // Sprites sobre la escena
    private Sprite spriteFondo;

    // Un menú de tipo SceneMenu
    private MenuScene menu;     // Contenedor de las opciones
    // Constantes para cada opción
    private final int OPCION_ACERCA_DE = 0;
    private final int OPCION_JUGAR = 1;
    private final int OPCION_OPCIONES = 2;
    private final int OPCION_RULES = 3;

    // Botones de cada opción
    private ButtonSprite btnAcercaDe;
    private ButtonSprite btnJugar;
    private ButtonSprite btnopciones;
    private ButtonSprite btnrules;

    @Override
    public void cargarRecursos() {
        // Fondo
        regionFondo = cargarImagen("MenuInicianSinBot.jpg");
        // Botones del menú
        regionBtnAcercaDe = cargarImagen("botnextra.png");
        regionBtnJugar = cargarImagen("sobreplay.png");
        regionBtnOpciones=cargarImagen("botonOp.jpg");
        regionBtnRules=cargarImagen("botnrules.png");
    }

    @Override
    public void crearEscena() {
        // Creamos el sprite de manera óptima
        spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2, regionFondo);

        // Crea el fondo de la pantalla
        SpriteBackground fondo = new SpriteBackground(1,1,1,spriteFondo);
        setBackground(fondo);
        setBackgroundEnabled(true);

        // Mostrar un recuadro atrás del menú
        //agregarFondoMenu();
        // Mostrar opciones de menú
        agregarMenu();
    }

    private void agregarFondoMenu() {
        Rectangle cuadro = new Rectangle(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2,
                0.75f*ControlJuego.ANCHO_CAMARA, 0.75f*ControlJuego.ALTO_CAMARA, actividadJuego.getVertexBufferObjectManager());
        cuadro.setColor(0.8f, 0.8f, 0.8f, 0.4f);
        attachChild(cuadro);
    }

    private void agregarMenu() {
        // Crea el objeto que representa el menú
        menu = new MenuScene(actividadJuego.camara);
        // Centrado en la pantalla
        menu.setPosition(ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA/2);
        // Crea las opciones (por ahora, acerca de y jugar)
        IMenuItem opcionAcercaDe = new ScaleMenuItemDecorator(new SpriteMenuItem(OPCION_ACERCA_DE,
                regionBtnAcercaDe, actividadJuego.getVertexBufferObjectManager()), 1.5f, 1);
        IMenuItem opcionJugar = new ScaleMenuItemDecorator(new SpriteMenuItem(OPCION_JUGAR,
                regionBtnJugar, actividadJuego.getVertexBufferObjectManager()), 1.5f, 1);
        IMenuItem opcionopciones = new ScaleMenuItemDecorator(new SpriteMenuItem(OPCION_OPCIONES,
                regionBtnOpciones, actividadJuego.getVertexBufferObjectManager()), 1.5f, 1);
        IMenuItem opcionrules = new ScaleMenuItemDecorator(new SpriteMenuItem(OPCION_RULES,
                regionBtnRules, actividadJuego.getVertexBufferObjectManager()), 1.5f, 1);

        // Agrega las opciones al menú
        menu.addMenuItem(opcionAcercaDe);
        menu.addMenuItem(opcionJugar);
        menu.addMenuItem(opcionopciones);
        menu.addMenuItem(opcionrules);

        // Termina la configuración
        menu.buildAnimations();
        menu.setBackgroundEnabled(false);   // Completamente transparente

        // Ubicar las opciones DENTRO del menú. El centro del menú es (0,0)
        opcionAcercaDe.setPosition(180,-360);
        opcionJugar.setPosition(180, 50);
        opcionopciones.setPosition(180,-230);
        opcionrules.setPosition(180, -80);


        // Registra el Listener para atender las opciones
        menu.setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
                                             float pMenuItemLocalX, float pMenuItemLocalY) {
                // El parámetro pMenuItem indica la opción oprimida
                switch(pMenuItem.getID()) {
                    case OPCION_ACERCA_DE:
                        // Mostrar la escena de AcercaDe
                        admEscenas.crearEscenaAcercaDe();
                        admEscenas.setEscena(TipoEscena.ESCENA_ACERCA_DE);
                        admEscenas.liberarEscenaMenu();
                        break;

                    case OPCION_JUGAR:
                        // Mostrar la pantalla de juego
                        admEscenas.crearEscenaNivel1();
                        admEscenas.setEscena(TipoEscena.ESCENA_NIVEL1);
                        admEscenas.liberarEscenaMenu();

                        break;
                    case OPCION_OPCIONES:

                        admEscenas.crearEscenaOpciones();
                        admEscenas.setEscena(TipoEscena.ESCENA_OPCIONES);
                        admEscenas.liberarEscenaMenu();
                        break;

                    case OPCION_RULES:

                        admEscenas.crearEscenaRules();
                        admEscenas.setEscena(TipoEscena.ESCENA_RULES);
                        admEscenas.liberarEscenaMenu();
                        break;


                }
                return true;
            }
        });

        // Asigna este menú a la escena
        setChildScene(menu);
    }

    // La escena se debe actualizar en este método que se repite "varias" veces por segundo
    // Aquí es donde programan TODA la acción de la escena (movimientos, choques, disparos, etc.)
    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);

    }


    @Override
    public void onBackKeyPressed() {
        // Salir del juego, no hacemos nada
    }

    @Override
    public TipoEscena getTipoEscena() {
        return TipoEscena.ESCENA_MENU;
    }

    @Override
    public void liberarEscena() {
        this.detachSelf();      // La escena se deconecta del engine
        this.dispose();         // Libera la memoria
        liberarRecursos();
    }

    @Override
    public void liberarRecursos() {
        regionFondo.getTexture().unload();
        regionFondo = null;
    }
}
