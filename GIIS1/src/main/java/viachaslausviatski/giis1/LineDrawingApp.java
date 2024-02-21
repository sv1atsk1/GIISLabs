package viachaslausviatski.giis1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javafx.scene.paint.Color;

public class LineDrawingApp extends Application {

    private enum Algorithm {
        DDA,
        BresenhamInteger,
        Wu
    }

    private Algorithm selectedAlgorithm = Algorithm.DDA;
    private Canvas canvas;
    private GraphicsContext gc;
    private TextField startXField;
    private TextField startYField;
    private TextField endXField;
    private TextField endYField;
    private CheckBox debugCheckBox;

    private int startX_DDA = 50;
    private int startY_DDA = 50;
    private int endX_DDA = 550;
    private int endY_DDA = 350;

    private int startX_BresenhamInteger = 50;
    private int startY_BresenhamInteger = 50;
    private int endX_BresenhamInteger = 550;
    private int endY_BresenhamInteger = 350;

    private int startX_Wu = 50;
    private int startY_Wu = 50;
    private int endX_Wu = 550;
    private int endY_Wu = 350;

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(600, 400);
        gc = canvas.getGraphicsContext2D();

        ComboBox<Algorithm> algorithmComboBox = new ComboBox<>();
        algorithmComboBox.getItems().addAll(Algorithm.values());
        algorithmComboBox.setValue(selectedAlgorithm);
        algorithmComboBox.setOnAction(e -> {
            selectedAlgorithm = algorithmComboBox.getValue();
            redraw();
        });

        startXField = new TextField("50");
        startYField = new TextField("50");
        endXField = new TextField("550");
        endYField = new TextField("350");

        Button drawButton = new Button("Draw Line");
        drawButton.setOnAction(e -> drawLine());

        debugCheckBox = new CheckBox("Debug Mode");
        debugCheckBox.setOnAction(e -> redraw());

        HBox coordinateBox = new HBox(10);
        coordinateBox.getChildren().addAll(
                startXField, startYField, endXField, endYField, drawButton, debugCheckBox
        );

        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setTop(algorithmComboBox);
        root.setBottom(coordinateBox);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Line Drawing App");
        primaryStage.show();

