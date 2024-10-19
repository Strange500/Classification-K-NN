package fr.univlille.s3.S302.view;

import fr.univlille.s3.S302.model.Data;
import fr.univlille.s3.S302.model.DataManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddController {
    @FXML
    private VBox AddVbox;
    Map<String, TextField> map=new HashMap<>();

    DataManager<Data> mn  = DataManager.instance;

    @FXML
    public void initialize() throws IOException {
        genererEcran();
    }
    public void genererEcran() throws IOException {
        Map<String,Number> map=mn.getDataList().get(0).getattributes();
        for (String s : map.keySet()){
            VBox tmp=GenererLigneAttributs(s);
            AddVbox.getChildren().add(tmp);
        }
    }

    public VBox GenererLigneAttributs(String label){
        VBox vbox=new VBox();
        Label labels = new Label(label);
        TextField tf = new TextField();
        vbox.getChildren().addAll(labels,tf);
        map.put(label,tf);
        return vbox;
    }
    public void AjouterPoint(){
        Map<String,Number> tmp=new HashMap<>();
        try{
            for (String s : map.keySet()){
                tmp.put(s,Double.parseDouble(map.get(s).getText()));
            }
            mn.addData(tmp);
        }catch (NumberFormatException e){
            DataController.genErrorPopup("Entrez valeurs valides").show(AddVbox.getScene().getWindow());

        }

    }
}
