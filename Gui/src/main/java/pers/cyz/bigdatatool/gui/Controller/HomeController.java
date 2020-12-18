package pers.cyz.bigdatatool.gui.Controller;

import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import pers.cyz.bigdatatool.gui.MyIcon;


public class HomeController {

    @FXML
    public JFXListView HomeList;

    public HomeController(){

    }

    @FXML
    public void initialize(){
        ObservableList<String> strList = HomeList.getItems();
        strList.add("hello");
        strList.add("world");

        HomeList.setItems(strList);
        HomeList.setFixedCellSize(32);
        HomeList.setCellFactory(new Callback<JFXListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(JFXListView<String> param) {
                ListCell<String> listcell = new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {

                        super.updateItem(item, empty);
                        if (empty == false) {
                            HBox hbox = new HBox(10);
                            Label l = new Label("集群");
                            l.setPrefWidth(32);
                            l.setPrefHeight(16);
                            FontIcon f = FontIcon.of(MyIcon.HADOOP,32);

                            hbox.getChildren().addAll(f,l);
                            setGraphic(hbox);
                        }
                    }
                };
                return listcell;
            }
        });

        System.out.println("hello");

    }
}
