package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gcB = canvas_main.getGraphicsContext2D();

        lineSizeSlide.setMin(1);
        lineSizeSlide.setMax(50);

        isFilled = true;
        curDrawingCircle = false;
        curDrawingBrush = true;
    }

    private GraphicsContext gcB, gcF;
    private double startX, startY, lastX, lastY, oldX, oldY;
    private boolean dragging, isFilled;
    private boolean curDrawingCircle, curDrawingBrush;

    @FXML
    Canvas canvas_main;
    @FXML
    Button drawCircle, drawBrush, drawSquare, drawTriangle, drawOval;
    @FXML
    Slider lineSizeSlide;
    @FXML
    ColorPicker colorPicker;
    @FXML
    CheckBox drawFilled;

    @FXML
    private void clearCanvas(ActionEvent e) {
        gcB.clearRect(0, 0, canvas_main.getWidth(), canvas_main.getHeight());
        gcF.clearRect(0, 0, canvas_main.getWidth(), canvas_main.getHeight());
    }

    @FXML
    public void pressedOnCanvas(MouseEvent e) {
        this.startX = e.getX();
        this.startY = e.getY();
        this.oldX = e.getX();
        this.oldY = e.getY();
        System.out.println("pressed : " + this.lastX + " - " + this.lastY);

    }

    @FXML
    public void mouseReleased(MouseEvent e) {
        dragging = false;
    }

    @FXML
    public void mouseDragged(MouseEvent e) {
        this.lastX = e.getX();
        this.lastY = e.getY();
        System.out.println("dragged : " + this.lastX + " - " + this.lastY);
        if (curDrawingBrush)
            freeDrawing();
    }

    @FXML
    public void onMouseExitedListener(MouseEvent event) {
        System.out.println("Mouse has disappeared");
    }

    private void freeDrawing() {
        gcB.setLineWidth(lineSizeSlide.getValue());
        gcB.setStroke(colorPicker.getValue());
        gcB.strokeLine(oldX, oldY, lastX, lastY);
        oldX = lastX;
        oldY = lastY;
    }

}
