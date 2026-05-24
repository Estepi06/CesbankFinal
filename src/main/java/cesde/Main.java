package cesde;

import cesde.config.Config;
import cesde.userinterface.MenuApp;

/**
 * Clase principal de inicio de la aplicación CesBank.
 */
public class Main {

    public static void main(String[] args) {
        // Inicializar el flujo completo de la aplicación y la inyección de dependencias
        MenuApp menuApp = Config.createMenuApp();
        
        // Arrancar el menú interactivo del cajero
        menuApp.mainMenu();
    }
}
