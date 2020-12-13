package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Callback;


public class Controller {


//    static class MyListCell extends ListCell<String> {
//
//        @Override
//        public void startEdit(){
//            System.out.println("2");
//
//        }
//
//        @Override
//        protected void updateItem(String item, boolean empty) {
//            super.updateItem(item, empty);
//            System.out.println("2");
//            if (item != null && !empty) { // <== test for null item and empty parameter
//                setGraphic(new Label("HELLO"));
//            } else {
//                setGraphic(null);
//            }
//        }
//
//    }

    @FXML
    public ListView HomeList;

    public Controller(){

    }

    @FXML
    public void initialize(){
        System.out.println("3");
//        HomeList = new ListView<String>();
//        HomeList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
//            @Override
//            public ListCell call(ListView param) {
//                ListCell<String> listcell = new ListCell<>() {
//                    @Override
//                    protected void updateItem(String item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (empty == false) {
//                            this.setGraphic(new Label("hello"));
//                        }
//                    }
//                };
//                System.out.println("2");
//                return listcell;
//            }
//        });

        System.out.println("hello");

    }
}
