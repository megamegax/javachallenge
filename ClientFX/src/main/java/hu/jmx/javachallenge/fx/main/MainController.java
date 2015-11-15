package hu.jmx.javachallenge.fx.main;

import hu.jmx.javachallenge.fx.component.SimplePixelCanvas;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable{
    @FXML
    private AnchorPane canvasContainer;
    private final SimplePixelCanvas canvas;
    private final List<Paint> colors;
    private final int[][] indices;

    {
        canvas = new SimplePixelCanvas(545, 406, 3, 3);
        indices = new int[][]{{0,0,1}, {1,2,1}, {1, 0, 0}};
        colors = Arrays.asList(Color.BLACK, Color.RED, Color.BLUE);
    }

    public void initialize(URL location, ResourceBundle resources) {
        canvasContainer.getChildren().add(canvas);
        canvas.drawMatrix(indices, colors);
    }
}
