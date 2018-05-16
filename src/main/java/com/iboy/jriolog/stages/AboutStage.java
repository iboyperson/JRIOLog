package com.iboy.jriolog.stages;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AboutStage extends Stage {
    FileInputStream logoStream;
    public AboutStage() {
        getIcons().add(new Image("/JRIOLog_Logo.png"));
        setTitle("About");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));

        Scene scene = new Scene(grid, 350, 300);
        setScene(scene);

        Image logo = new Image("/JRIOLog_Logo.png", 150, 150, true, true);
        ImageView logoView = new ImageView(logo);
        grid.add(logoView, 0, 0);
        GridPane.setHalignment(logoView, HPos.CENTER);

        Text text = new Text("JRIOLog");
        text.setFont(Font.font(30));
        grid.add(text, 0, 1);
        GridPane.setHalignment(text, HPos.CENTER);
    }
}
