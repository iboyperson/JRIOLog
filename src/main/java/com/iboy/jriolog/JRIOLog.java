package com.iboy.jriolog;

import com.jcraft.jsch.JSchException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class JRIOLog extends Application {
	public static Logger log = Logger.getLogger(JRIOLog.class.getName());
	private ScheduledExecutorService scheduler;

	private Runnable rioLogFetcher = new Runnable() {
		@Override
		public void run() {
			BufferedReader br = new BufferedReader(new InputStreamReader(rio.getInput()));
			String line;
			try {
				while ((line = br.readLine()) != null) {
					rioLog.appendText(line + "\n");
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private ConfigHandler configHandler;
	private RIOConnection rio;
	private TextArea rioLog;
	private Ellipse statusIcon;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		configHandler = new ConfigHandler();

		//Make a new RIOConnection
		if (configHandler.getPassword().isEmpty()) {
			rio = new RIOConnection(configHandler.getIp(), configHandler.getPort(), configHandler.getLogin());
		}
		else {
			rio = new RIOConnection(configHandler.getIp(), configHandler.getPort(), configHandler.getLogin(), configHandler.getPassword());
		}

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

		//Create Scene and set it as primaryStage scene
		Scene scene = new Scene(grid, primaryStage.getWidth(), primaryStage.getHeight());
		primaryStage.setScene(scene);

		// Show grid lines for Debugging
		grid.setGridLinesVisible(false);
		if (grid.isGridLinesVisible()) {
			log.info("Application in debugging mode");
		}

		//Create the log output area
		rioLog = new TextArea();
		rioLog.setPrefSize(scene.getWidth(), scene.getHeight());
		rioLog.setEditable(false);
		grid.add(rioLog, 1, 0, 5, 5);

		//Create Status Ellipse
		statusIcon = new Ellipse(25, 25);
		statusIcon.setStroke(Color.BLACK);
		statusIcon.setStrokeWidth(2.0);
		statusIcon.setFill(Color.RED);
		grid.add(statusIcon, 0, 0);

		//Create Buttons
		Button start = new Button("Start");
		Button stop = new Button("Stop");

		start.setDisable(false);
		stop.setDisable(true);

		start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				boolean isConnected = false;
				try {
					isConnected = rio.connect();
				}
				catch (JSchException | IOException e) {
					e.printStackTrace();
				}

				if(isConnected) {
					statusIcon.setFill(Color.GREEN);
					rioLog.clear();
					rioLog.appendText("Connection Successful\n");
					startScheduledExecutorService();
					start.setDisable(true);
					stop.setDisable(false);
				}
				else {
					rioLog.appendText("Connection Unsuccessful\n");
				}
			}
		});
		grid.add(start, 0, 1);

		stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				rio.disconnect();
				stopScheduledExecutorService();
				statusIcon.setFill(Color.RED);
				start.setDisable(false);
				stop.setDisable(true);
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

	private void startScheduledExecutorService() {
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.execute(rioLogFetcher);
		//scheduler.scheduleAtFixedRate(rioLogFetcher,1,1,TimeUnit.MILLISECONDS);
		//scheduler.schedule(rioLogFetcher,0,TimeUnit.MILLISECONDS);
	}

	private void stopScheduledExecutorService() {
		scheduler.shutdown();
	}
}
