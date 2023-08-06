package com.example.connect4game;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String discColor1 = "#24303E";
	private static final String discColor2 = "#4CAA88";

	private boolean isPlayer1Turn = true;

	private static String PLAYER1 = "Player 1";
	private static String PLAYER2 = "Player 2";

	private Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS];

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscsPane;

	@FXML
	public Label playerNameLabel;

	@FXML
	public TextField playerOneTextField;

	@FXML
	public TextField playerTwoTextField;

	@FXML
	public Button setNamesButton;

	private boolean isAllowedToInsert = true;

	public void createPlayground() {
		Shape rectangleWithHoles = createGameStructuralGrid();

		rootGridPane.add(rectangleWithHoles, 0, 1);

		List<Rectangle> rectangleList = createClickableColumns();
		for (Rectangle rectangle : rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}

		setNamesButton.setOnAction(event -> {
			String s1 = playerOneTextField.getText().trim();
			String s2 = playerTwoTextField.getText().trim();
			if (!s1.isEmpty()) {
				PLAYER1 = s1;
				playerNameLabel.setText(PLAYER1 + "'s Turn");

			}
			if (!s2.isEmpty()) {
				PLAYER2 = s2;
			}
		});
	}

	public Shape createGameStructuralGrid() {
		Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);

		for (int row = 0; row < ROWS; row++) {
			for (int column = 0; column < COLUMNS; column++) {
				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER / 2);
				circle.setCenterX(CIRCLE_DIAMETER / 2);
				circle.setCenterY(CIRCLE_DIAMETER / 2);
				circle.setSmooth(true);
				circle.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);

			}
		}

		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableColumns() {
		List<Rectangle> rectangleList = new ArrayList<>();
		for (int column = 0; column < COLUMNS; column++) {
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			int finalColumn = column;
			rectangle.setOnMouseClicked(event -> {

				if (isAllowedToInsert) {
					isAllowedToInsert = false;
					insertDisc(new Disc(isPlayer1Turn), finalColumn);
				}

			});
			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private void insertDisc(Disc disc, int column) {

		int row = ROWS - 1;
		while (row >= 0) {
			if (getDiscIfPresent(row, column) == null)
				break;
			row--;

		}
		if (row < 0)
			return;
		insertedDiscsArray[row][column] = disc;
		insertedDiscsPane.getChildren().add(disc);

		disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		int currentRow = row;
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		translateTransition.setOnFinished(event -> {
			isAllowedToInsert = true;
			if (gameEnded(currentRow, column)) {
				gameOver();
				return;

			}
			isPlayer1Turn = !isPlayer1Turn;
			playerNameLabel.setText(isPlayer1Turn ? PLAYER1 : PLAYER2);
		});
		translateTransition.play();

	}

	private boolean gameEnded(int row, int column) {

		List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3).mapToObj(r -> new Point2D(r, column))
				.collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
				.mapToObj(c -> new Point2D(row, c)).collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row - 3, column + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6).mapToObj(i -> startPoint1.add(i, -i))
				.collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row - 3, column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6).mapToObj(i -> startPoint2.add(i, i))
				.collect(Collectors.toList());

		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
				|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {

		int chain = 0;
		for (Point2D point : points) {

			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

			if (disc != null && disc.isPlayer1Move == isPlayer1Turn) {
				chain++;
				if (chain == 4) {
					return true;
				}

			} else {
				chain = 0;
			}

		}
		return false;
	}

	private Disc getDiscIfPresent(int row, int column) {
		if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0)
			return null;

		return insertedDiscsArray[row][column];

	}

	private void gameOver() {

		String winner = isPlayer1Turn ? PLAYER1 : PLAYER2;
		System.out.println("Winner is: " + winner);
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connet 4 Game");
		alert.setHeaderText("The Winner is " + winner);
		alert.setContentText("Want to play again?");
		ButtonType yesButton = new ButtonType("Yes");
		ButtonType noButton = new ButtonType(" No, Exit");
		alert.getButtonTypes().setAll(yesButton, noButton);
		Platform.runLater(() -> {
			Optional<ButtonType> buttonClicked = alert.showAndWait();
			if (buttonClicked.isPresent() && buttonClicked.get() == yesButton) {
				// todo
				restGame();

			} else {
				Platform.exit();
				System.exit(0);

			}

		});

	}

	public void restGame() {
		insertedDiscsPane.getChildren().clear();
		for (int row = 0; row < insertedDiscsArray.length; row++) {
			for (int col = 0; col < insertedDiscsArray[row].length; col++) {
				insertedDiscsArray[row][col] = null;

			}
		}
		isPlayer1Turn = true;
		playerNameLabel.setText(PLAYER1);
		createPlayground();
	}

	private static class Disc extends Circle {
		private final boolean isPlayer1Move;

		public Disc(boolean isPlayer1Move) {
			this.isPlayer1Move = isPlayer1Move;
			setRadius(CIRCLE_DIAMETER / 2);
			setFill(isPlayer1Move ? Color.valueOf(discColor1) : Color.valueOf(discColor2));
			setCenterX(CIRCLE_DIAMETER / 2);
			setCenterY(CIRCLE_DIAMETER / 2);
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}
}
