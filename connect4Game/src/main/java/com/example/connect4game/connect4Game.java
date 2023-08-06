package com.example.connect4game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class connect4Game extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	private Controller controller;

	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("app_layout.fxml"));
		GridPane rootGridPane = loader.load();
		controller = loader.getController();
		controller.createPlayground();

		MenuBar menuBar = createMenu();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().add(menuBar);
		Scene scene = new Scene(rootGridPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect 4");
		primaryStage.setResizable(false);
		primaryStage.show();

	}

	private MenuBar createMenu() {
		Menu fileMenu = new Menu("File");
		MenuItem newGame = new MenuItem("New Game");
		newGame.setOnAction(actionEvent -> controller.restGame());
		MenuItem resetGame = new MenuItem("Reset Game");
		resetGame.setOnAction(actionEvent -> controller.restGame());
		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
		MenuItem exitGame = new MenuItem(" Exit Game");
		exitGame.setOnAction(actionEvent -> exitGame());
		fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

		Menu helpMenu = new Menu("Help");
		MenuItem aboutGame = new MenuItem("About Game");
		aboutGame.setOnAction(event -> aboutGame());
		MenuItem aboutDeveloper = new MenuItem("About Developer");
		aboutDeveloper.setOnAction(event -> aboutDeveloper());
		helpMenu.getItems().addAll(aboutGame, aboutDeveloper);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, helpMenu);
		return menuBar;

	}

	private void aboutDeveloper() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About Developer");
		alert.setHeaderText("Shivam Shahane");
		alert.setContentText(
				"I love to play this game from my childhood. This game is very good and entertaining.It will help you to improve Logic.");
		alert.show();
	}

	private void aboutGame() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About Connect 4");
		alert.setHeaderText("How to play game");
		alert.setContentText(
				"Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing the right moves.");
		alert.show();
	}

	private void exitGame() {
		Platform.exit();
		System.exit(0);
	}

	private void resetGame() {

	}

}
