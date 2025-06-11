package org.example.demo;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageEditorApp extends Application {
    private static final double IMAGE_MAX_WIDTH = 400;
    private static final double IMAGE_MAX_HEIGHT = 400;
    private ImageView logoImageView = new ImageView();

    private ImageView originalImageView = new ImageView();
    private ImageView modifiedImageView = new ImageView();
    private ComboBox<String> operationComboBox = new ComboBox<>();
    private Button executeButton = new Button("Wykonaj");
    private Button saveButton = new Button("Zapisz");
    private File currentImageFile;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Edytor Obrazów - Politechnika Wrocławska");

        Image logoImage;
        try {
            logoImage = new Image(getClass().getResourceAsStream("/logo_pwr.png"));
            logoImageView.setImage(logoImage);
            logoImageView.setFitHeight(80);
            logoImageView.setPreserveRatio(true);
            logoImageView.setSmooth(true);
        } catch (Exception e) {
            System.err.println("Nie udało się załadować logo PWr");
        }

        BorderPane root = new BorderPane();

        Label welcomeLabel = new Label("Witaj poszukiwaczu!");
        VBox topBox = new VBox(5, logoImageView, welcomeLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(5));
        root.setTop(topBox);

        Label authorLabel = new Label("Autor: Michał Janusz");

        // Lewy panel: operacje i przyciski
        operationComboBox.getItems().addAll("Negatyw", "Progowanie", "Konturowanie", "Obrót");
        operationComboBox.setPromptText("Wybierz operację");

        Button loadButton = new Button("Wczytaj obraz");
        saveButton.setDisable(true);
        executeButton.setOnAction(e -> handleExecute());
        loadButton.setOnAction(e -> handleLoadImage());
        saveButton.setOnAction(e -> handleSaveImage());

        VBox leftPanel = new VBox(20, operationComboBox, loadButton, executeButton, saveButton);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new Insets(30, 15, 10, 15));
        leftPanel.setStyle("-fx-background-color: #F3F3F3;");

        originalImageView.setFitWidth(IMAGE_MAX_WIDTH);
        originalImageView.setFitHeight(IMAGE_MAX_HEIGHT);
        originalImageView.setPreserveRatio(true);
        originalImageView.setSmooth(true);

        modifiedImageView.setFitWidth(IMAGE_MAX_WIDTH);
        modifiedImageView.setFitHeight(IMAGE_MAX_HEIGHT);
        modifiedImageView.setPreserveRatio(true);
        modifiedImageView.setSmooth(true);

        VBox originalBox = new VBox(5, new Label("Oryginał"), originalImageView);
        originalBox.setAlignment(Pos.CENTER);

        VBox modifiedBox = new VBox(5, new Label("Zmodyfikowany"), modifiedImageView);
        modifiedBox.setAlignment(Pos.CENTER);

        HBox imageBox = new HBox(30, originalBox, modifiedBox);
        imageBox.setAlignment(Pos.CENTER);

        StackPane centerPane = new StackPane(imageBox);
        centerPane.setPadding(new Insets(15, 10, 10, 10));

        root.setLeft(leftPanel);
        root.setCenter(centerPane);

        root.setBottom(authorLabel);
        BorderPane.setAlignment(authorLabel, Pos.CENTER);
        BorderPane.setMargin(authorLabel, new Insets(10));

        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }

    private void handleLoadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Pliki JPG", "*.jpg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            if (!selectedFile.getName().toLowerCase().endsWith(".jpg")) {
                showToast("Niedozwolony format pliku");
                return;
            }
            try {
                Image image = new Image(new FileInputStream(selectedFile));
                originalImageView.setImage(image);
                modifiedImageView.setImage(image);
                currentImageFile = selectedFile;
                saveButton.setDisable(false);
                showToast("Pomyślnie załadowano plik");
            } catch (FileNotFoundException e) {
                showToast("Nie udało się załadować pliku");
            }
        }
    }

    private void handleExecute() {
        String operation = operationComboBox.getValue();
        if (operation == null) {
            showToast("Nie wybrano operacji do wykonania");
            return;
        }
        Image originalImage = originalImageView.getImage();
        if (originalImage == null) {
            showToast("Najpierw wczytaj obraz!");
            return;
        }

        Image resultImage = null;
        switch (operation) {
            case "Negatyw":
                resultImage = applyNegative(originalImage);
                break;
            case "Progowanie":
                resultImage = applyThresholding(originalImage, 128); // próg 128, można dać suwak
                break;
            case "Konturowanie":
                resultImage = applyContouring(originalImage);
                break;
            case "Obrót":
                resultImage = rotateImage(originalImage, 90);
                break;
        }
        if (resultImage != null) {
            modifiedImageView.setImage(resultImage);
            showToast("Wykonano operację: " + operation);
        }
    }

    private void handleSaveImage() {
        if (modifiedImageView.getImage() == null) return;

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Zapisz obraz");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setPadding(new Insets(10));

        TextField nameField = new TextField();
        nameField.setPromptText("Nazwa pliku (3-100 znaków)");

        Label validationLabel = new Label();
        validationLabel.setStyle("-fx-text-fill: red;");

        Button saveBtn = new Button("Zapisz");
        Button cancelBtn = new Button("Anuluj");

        saveBtn.setOnAction(e -> {
            String fileName = nameField.getText();
            if (fileName.length() < 3) {
                validationLabel.setText("Wpisz co najmniej 3 znaki");
                return;
            }
            File outputFile = new File(System.getProperty("C:/Users/Michal/Pictures") + ".jpg");
            if (outputFile.exists()) {
                showToast("Plik " + fileName + ".jpg już istnieje w systemie");
                return;
            }
            try {
                Image writableImage = modifiedImageView.getImage();
                outputFile = new File(System.getProperty("C:/Users/Michal/Pictures") + ".jpg");
//                WritableImage writableImage = new WritableImage(
//                        modifiedImageView.getImage().getPixelReader(),
//                        (int) modifiedImageView.getImage().getWidth(),
//                        (int) modifiedImageView.getImage().getHeight()
//                );
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(bufferedImage, "jpg", outputFile);
                showToast("Zapisano obraz w pliku " + fileName + ".jpg");
                dialog.close();
            } catch (IOException ex) {
                showToast("Nie udało się zapisać pliku " + fileName + ".jpg");
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        dialogVBox.getChildren().addAll(nameField, validationLabel, new HBox(10, saveBtn, cancelBtn));
        dialog.setScene(new Scene(dialogVBox));
        dialog.show();
    }

    private void showToast(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // NEGATYW
    private Image applyNegative(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage output = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = output.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = reader.getColor(x, y);
                writer.setColor(x, y, new Color(
                        1 - c.getRed(),
                        1 - c.getGreen(),
                        1 - c.getBlue(),
                        c.getOpacity()
                ));
            }
        }
        return output;
    }

    // PROGOWANIE
    private Image applyThresholding(Image image, int threshold) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage output = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = output.getPixelWriter();
        double t = threshold / 255.0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = reader.getColor(x, y);
                double avg = (c.getRed() + c.getGreen() + c.getBlue()) / 3.0;
                writer.setColor(x, y, avg > t ? Color.WHITE : Color.BLACK);
            }
        }
        return output;
    }

    // KONTOUROWANIE (prosty detektor)
    private Image applyContouring(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage output = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = output.getPixelWriter();
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width - 1; x++) {
                Color c = reader.getColor(x, y);
                Color cx = reader.getColor(x + 1, y);
                Color cy = reader.getColor(x, y + 1);
                double dx = Math.abs(c.getRed() - cx.getRed()) +
                        Math.abs(c.getGreen() - cx.getGreen()) +
                        Math.abs(c.getBlue() - cx.getBlue());
                double dy = Math.abs(c.getRed() - cy.getRed()) +
                        Math.abs(c.getGreen() - cy.getGreen()) +
                        Math.abs(c.getBlue() - cy.getBlue());
                double v = dx + dy;
                writer.setColor(x, y, v > 0.3 ? Color.BLACK : Color.WHITE);
            }
        }
        // Dla ostatniego rzędu i kolumny:
        for (int x = 0; x < width; x++) writer.setColor(x, height - 1, Color.WHITE);
        for (int y = 0; y < height; y++) writer.setColor(width - 1, y, Color.WHITE);
        return output;
    }

    // OBRÓT o 90 stopni w prawo
    private Image rotateImage(Image image, int angle) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage output = new WritableImage(height, width);
        PixelReader reader = image.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                output.getPixelWriter().setColor(height - 1 - y, x, reader.getColor(x, y));
            }
        }
        return output;
    }
}