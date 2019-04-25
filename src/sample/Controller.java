package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.*;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //two-layered system
        canvBack = canvas_main.getGraphicsContext2D();
        canvVisual = canvas_visual.getGraphicsContext2D();


        lastState = canvas_main.getGraphicsContext2D();


        lineSizeSlide.setMin(1);
        lineSizeSlide.setMax(50);

        curDrawingBrush = true;
        curDrawingLine = false;
        curDrawingRectangle = false;
        curDrawingOval = false;
    }

    //************************************
    //***           VARIABLES          ***
    //************************************
    private GraphicsContext canvBack, canvVisual;
    private double startX, startY, lastX, lastY, oldX, oldY;
    private boolean curDrawingBrush, curDrawingLine, curDrawingRectangle, curDrawingOval, curErasing;
    //For ctrl+z
    private GraphicsContext lastState;

    private KeyCombination keyCombCtrZ = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN);
    private KeyCombination keyCombCtrY = new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN);

    //************************************
    //***            BUTTONS           ***
    //************************************
    @FXML
    Canvas canvas_main, canvas_visual;
    @FXML
    Button BrushButton, LineButton, RectangleButton, OvalButton, ClearButton;
    @FXML
    Slider lineSizeSlide;
    @FXML
    ColorPicker colorPicker;
    @FXML
    CheckBox drawFilled;

    //*****************************************
    //***          Keyboard controls        ***
    //*****************************************

    //TODO undo/redo
    @FXML
    public void KeyboardPressed(KeyEvent e) {
        if (keyCombCtrZ.match(e) || keyCombCtrY.match(e)) {
            canvBack = lastState;
            System.out.println("Ctrl+Z/Y is pressed");
        }
    }

    @FXML
    public void KeyboardReleased(KeyEvent e) {
        System.out.println("released");
    }

    //*****************************************
    //***       Mouse controls start        ***
    //*****************************************

    @FXML
    public void pressedOnCanvas(MouseEvent e) {
//        lastState = canvBack;
//        System.out.println("added on stack");

        this.startX = e.getX();
        this.startY = e.getY();
        this.oldX = e.getX();
        this.oldY = e.getY();
    }

    @FXML
    public void mouseReleased(MouseEvent e) {
//        canvBack.save();
//        System.out.println("added on stack");

        canvVisual.clearRect(0, 0, canvas_main.getWidth(), canvas_main.getHeight());
        if (curDrawingLine)
            drawLine();
        if (curDrawingRectangle)
            drawRectangle();
        if (curDrawingOval)
            drawOval();
    }

    @FXML
    public void mouseDragged(MouseEvent e) {
        this.lastX = e.getX();
        this.lastY = e.getY();
        if (curDrawingBrush)
            drawBrush();
        if (curDrawingLine)
            drawLineVisual();
        if (curDrawingOval)
            drawOvalVisual();
        if (curDrawingRectangle)
            drawRectVisual();
        if (curErasing)
            erasing();
    }

    @FXML
    public void onMouseExitedListener(MouseEvent e) {
//        System.out.println("Mouse has disappeared");
    }


    //*****************************************
    //***       Buttons controls start      ***
    //*****************************************

    @FXML
    public void pressBrush() {
        curDrawingBrush = true;
        curDrawingLine = false;
        curDrawingRectangle = false;
        curDrawingOval = false;
        curErasing = false;
    }

    @FXML
    public void pressLine() {
        curDrawingBrush = false;
        curDrawingLine = true;
        curDrawingRectangle = false;
        curDrawingOval = false;
        curErasing = false;
    }

    @FXML
    public void pressRectangle() {
        curDrawingBrush = false;
        curDrawingLine = false;
        curDrawingRectangle = true;
        curDrawingOval = false;
        curErasing = false;
    }

    @FXML
    public void pressOval() {
        curDrawingBrush = false;
        curDrawingLine = false;
        curDrawingRectangle = false;
        curDrawingOval = true;
        curErasing = false;
    }

    @FXML
    public void pressEraser() {
        curDrawingBrush = false;
        curDrawingLine = false;
        curDrawingRectangle = false;
        curDrawingOval = false;
        curErasing = true;
        System.out.println("erasing");
    }

    @FXML
    private void clearCanvas() {
        canvBack.clearRect(0, 0, canvas_main.getWidth(), canvas_main.getHeight());
        canvVisual.clearRect(0, 0, canvas_main.getWidth(), canvas_main.getHeight());
    }


    //*****************************************
    //***       Drawing functions start     ***
    //*****************************************

    private void drawBrush() {
        canvBack.setLineWidth(lineSizeSlide.getValue());
        canvBack.setStroke(colorPicker.getValue());
        canvBack.strokeLine(oldX, oldY, lastX, lastY);
        oldX = lastX;
        oldY = lastY;
    }

    private void drawLine() {
        canvBack.setLineWidth(lineSizeSlide.getValue());
        canvBack.setStroke(colorPicker.getValue());
        canvBack.strokeLine(startX, startY, lastX, lastY);
    }

    private void drawRectangle() {
        double wh = lastX - startX;
        double hg = lastY - startY;
        canvBack.setLineWidth(lineSizeSlide.getValue());

        double cornerX = (wh > 0 ? startX : startX + wh);
        double cornerY = (hg > 0 ? startY : startY + hg);

        if (drawFilled.isSelected()) {
            canvBack.setFill(colorPicker.getValue());
            canvBack.fillRect(cornerX, cornerY, Math.abs(wh), Math.abs(hg));
        } else {
            canvBack.setStroke(colorPicker.getValue());
            canvBack.strokeRect(cornerX, cornerY, Math.abs(wh), Math.abs(hg));
        }
    }

    private void drawOval() {
        double wh = lastX - startX;
        double hg = lastY - startY;
        canvBack.setLineWidth(lineSizeSlide.getValue());

        double cornerX = (wh > 0 ? startX : startX + wh);
        double cornerY = (hg > 0 ? startY : startY + hg);

        if (drawFilled.isSelected()) {
            canvBack.setFill(colorPicker.getValue());
            canvBack.fillOval(cornerX, cornerY, Math.abs(wh), Math.abs(hg));
        } else {
            canvBack.setStroke(colorPicker.getValue());
            canvBack.strokeOval(cornerX, cornerY, Math.abs(wh), Math.abs(hg));
        }
    }

    private void erasing() {
        canvBack.clearRect(lastX, lastY, 10, 10);
        canvVisual.clearRect(lastX, lastY, 10, 10);
        oldX = lastX;
        oldY = lastY;
        System.out.printf("%f - %f : %f - %f\n", lastX, lastY, lastX + 5, lastY + 5);
    }


    //*****************************************
    //***       Drawing visuals start       ***
    //*****************************************

    private void drawLineVisual() {
        canvVisual.setLineWidth(lineSizeSlide.getValue());
        canvVisual.setStroke(colorPicker.getValue());
        canvVisual.clearRect(0, 0, canvas_main.getWidth(), canvas_main.getHeight());
        canvVisual.strokeLine(startX, startY, lastX, lastY);
    }

    private void drawOvalVisual() {
        double wh = lastX - startX;
        double hg = lastY - startY;
        canvVisual.setLineWidth(lineSizeSlide.getValue());

        double cornerX = (wh > 0 ? startX : startX + wh);
        double cornerY = (hg > 0 ? startY : startY + hg);

        if (drawFilled.isSelected()) {
            canvVisual.clearRect(0, 0, canvas_main.getWidth(), canvas_main.getHeight());
            canvVisual.setFill(colorPicker.getValue());
            canvVisual.fillOval(cornerX, cornerY, Math.abs(wh), Math.abs(hg));
        } else {
            canvVisual.clearRect(0, 0, canvas_main.getWidth(), canvas_main.getHeight());
            canvVisual.setStroke(colorPicker.getValue());
            canvVisual.strokeOval(cornerX, cornerY, Math.abs(wh), Math.abs(hg));
        }

    }

    private void drawRectVisual() {
        double wh = lastX - startX;
        double hg = lastY - startY;
        canvVisual.setLineWidth(lineSizeSlide.getValue());

        double cornerX = (wh > 0 ? startX : startX + wh);
        double cornerY = (hg > 0 ? startY : startY + hg);

        if (drawFilled.isSelected()) {
            canvVisual.clearRect(0, 0, canvas_main.getWidth(), canvas_main.getHeight());
            canvVisual.setFill(colorPicker.getValue());
            canvVisual.fillRect(cornerX, cornerY, Math.abs(wh), Math.abs(hg));
        } else {
            canvVisual.clearRect(0, 0, canvas_main.getWidth(), canvas_main.getHeight());
            canvVisual.setStroke(colorPicker.getValue());
            canvVisual.strokeRect(cornerX, cornerY, Math.abs(wh), Math.abs(hg));
        }
    }
}