package com.iboy.jriolog.stages;

import com.iboy.jriolog.ConfigHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SettingsStage extends Stage {
    public SettingsStage(ConfigHandler configHandler) {
        getIcons().add(new Image("/JRIOLog_Logo.png"));
        setTitle("Settings");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));

        Scene scene = new Scene(grid, 500, 400);
        setScene(scene);

        Label teamLabel = new Label("Team: ");
        grid.add(teamLabel, 0, 0);

        TextField teamField = new TextField();
        teamField.setText(configHandler.getTeam());
        grid.add(teamField, 1, 0);

        Label portLabel = new Label("Port: ");
        grid.add(portLabel, 0, 2);

        TextField portField = new TextField();
        portField.setText(Integer.toString(configHandler.getPort()));
        grid.add(portField, 1, 2);

        Label loginLabel = new Label("Login: ");
        grid.add(loginLabel, 0, 3);

        TextField loginField = new TextField();
        loginField.setText(configHandler.getLogin());
        grid.add(loginField, 1, 3);

        Label passwordLabel = new Label("Password: ");
        grid.add(passwordLabel, 0, 4);

        TextField passwordField = new TextField();
        passwordField.setText(configHandler.getPassword());
        grid.add(passwordField, 1, 4);

        Button save = new Button("Save");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!teamField.getText().equals(configHandler.getTeam())) {
                    configHandler.setTeam(teamField.getText());
                }

                if (!portField.getText().equals(configHandler.getPort())) {
                    configHandler.setPort(Integer.parseInt(portField.getText()));
                }

                if (!loginField.getText().equals(configHandler.getLogin())) {
                    configHandler.setLogin(loginField.getText());
                }

                if (!passwordField.getText().equals(configHandler.getPassword())) {
                    configHandler.setPassword(passwordField.getText());
                }

                configHandler.loadConfig();

                //Reload Text Fields
                if (!teamField.getText().equals(configHandler.getTeam())) {
                    teamField.setText(configHandler.getTeam());
                }

                if (!portField.getText().equals(configHandler.getPort())) {
                    portField.setText(Integer.toString(configHandler.getPort()));
                }

                if (!loginField.getText().equals(configHandler.getLogin())) {
                    loginField.setText(configHandler.getLogin());
                }

                if (!passwordField.getText().equals(configHandler.getPassword())) {
                    passwordField.setText(configHandler.getPassword());
                }
            }
        });
        grid.add(save, 0, 5);
    }
}
