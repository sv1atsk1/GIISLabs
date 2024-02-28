package io.github.ardonplay.paint;

import javafx.scene.paint.Color;
import javafx.util.Pair;

public interface DrawService {
    void setMode(String mode);
    void printLine(Pair<Integer, Integer> firstPoint, Pair<Integer, Integer> secondPoint, Color color);

    void basicLine(Pair<Integer, Integer> firstPoint, Pair<Integer, Integer> secondPoint, Color color);

    void bresenhamLine(Pair<Integer, Integer> firstPoint, Pair<Integer, Integer> secondPoint, Color color);

    void vuLine(Pair<Integer, Integer> firstPoint, Pair<Integer, Integer> secondPoint, Color color);

    void drawCircle(Pair<Integer, Integer> center, Color color);

    void drawEllipse(Pair<Integer, Integer> center, int a, int b, Color color);

    void drawParabola(Pair<Integer, Integer> focus, int a, int direction, Color color);

    void drawHyperbola(Pair<Integer, Integer> center, int a, int b, Color color);

}
