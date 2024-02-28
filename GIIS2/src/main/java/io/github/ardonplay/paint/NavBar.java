package io.github.ardonplay.paint;

import javafx.collections.FXCollections;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class NavBar extends HBox {
    private final LinesController linesController;

    public NavBar(Canvas canvas) {
        super();

        this.linesController = new LinesController(canvas);

        ComboBox<String> lineSelector = new ComboBox<>();
        lineSelector.setItems(FXCollections.observableArrayList("DDA", "Bresenham", "Vu", "Circle", "Ellipse", "Parabola", "Hyperbola"));
        lineSelector.setPromptText("Lines");

        lineSelector.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                linesController.unsubscribe();
            } else {
                linesController.subscribe(newValue);
            }
        });

//        Rectangle colorPreview = new Rectangle(100, 20);
//        colorPreview.setFill(Color.GRAY);

        super.setSpacing(10);
        super.setStyle("-fx-background-color: #000000; -fx-padding: 10px;");
        super.getChildren().addAll(lineSelector);
    }
}