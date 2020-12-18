package pers.cyz.bigdatatool.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        stage.getIcons().add(new Image("static/集群.png"));
        FXMLLoader fx = new FXMLLoader();
        VBox root = (VBox)fx.load(new FileInputStream(new File("Gui/src/main/java/pers/cyz/bigdatatool/gui/Fxml/Home.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
