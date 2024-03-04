package io.github.ardonplay.paint;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.List;

import static java.lang.Math.*;


public class DrawServiceImpl implements DrawService {

    private String mode;


    private final PixelWriter pixelWriter;

    public DrawServiceImpl(PixelWriter pixelWriter) {
        this.pixelWriter = pixelWriter;
    }

    @Override
    public void setMode(String mode){
        this.mode = mode;
    }
    @Override
    public void printLine(Pair<Integer, Integer> firstPoint, Pair<Integer, Integer> secondPoint, Color color) {
        switch (mode) {
            case "Vu" -> vuLine(firstPoint, secondPoint, color);
            case "Bresenham" -> bresenhamLine(firstPoint, secondPoint, color);
            case "DDA" -> basicLine(firstPoint, secondPoint, color);
            case "Circle" -> drawCircle(firstPoint, color);
            case "Ellipse" -> drawEllipse(firstPoint, 80, 50, color);
            case "Parabola" -> drawParabola(firstPoint, 50, -1, color);
            case "Hyperbola" -> drawHyperbola(firstPoint, 20, 30, color);
            case "Hermite" -> drawHermiteCurve(firstPoint, new Pair<>(secondPoint.getKey() + 100, secondPoint.getValue() + 100), secondPoint, new Pair<>(secondPoint.getKey() + 200, secondPoint.getValue()), color);
            case "Bezier" -> drawBezierCurve(List.of(firstPoint, new Pair<>(firstPoint.getKey() + 50, firstPoint.getValue() + 100), new Pair<>(secondPoint.getKey() - 50, secondPoint.getValue() - 100), secondPoint), color);
            case "BSpline" -> drawBSpline(List.of(firstPoint, new Pair<>(firstPoint.getKey() + 50, firstPoint.getValue() + 100), new Pair<>(secondPoint.getKey() - 50, secondPoint.getValue() - 100), secondPoint), color);
        }
    }

    @Override
    public void basicLine(Pair<Integer, Integer> firstPoint, Pair<Integer, Integer> secondPoint, Color color) {

        int x1 = firstPoint.getKey();
        int x2 = secondPoint.getKey();
        int y1 = firstPoint.getValue();
        int y2 = secondPoint.getValue();

        drawLine(x1, y1, x2, y2, color);
    }

    @Override
    public void bresenhamLine(Pair<Integer, Integer> firstPoint, Pair<Integer, Integer> secondPoint, Color color) {
        int x1 = firstPoint.getKey();
        int y1 = firstPoint.getValue();
        int x2 = secondPoint.getKey();
        int y2 = secondPoint.getValue();

        int deltaX = Math.abs(x2 - x1);
        int deltaY = Math.abs(y2 - y1);
        int signX = x1 < x2 ? 1 : -1;
        int signY = y1 < y2 ? 1 : -1;

        var error = deltaX - deltaY;

        pixelWriter.setColor(x2, y2, color);

        while (x1 != x2 || y1 != y2) {
            pixelWriter.setColor(x1, y1, color);

            var error2 = error * 2;
            if (error2 > -deltaY) {
                error -= deltaY;
                x1 += signX;
            }

            if (error2 < deltaX) {
                error += deltaX;
                y1 += signY;
            }
        }
    }

    @Override
    public void vuLine(Pair<Integer, Integer> firstPoint, Pair<Integer, Integer> secondPoint, Color color) {
        int x0 = firstPoint.getKey();
        int x1 = secondPoint.getKey();
        int y0 = firstPoint.getValue();
        int y1 = secondPoint.getValue();

        boolean steep = abs(y1 - y0) > abs(x1 - x0);
        if (steep)
           vuLine(new Pair<>(y0, x0), new Pair<>(y1, x1), color);

        if (x0 > x1)
            vuLine(new Pair<>(x1, y1), new Pair<>(x0, y0), color);

        double dx = x1 - x0;
        double dy = y1 - y0;
        double gradient = dy / dx;

        double xend = round(x0);
        double yend = y0 + gradient * (xend - x0);
        double xgap = rfpart(x0 + 0.5);
        double xpxl1 = xend;
        double ypxl1 = ipart(yend);

        if (steep) {
            plot(ypxl1, xpxl1, rfpart(yend) * xgap, color);
            plot(ypxl1 + 1, xpxl1, fpart(yend) * xgap, color);
        } else {
            plot(xpxl1, ypxl1, rfpart(yend) * xgap, color);
            plot(xpxl1, ypxl1 + 1, fpart(yend) * xgap, color);
        }

        double intery = yend + gradient;

        xend = round(x1);
        yend = y1 + gradient * (xend - x1);
        xgap = fpart(x1 + 0.5);
        double xpxl2 = xend;
        double ypxl2 = ipart(yend);

        if (steep) {
            plot(ypxl2, xpxl2, rfpart(yend) * xgap, color);
            plot(ypxl2 + 1, xpxl2, fpart(yend) * xgap, color);
        } else {
            plot(xpxl2, ypxl2, rfpart(yend) * xgap, color);
            plot(xpxl2, ypxl2 + 1, fpart(yend) * xgap, color);
        }

        for (double x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
            if (steep) {
                plot(ipart(intery), x, rfpart(intery), color);
                plot(ipart(intery) + 1, x, fpart(intery), color);
            } else {
                plot(x, ipart(intery), rfpart(intery), color);
                plot(x, ipart(intery) + 1, fpart(intery), color);
            }
            intery = intery + gradient;
        }

    }