        redraw();
    }

    private void drawLine() {
        int startX = Integer.parseInt(startXField.getText());
        int startY = Integer.parseInt(startYField.getText());
        int endX = Integer.parseInt(endXField.getText());
        int endY = Integer.parseInt(endYField.getText());

        switch (selectedAlgorithm) {
            case DDA:
                startX_DDA = startX;
                startY_DDA = startY;
                endX_DDA = endX;
                endY_DDA = endY;
                break;
            case BresenhamInteger:
                startX_BresenhamInteger = startX;
                startY_BresenhamInteger = startY;
                endX_BresenhamInteger = endX;
                endY_BresenhamInteger = endY;
                break;
            case Wu:
                startX_Wu = startX;
                startY_Wu = startY;
                endX_Wu = endX;
                endY_Wu = endY;
                break;
        }

        redraw();
    }

    private void drawLine(int x1, int y1, int x2, int y2) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setFill(Color.BLACK);
        switch (selectedAlgorithm) {
            case DDA:
                drawDDALine(x1, y1, x2, y2);
                break;
            case BresenhamInteger:
                drawBresenhamIntegerLine(x1, y1, x2, y2);
                break;
            case Wu:
                drawWuLine(x1, y1, x2, y2);
                break;
        }
    }


    private void drawDDALine(int x1, int y1, int x2, int y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double steps = Math.max(Math.abs(dx), Math.abs(dy));
        double xIncrement = dx / steps;
        double yIncrement = dy / steps;
        double x = x1;
        double y = y1;

        for (int i = 0; i <= steps; i++) {
            gc.fillRect(Math.round(x), Math.round(y), 1, 1);
            x += xIncrement;
            y += yIncrement;
        }
    }

    private void drawBresenhamIntegerLine(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (x1 != x2 || y1 != y2) {
            gc.fillRect(x1, y1, 1, 1);
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    private void drawWuLine(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        boolean steep = dy > dx;

        if (steep) {
            int temp = x1;
            x1 = y1;
            y1 = temp;

            temp = x2;
            x2 = y2;
            y2 = temp;
        }

        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;

            temp = y1;
            y1 = y2;
            y2 = temp;
        }

        dx = x2 - x1;
        dy = y2 - y1;

        double gradient = (double) dy / dx;

        int xend = Math.round(x1);
        double yend = y1 + gradient * (xend - x1);
        double xgap = 1 - fractionalPart(x1 + 0.5);

        int xpxl1 = xend;
        int ypxl1 = (int) Math.floor(yend);

        if (steep) {
            plot(ypxl1, xpxl1, fractionalPart(yend) * xgap);
            plot(ypxl1 + 1, xpxl1, fractionalPart(yend) * (1 - xgap));
        } else {
            plot(xpxl1, ypxl1, fractionalPart(yend) * xgap);
            plot(xpxl1, ypxl1 + 1, fractionalPart(yend) * (1 - xgap));
        }

        double intery = yend + gradient;

        xend = Math.round(x2);
        yend = y2 + gradient * (xend - x2);
        xgap = fractionalPart(x2 + 0.5);

        int xpxl2 = xend;
        int ypxl2 = (int) Math.floor(yend);

        if (steep) {
            plot(ypxl2, xpxl2, fractionalPart(yend) * xgap);
            plot(ypxl2 + 1, xpxl2, fractionalPart(yend) * (1 - xgap));
        } else {
            plot(xpxl2, ypxl2, fractionalPart(yend) * xgap);
            plot(xpxl2, ypxl2 + 1, fractionalPart(yend) * (1 - xgap));
        }

        if (steep) {
            for (int x = xpxl1 + 1; x < xpxl2; x++) {
                plot((int) Math.floor(intery), x, fractionalPart(intery));
                plot((int) Math.floor(intery) + 1, x, fractionalPart(intery));
                intery += gradient;
            }
        } else {
            for (int x = xpxl1 + 1; x < xpxl2; x++) {
                plot(x, (int) Math.floor(intery), fractionalPart(intery));
                plot(x, (int) Math.floor(intery) + 1, fractionalPart(intery));
                intery += gradient;
            }
        }
    }

    private void plot(int x, int y, double intensity) {
        gc.fillRect(x, y, 1, 1 - intensity);
        gc.fillRect(x, y + 1, 1, intensity);
    }

    private void plotPixel(int x, int y, double brightness) {
        brightness = Math.max(0, Math.min(brightness, 1));
        Color color = Color.gray(brightness);
        gc.setFill(color);
        gc.fillRect(x, y, 1, 1);
    }

    private double fractionalPart(double x) {
        return x - Math.floor(x);
    }

    private void redraw() {
        switch (selectedAlgorithm) {
            case DDA:
                if (debugCheckBox.isSelected()) {
                    drawDebugDDALine(startX_DDA, startY_DDA, endX_DDA, endY_DDA);
                } else {
                    drawLine(startX_DDA, startY_DDA, endX_DDA, endY_DDA);
                }
                break;
            case BresenhamInteger:
                if (debugCheckBox.isSelected()) {
                    drawDebugBresenhamIntegerLine(startX_BresenhamInteger, startY_BresenhamInteger, endX_BresenhamInteger, endY_BresenhamInteger);
                } else {
                    drawLine(startX_BresenhamInteger, startY_BresenhamInteger, endX_BresenhamInteger, endY_BresenhamInteger);
                }
                break;
            case Wu:
                if (debugCheckBox.isSelected()) {
                    drawDebugWuLine(startX_Wu, startY_Wu, endX_Wu, endY_Wu);
                } else {
                    drawLine(startX_Wu, startY_Wu, endX_Wu, endY_Wu);
                }
                break;
        }
    }

    private void drawDebugDDALine(int x1, int y1, int x2, int y2) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        double dx = x2 - x1;
        double dy = y2 - y1;
        double steps = Math.max(Math.abs(dx), Math.abs(dy));
        double xIncrement = dx / steps;
        double yIncrement = dy / steps;
        double x = x1;
        double y = y1;

        for (int i = 0; i <= steps; i++) {
            plotPixel((int) x, (int) y, 0.0);
            x += xIncrement;
            y += yIncrement;
        }
    }
    private void drawDebugBresenhamIntegerLine(int x1, int y1, int x2, int y2) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        double distance = Math.sqrt(dx * dx + dy * dy);

        while (x1 != x2 || y1 != y2) {
            double brightness = 1.0 - (Math.abs(dx * (y1 - y2) - (x1 - x2) * dy) / distance);
            plotPixel(x1, y1, brightness);
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    private void drawDebugWuLine(int x1, int y1, int x2, int y2) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        boolean steep = dy > dx;

        if (steep) {
            int temp = x1;
            x1 = y1;
            y1 = temp;

            temp = x2;
            x2 = y2;
            y2 = temp;
        }

        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;

            temp = y1;
            y1 = y2;
            y2 = temp;
        }

        dx = x2 - x1;
        dy = y2 - y1;

        double gradient = (double) dy / dx;

        double xend = Math.round(x1);
        double yend = y1 + gradient * (xend - x1);
        double xgap = 1 - fractionalPart(x1 + 0.5);

        int xpxl1 = (int) xend;
        int ypxl1 = (int) yend;

        if (steep) {
            plotPixel(ypxl1, xpxl1, 1 - fractionalPart(yend) * xgap);
            plotPixel(ypxl1 + 1, xpxl1, fractionalPart(yend) * xgap);
        } else {
            plotPixel(xpxl1, ypxl1, 1 - fractionalPart(yend) * xgap);
            plotPixel(xpxl1, ypxl1 + 1, fractionalPart(yend) * xgap);
        }

        double intery = yend + gradient;

        xend = Math.round(x2);
        yend = y2 + gradient * (xend - x2);
        xgap = fractionalPart(x2 + 0.5);

        int xpxl2 = (int) xend;
        int ypxl2 = (int) yend;

        if (steep) {
            plotPixel(ypxl2, xpxl2, 1 - fractionalPart(yend) * xgap);
            plotPixel(ypxl2 + 1, xpxl2, fractionalPart(yend) * xgap);
        } else {
            plotPixel(xpxl2, ypxl2, 1 - fractionalPart(yend) * xgap);
            plotPixel(xpxl2, ypxl2 + 1, fractionalPart(yend) * xgap);
        }

        if (steep) {
            for (int x = xpxl1 + 1; x < xpxl2; x++) {
                double brightness = 1.0 - fractionalPart(intery);
                plotPixel((int) Math.floor(intery), x, brightness);
                plotPixel((int) Math.floor(intery) + 1, x, 1 - brightness);
                intery += gradient;
            }
        } else {
            for (int x = xpxl1 + 1; x < xpxl2; x++) {
                double brightness = 1.0 - fractionalPart(intery);
                plotPixel(x, (int) Math.floor(intery), brightness);
                plotPixel(x, (int) Math.floor(intery) + 1, 1 - brightness);
                intery += gradient;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}