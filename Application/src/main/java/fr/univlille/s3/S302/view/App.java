package fr.univlille.s3.S302.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import atlantafx.base.theme.Dracula;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    /**
     * Charger le fichier FXML et met le thème Dracula
     * 
     * @param fxml le nom du fichier FXML à charger
     * 
     * @return le parent du fichier FXML chargé
     * 
     * @throws IOException si le fichier FXML ne peut pas être chargé
     */
    static Parent loadFXML(String fxml) throws IOException {
        Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Met le fichier FXML en tant que racine
     * 
     * @param fxml le nom du fichier FXML à mettre en tant que racine
     * 
     * @throws IOException si le fichier FXML ne peut pas être chargé
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Lance l'application
     * 
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Démarre l'application
     * 
     * @param stage la scène à démarrer
     * 
     * @throws IOException si le fichier FXML ne peut pas être chargé
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("UI"), 1280, 960);
        stage.setTitle("Classification K-NN");
        stage.setScene(scene);
        stage.show();
    }
}
