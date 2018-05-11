package com.iboy.jriolog;

import com.iboy.jriolog.stages.AboutStage;
import com.iboy.jriolog.stages.SettingsStage;
import com.jcraft.jsch.JSchException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
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
    private Logger log;
    private ScheduledExecutorService scheduler;
    private ConfigHandler configHandler;
    private RIOConnection rio;
    private TextArea rioLog;
    private SettingsStage settingsStage;
    private AboutStage aboutStage;
    private Runnable rioLogFetcher = new Runnable() {
        @Override
        public void run() {
            BufferedReader br = new BufferedReader(new InputStreamReader(rio.getInput()));
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    rioLog.appendText(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    private Ellipse statusIcon;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image("/JRIOLog_Logo_noBackground.png"));

        log = Logger.getLogger(JRIOLog.class.getName());
        scheduler = Executors.newScheduledThreadPool(1);
        configHandler = new ConfigHandler();

        //Make a new RIOConnection
        rio = new RIOConnection(configHandler);

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
        grid.add(rioLog, 1, 0, 6, 6);

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
                rioLog.clear();

                boolean isConnected = false;

                try {
                    isConnected = rio.connect();
                } catch (JSchException e) {
                    //e.printStackTrace();
                }

                if (isConnected) {
                    statusIcon.setFill(Color.GREEN);
                    rioLog.clear();
                    rioLog.appendText("Connection Successful\n");
                    startScheduledExecutorService();
                    start.setDisable(true);
                    stop.setDisable(false);
                } else {
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
                if (settingsStage == null) {
                    settingsStage = new SettingsStage(configHandler);
                }
                settingsStage.show();
            }
        });
        grid.add(settings, 0, 3);

        Button about = new Button("About");
        about.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(aboutStage == null) {
                    aboutStage = new AboutStage();
                }
                aboutStage.show();
            }
        });
        grid.add(about, 0, 4);

        primaryStage.show();
    }

    @Override
    public void stop(){
        log.info("Exiting JRIOLog");
        //stopScheduledExecutorService();
        //rio.disconnect();
    }

    private void startScheduledExecutorService() {
        scheduler.execute(rioLogFetcher);
    }

    private void stopScheduledExecutorService() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
