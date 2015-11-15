package hu.jmx.javachallenge.fx.component;


import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Paint;

/**
 * Created by joci on 11/15/15.
 */
public class SimplePixelCanvas extends AnchorPane {

    private final Canvas canvas;
    private final DoubleProperty pixelWidth, pixelHeight;
    private Paint[][] current;
    protected final IntegerProperty rows, columns;

    public SimplePixelCanvas(double width, double height, int rows, int columns) {
        this.setWidth(width);
        this.setHeight(height);

        this.canvas = new Canvas();
        this.pixelWidth = new SimpleDoubleProperty();
        this.pixelHeight = new SimpleDoubleProperty();

        this.rows = new SimpleIntegerProperty(rows);
        this.columns = new SimpleIntegerProperty(columns);
        this.rows.addListener(this::onSizeChange);
        this.columns.addListener(this::onSizeChange);

        this.current = getInitialColors();

        this.pixelWidth.bind(widthProperty().divide(this.columns));
        this.pixelHeight.bind(heightProperty().divide(this.rows));
        this.pixelWidth.addListener(this::onPropertyChange);
        this.pixelHeight.addListener(this::onPropertyChange);

        this.canvas.widthProperty().bind(this.widthProperty());
        this.canvas.heightProperty().bind(this.heightProperty());

        widthProperty().addListener(this::onPropertyChange);
        heightProperty().addListener(this::onPropertyChange);

        getChildren().add(this.canvas);
    }


    private void onPropertyChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        repaint();
    }

    private void onSizeChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        this.current = getInitialColors();
        repaint();
    }

    public void drawPixel(int x, int y, Paint p) {
        //lower estimate for x and y offset
        final double offsetX = Math.floor(x * getPixelWidth().get());
        final double offsetY = Math.floor(y * getPixelHeight().get());
        //upper estimate for width and height
        final double rectangleWidth = Math.ceil(getPixelWidth().get());
        final double rectangleHeight = Math.ceil(getPixelHeight().get());
        GraphicsContext gc = getCanvas().getGraphicsContext2D();
        gc.setFill(p);
        gc.fillRect(offsetX, offsetY, rectangleWidth, rectangleHeight);

        current[x][y] = p;
    }

    public void drawMatrix(int[][] indices, List<Paint> paints) {
        for (int i = 0; i < indices.length; ++i) {
            for (int j = 0; j < indices[i].length; ++j) {
                drawPixel(i, j, paints.get(indices[i][j]));
            }
        }
    }

    public void repaint() {
        drawMatrix(this.current);
    }

    public void drawMatrix(Paint[][] paintMatrix) {
        for (int i = 0; i < current.length; ++i) {
            for (int j = 0; j < current[i].length; ++j) {
                drawPixel(i, j, paintMatrix[i][j]);
            }
        }
    }

    private Color[][] getInitialColors() {
        Color[][] initialColors = new Color[rows.get()][columns.get()];
        for (int i = 0; i < initialColors.length; ++i) {
            for (int j = 0; j < initialColors[i].length; ++j) {
                initialColors[i][j] = new Color(0, 0, 0, 0);
            }
        }
        return initialColors;
    }

    public int getRows() {
        return rows.get();
    }

    public void setRows(int newRow) {
        rows.set(newRow);
    }

    public int getColumns() {
        return columns.get();
    }

    public void setColumns(int newColumn) {
        columns.set(newColumn);
    }

    protected Canvas getCanvas() {
        return this.canvas;
    }

    protected DoubleProperty getPixelWidth() {
        return this.pixelWidth;
    }

    protected DoubleProperty getPixelHeight() {
        return this.pixelHeight;
    }
}