    int ipart(double x) {
        return (int) x;
    }

    double fpart(double x) {
        return x - floor(x);
    }

    double rfpart(double x) {
        return 1.0 - fpart(x);
    }


    void plot(double x, double y, double c, Color color) {
        pixelWriter.setColor((int) x, (int) y, new Color(color.getRed(), color.getGreen(), color.getBlue(), c));
    }


    private void drawLine(int x1, int y1, int x2, int y2, Color color) {

        double length = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));

        double deltaX = (x2 - x1) / length;

        double deltaY = (y2 - y1) / length;

        double xren = (x1 + 0.5 * Math.signum(deltaX));
        double yren = (y1 + 0.5 * Math.signum(deltaY));

        pixelWriter.setColor((int) xren, (int) yren, color);

        double i = 0;
        while (i <= length) {
            xren = xren + deltaX;
            yren = yren + deltaY;
            pixelWriter.setColor((int) xren, (int) yren, color);
            i = i + 1;
        }
    }

    public void drawCircle(Pair<Integer, Integer> center, Color color) {
        int centerX = center.getKey();
        int centerY = center.getValue();

        int radius = 20;
        int y = 0;
        int decisionOver2 = 1 -radius;

        while (radius >= y) {
            drawCirclePoints(centerX, centerY, radius, y, color);
            y++;

            if (decisionOver2 <= 0) {
                decisionOver2 += 2 * y + 1;
            } else {
                radius--;
                decisionOver2 += 2 * (y - radius) + 1;
            }
            drawCirclePoints(centerX, centerY, radius, y, color);
        }
    }

    private void drawCirclePoints(int centerX, int centerY, int x, int y, Color color) {
        pixelWriter.setColor(centerX + x, centerY + y, color);
        pixelWriter.setColor(centerX - x, centerY + y, color);
        pixelWriter.setColor(centerX + x, centerY - y, color);
        pixelWriter.setColor(centerX - x, centerY - y, color);
        pixelWriter.setColor(centerX + y, centerY + x, color);
        pixelWriter.setColor(centerX - y, centerY + x, color);
        pixelWriter.setColor(centerX + y, centerY - x, color);
        pixelWriter.setColor(centerX - y, centerY - x, color);
    }

    public void drawEllipse(Pair<Integer, Integer> center, int a, int b, Color color) {
        int centerX = center.getKey();
        int centerY = center.getValue();

        for (int x = -a; x <= a; x++) {
            int y = (int) Math.round(b * Math.sqrt(1 - (x * x) / (double) (a * a)));
            drawEllipsePoints(centerX, centerY, x, y, color);
            drawEllipsePoints(centerX, centerY, x, -y, color);
        }
    }

    private void drawEllipsePoints(int centerX, int centerY, int x, int y, Color color) {
        pixelWriter.setColor(centerX + x, centerY + y, color);
        pixelWriter.setColor(centerX - x, centerY + y, color);
    }

    public void drawParabola(Pair<Integer, Integer> focus, int a, int direction, Color color) {
        int focusX = focus.getKey();
        int focusY = focus.getValue();

        for (int x = -a; x <= a; x++) {
            int y = direction * x * x / (2 * a);
            drawParabolaPoints(focusX, focusY, x, y, color);
        }
    }

    private void drawParabolaPoints(int centerX, int centerY, int x, int y, Color color) {
        pixelWriter.setColor(centerX + x, centerY + y, color);
        pixelWriter.setColor(centerX - x, centerY + y, color);
    }

    public void drawHyperbola(Pair<Integer, Integer> center, int a, int b, Color color) {
        int centerX = center.getKey();
        int centerY = center.getValue();

        for (int x = -a; x <= a; x++) {
            int y = (int) Math.round(b * Math.sqrt(1 + (x * x) / (double) (a * a)));
            drawHyperbolaPoints(centerX, centerY, x, y, color);
            drawHyperbolaPoints(centerX, centerY, x, -y, color);
        }
    }

    private void drawHyperbolaPoints(int centerX, int centerY, int x, int y, Color color) {
        pixelWriter.setColor(centerX + x, centerY + y, color);
        pixelWriter.setColor(centerX - x, centerY + y, color);
    }

    @Override
    public void drawHermiteCurve(Pair<Integer, Integer> startPoint, Pair<Integer, Integer> endPoint,
                                 Pair<Integer, Integer> startTangent, Pair<Integer, Integer> endTangent,
                                 Color color) {
        int startX = startPoint.getKey();
        int startY = startPoint.getValue();
        int endX = endPoint.getKey();
        int endY = endPoint.getValue();
        int startTanX = startTangent.getKey();
        int startTanY = startTangent.getValue();
        int endTanX = endTangent.getKey();
        int endTanY = endTangent.getValue();

        double t = 0.0;
        double step = 0.01;

        while (t <= 1.0) {
            double x = calculateHermiteValue(startX, startTanX, endX, endTanX, t);
            double y = calculateHermiteValue(startY, startTanY, endY, endTanY, t);
            pixelWriter.setColor((int) x, (int) y, color);
            t += step;
        }
    }

    private double calculateHermiteValue(int p0, int t0, int p1, int t1, double t) {
        double t2 = t * t;
        double t3 = t2 * t;
        double h1 = 2 * t3 - 3 * t2 + 1;
        double h2 = -2 * t3 + 3 * t2;
        double h3 = t3 - 2 * t2 + t;
        double h4 = t3 - t2;

        return h1 * p0 + h2 * p1 + h3 * t0 + h4 * t1;
    }

    @Override
    public void drawBezierCurve(List<Pair<Integer, Integer>> controlPoints, Color color) {
        int n = controlPoints.size() - 1;
        double step = 0.01;

        for (double t = 0.0; t <= 1.0; t += step) {
            double x = 0.0;
            double y = 0.0;

            for (int i = 0; i <= n; i++) {
                double coefficient = binomialCoefficient(n, i) * Math.pow(t, i) * Math.pow(1 - t, n - i);
                x += coefficient * controlPoints.get(i).getKey();
                y += coefficient * controlPoints.get(i).getValue();
            }

            pixelWriter.setColor((int) x, (int) y, color);
        }
    }

    private int binomialCoefficient(int n, int k) {
        int res = 1;
        if (k > n - k)
            k = n - k;
        for (int i = 0; i < k; ++i) {
            res *= (n - i);
            res /= (i + 1);
        }
        return res;
    }

    @Override
    public void drawBSpline(List<Pair<Integer, Integer>> controlPoints, Color color) {
        int n = controlPoints.size() - 1;
        double step = 0.01;

        for (double t = 2; t <= n; t += step) {
            double x = 0.0;
            double y = 0.0;

            for (int i = 0; i <= n; i++) {
                double basis = bSplineBasis(i, 3, t);
                x += basis * controlPoints.get(i).getKey();
                y += basis * controlPoints.get(i).getValue();
            }

            int roundedX = (int) Math.round(x);
            int roundedY = (int) Math.round(y);
            pixelWriter.setColor(roundedX, roundedY, color);
        }
    }

    private double bSplineBasis(int i, int k, double t) {
        if (k == 1) {
            if (i <= t && t < i + 1)
                return 1;
            return 0;
        }
        double denominator1 = i + k - 1 - i;
        double denominator2 = i + k - 1 - (i + k - 1 - 1);
        double basis1 = 0, basis2 = 0;
        if (denominator1 != 0)
            basis1 = ((t - i) / denominator1) * bSplineBasis(i, k - 1, t);
        if (denominator2 != 0)
            basis2 = ((i + k - t) / denominator2) * bSplineBasis(i + 1, k - 1, t);
        return basis1 + basis2;
    }




}
