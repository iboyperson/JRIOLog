package com.iboy.jriolog;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.logging.Logger;

public class JRIOLog extends Application {
	public static Logger log = Logger.getLogger(JRIOLog.class.getName());

	ConfigHandler configHandler;

	Ellipse status;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		configHandler = new ConfigHandler();

		//Make a new RIOConnection
		RIOConnection rio = new RIOConnection(configHandler.getIp(), configHandler.getPort(), configHandler.getLogin());

		//Set Basic Stage Info
		primaryStage.setTitle("JRIOLog");
		primaryStage.setMinWidth(640);
		primaryStage.setMinHeight(480);
		primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
		primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());

		//Create GridBagLayout
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.TOP_LEFT);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));

		//Create Scene and set it
		Scene scene = new Scene(grid, primaryStage.getWidth(), primaryStage.getHeight());
		primaryStage.setScene(scene);

		//For Debugging
		grid.setGridLinesVisible(false);
		if (grid.isGridLinesVisible()) {
			log.info("Application in debugging mode");
		}

		//Create the log output area
		TextArea logArea = new TextArea();
		logArea.setPrefSize(scene.getWidth(), scene.getHeight());
		logArea.setEditable(false);
		grid.add(logArea, 1, 0, 5, 5);

		//Create Status Ellipse
		status = new Ellipse(25, 25);
		status.setStroke(Paint.valueOf("Black"));
		status.setStrokeWidth(2.0);
		status.setFill(Paint.valueOf("Red"));
		grid.add(status, 0, 0);

		//Create Buttons
		Button start = new Button("Start");
		start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					//rio.connect();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				status.setFill(Paint.valueOf("Green"));
			}
		});
		grid.add(start, 0, 1);

		Button stop = new Button("Stop");
		stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//rio.disconnect();
				status.setFill(Paint.valueOf("Red"));
			}
		});
		grid.add(stop, 0, 2);

		Button settings = new Button("Settings");
		settings.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showSettings();
			}
		});
		grid.add(settings, 0, 3);

		primaryStage.show();

		//RIOConnection rio = new RIOConnection("10.64.79.1", 22, "luser", "");
		//rio.connect();
	}

	public void showSettings() {
		Stage settingsStage = new Stage();
		settingsStage.setTitle("Settings");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.TOP_LEFT);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));

		Scene scene = new Scene(grid,500,400);
		settingsStage.setScene(scene);

		Label teamLabel = new Label("Team: ");
		grid.add(teamLabel,0,0);

		TextField teamField = new TextField();
		teamField.setText(configHandler.getTeam());
		grid.add(teamField,1,0);

		Label ipLabel = new Label("Ip: ");
		grid.add(ipLabel,0,1);

		TextField ipField = new TextField();
		ipField.setText(configHandler.getIp());
		grid.add(ipField,1,1);

		Label portLabel = new Label("Port: ");
		grid.add(portLabel,0,2);

		TextField portField = new TextField();
		portField.setText(Integer.toString(configHandler.getPort()));
		grid.add(portField,1,2);

		Label loginLabel = new Label("Login: ");
		grid.add(loginLabel,0,3);

		TextField loginField = new TextField();
		loginField.setText(configHandler.getLogin());
		grid.add(loginField,1,3);

		Label passwdLabel = new Label("Password: ");
		grid.add(passwdLabel,0,4);

		TextField passwdField = new TextField();
		passwdField.setText(configHandler.getPassword());
		grid.add(passwdField,1,4);

		Button save = new Button("Save");
		save.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!teamField.getText().equals(configHandler.getTeam())) {
					configHandler.setTeam(teamField.getText());
				}

				if (!ipField.getText().equals(configHandler.getIp())) {
					configHandler.setIp(ipField.getText());
					configHandler.setIpUserSet(true);
				}

				if (!portField.getText().equals(configHandler.getPort())) {
					configHandler.setPort(Integer.parseInt(portField.getText()));
				}

				if (!loginField.getText().equals(configHandler.getLogin())) {
					configHandler.setLogin(loginField.getText());
				}

				if (!passwdField.getText().equals(configHandler.getPassword())) {
					configHandler.setPassword(passwdField.getText());
				}

				configHandler.reloadConfig();

				//Reload Text Fields
				if (!teamField.getText().equals(configHandler.getTeam())) {
					teamField.setText(configHandler.getTeam());
				}

				if (!ipField.getText().equals(configHandler.getIp())) {
					ipField.setText(configHandler.getIp());
				}

				if (!portField.getText().equals(configHandler.getPort())) {
					portField.setText(Integer.toString(configHandler.getPort()));
				}

				if (!loginField.getText().equals(configHandler.getLogin())) {
					loginField.setText(configHandler.getLogin());
				}

				if (!passwdField.getText().equals(configHandler.getPassword())) {
					passwdField.setText(configHandler.getPassword());
				}
			}
		});
		grid.add(save,0,5);

		settingsStage.show();
	}
}
