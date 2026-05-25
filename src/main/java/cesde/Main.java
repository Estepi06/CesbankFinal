package cesde;

import cesde.config.Config;
import cesde.userinterface.MenuApp;


public class Main {

    public static void main(String[] args) {
        // Inicializar el flujo completo de la aplicación y la inyección de dependencias
        MenuApp menuApp = Config.createMenuApp();
        
        // Arrancar el menú interactivo del cajero
        menuApp.mainMenu();
    }
}
