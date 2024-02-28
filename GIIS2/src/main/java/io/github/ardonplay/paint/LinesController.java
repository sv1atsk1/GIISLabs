package io.github.ardonplay.paint;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import javafx.scene.paint.Color;

public class LinesController {
    private Canvas canvas;
    private Pair<Integer, Integer> firstPoint;
    private Pair<Integer, Integer> secondPoint;

    private final DrawService drawService;
    private boolean tapped;

    public LinesController(Canvas canvas) {
        this.canvas = canvas;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        this.drawService = new DrawServiceImpl(gc.getPixelWriter());
    }

    public void subscribe(String mode) {
        drawService.setMode(mode);
        this.canvas.setOnMousePressed(event -> {
            int x = (int) event.getX();
            int y = (int) event.getY();

            if (!tapped) {
                firstPoint = new Pair<>(x, y);
                tapped = true;
            } else {
                secondPoint = new Pair<>(x, y);
                drawService.printLine(firstPoint, secondPoint, Color.BLACK);
                tapped = false;
            }
        });
    }

    public void unsubscribe() {
        this.canvas.setOnMousePressed(event -> {
        });
    }
}