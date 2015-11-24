package mx.itesm.skydrop;

import org.andengine.engine.Engine;

/**
 * Administra la escena que se verÃ¡ en la pantalla
 */
public class AdministradorEscenas
{
    // Instancia Ãºnica
    private static final AdministradorEscenas INSTANCE =
            new AdministradorEscenas();
    protected ControlJuego actividadJuego;

    // Declara las distintas escenas que forman el juego
    private EscenaBase escenaSplash;
    private EscenaBase escenaMenu;
    private EscenaBase escenaAcercaDe;
    private EscenaBase escenaOpciones;
    private EscenaBase escenaRules;
    private EscenaBase escenaNivel1;
    private EscenaBase escenaNivel2;
    private EscenaBase escenaNivel3;


    // El tipo de escena que se estÃ¡ mostrando
    private TipoEscena tipoEscenaActual = TipoEscena.ESCENA_SPLASH;
    // La escena que se estÃ¡ mostrando
    private EscenaBase escenaActual;
    // El engine para hacer el cambio de escenas
    private Engine engine;

    // Asigna valores iniciales del administrador
    public static void inicializarAdministrador(ControlJuego actividadJuego, Engine engine) {
        getInstance().actividadJuego = actividadJuego;
        getInstance().engine = engine;
    }

    // Regresa la instancia del administrador de escenas
    public static AdministradorEscenas getInstance() {
        return INSTANCE;
    }

    // Regresa el tipo de la escena actual
    public TipoEscena getTipoEscenaActual() {
        return tipoEscenaActual;
    }

    // Regresa la escena actual
    public EscenaBase getEscenaActual() {
        return escenaActual;
    }

    /*
     * Pone en la pantalla la escena que llega como parÃ¡metro y guarda el nuevo estado
     */
    private void setEscenaBase(EscenaBase nueva) {
        engine.setScene(nueva);
        escenaActual = nueva;
        tipoEscenaActual = nueva.getTipoEscena();
    }

    /**
     * Cambia a la escena especificada en el parÃ¡metro
     * @param nuevoTipo la nueva escena que se quiere mostrar
     */
    public void setEscena(TipoEscena nuevoTipo) {
        switch (nuevoTipo) {
            case ESCENA_SPLASH:
                setEscenaBase(escenaSplash);
                break;
            case ESCENA_MENU:
                setEscenaBase(escenaMenu);
                break;
            case ESCENA_ACERCA_DE:
                setEscenaBase(escenaAcercaDe);
                break;
            case ESCENA_OPCIONES:
                setEscenaBase(escenaOpciones);
                break;
            case ESCENA_RULES:
                setEscenaBase(escenaRules);
                break;
            case ESCENA_NIVEL1:
                setEscenaBase(escenaNivel1);
                break;
            case ESCENA_NIVEL2:
                setEscenaBase(escenaNivel2);
                break;
            case ESCENA_NIVEL3:
                setEscenaBase(escenaNivel3);
                break;

        }
    }

    //*** Crea la escena de Splash
    public void crearEscenaSplash() {
        // Carga los recursos
        escenaSplash = new EscenaSplash();
    }

    //*** Libera la escena de Splash
    public void liberarEscenaSplash() {
        escenaSplash.liberarEscena();
        escenaSplash = null;
    }

    // ** MENU
    //*** Crea la escena de MENU
    public void crearEscenaMenu() {
        // Carga los recursos
        escenaMenu = new EscenaMenu();
    }

    //*** Libera la escena de MENU
    public void liberarEscenaMenu() {
        escenaMenu.liberarEscena();
        escenaMenu = null;
    }

    //*** Crea la escena de Acerca De
    public void crearEscenaAcercaDe() {
        // Carga los recursos
        escenaAcercaDe = new EscenaAcercaDe();
    }

    //*** Libera la escena de AcercDe
    public void liberarEscenaAcercaDe() {
        escenaAcercaDe.liberarEscena();
        escenaAcercaDe = null;
    }
    public void crearEscenaOpciones() {
        // Carga los recursos
        escenaOpciones = new EscenaOpciones();
    }
    public void liberarEscenaOpciones() {
        escenaOpciones.liberarEscena();
        escenaOpciones = null;
    }
    public void crearEscenaRules() {
        // Carga los recursos
        escenaRules = new EscenaRules();
    }
    public void liberarEscenaRules() {
        escenaRules.liberarEscena();
        escenaRules = null;
    }

    public void crearEscenaNivel1() {
        escenaNivel1 = new EscenaNivel1();
    }
    public void liberarEscenaNivel1(){
        escenaNivel1.liberarEscena();
        escenaNivel1= null;
    }
    public void crearEscenaNivel2() {
        escenaNivel2 = new EscenaNivel2();
    }

    public void liberarEscenaNivel2() {
        escenaNivel2.liberarEscena();
        escenaNivel2 = null;

    }
    public void crearEscenaNivel3() {
        escenaNivel3 = new EscenaNivel3();
    }

    public void liberarEscenaNivel3() {
        escenaNivel3.liberarEscena();
        escenaNivel3 = null;

    }




/*
    //*** Crea la escena de JUEGO
    public void crearEscenaJuego() {
        // Carga los recursos
        admRecursos.cargarRecursosJuego();
        //escenaJuego = new EscenaJuego();
    }

    //*** Libera la escena de JUEGO
    public void liberarEscenaJuego() {
        admRecursos.liberarRecursosJuego();
        escenaJuego.liberarEscena();
        escenaJuego = null;
    }

    //*** Crea la escena de Juego Dos
    public void crearEscenaJuegoDos() {
        // Carga los recursos
        admRecursos.cargarRecursosJuegoDos();
        //escenaJuegoDos = new EscenaJuegoDos();
    }

    //*** Libera la escena de Juego Dos
    public void liberarEscenaJuegoDos() {
        admRecursos.liberarRecursosJuegoDos();
        escenaJuegoDos.liberarEscena();
        escenaJuegoDos = null;
    }
*/
}
