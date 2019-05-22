package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements Initializable {
    //************************************
    //***            BUTTONS           ***
    //************************************
    @FXML
    StackPane stack_pane;
    //************************************
    //***           VARIABLES          ***
    //************************************
    private GraphicsContext canvBack, canvVisual;
    private ArrayList<Strokes.stroke> strokes = new ArrayList<>(15);
    private int level;
    private double startX, startY, lastX, lastY, oldX, oldY;
    private boolean curDrawingBrush, curDrawingLine, curDrawingRectangle, curDrawingOval, curErasing;

    private KeyCombination keyCombCtrZ = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN);
    private KeyCombination keyCombCtrY = new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //two-layered system
        canvVisual = canvas_visual.getGraphicsContext2D();
        canvBack = canvas_main.getGraphicsContext2D();
        level = 0;

        //Slider min and max
        lineSizeSlide.setMin(1);
        lineSizeSlide.setMax(50);

        curDrawingBrush = true;
        curDrawingLine = false;
        curDrawingRectangle = false;
        curDrawingOval = false;
    }

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
    //TODO clear last x and y, so it won't draw new rectangles on new tapping
    @FXML
    public void KeyboardPressed(KeyEvent e) {
        if (keyCombCtrZ.match(e) || keyCombCtrY.match(e)) {
            if (level > 0) {
                strokes.remove(--level);
                clearCanvas();
                strokes.forEach(x -> x.reDraw(canvBack));
                System.out.println("Ctrl/Cmd + Z is pressed");

            }
        }
    }

    @FXML
    public void KeyboardReleased(KeyEvent e) {
//        System.out.println("released");
    }

    //*****************************************
    //***       Mouse controls start        ***
    //*****************************************

    @FXML
    public void pressedOnCanvas(MouseEvent e) {
        this.startX = e.getX();
        this.startY = e.getY();
        this.oldX = e.getX();
        this.oldY = e.getY();
        if (curDrawingBrush) {
            strokes.add(new Strokes.brush());
            level++;
        }
        if (curErasing) {
            strokes.add(new Strokes.eraser());
            level++;
        }
    }

    @FXML
    public void mouseReleased(MouseEvent e) {
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

    @FXML
    public void saveCanvas() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) canvas_main.getWidth(), (int) canvas_main.getHeight());
                canvas_main.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    //*****************************************
    //***       Drawing functions start     ***
    //*****************************************

    private void drawBrush() {
        ((Strokes.brush) strokes.get(level - 1)).brushes.add(new Strokes.brush(oldX, oldY, lastX, lastY, lineSizeSlide.getValue(), false, colorPicker.getValue()));
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
        strokes.add(new Strokes.line(startX, startY, lastX, lastY, lineSizeSlide.getValue(), false, colorPicker.getValue()));
        level++;
    }

    private void drawRectangle() {
        double wh = lastX - startX;
        double hg = lastY - startY;
        canvBack.setLineWidth(lineSizeSlide.getValue());

        double cornerX = (wh > 0 ? startX : startX + wh);
        double cornerY = (hg > 0 ? startY : startY + hg);
        wh = Math.abs(wh);
        hg = Math.abs(hg);

        if (drawFilled.isSelected()) {
            canvBack.setFill(colorPicker.getValue());
            canvBack.fillRect(cornerX, cornerY, wh, hg);

        } else {
            canvBack.setStroke(colorPicker.getValue());
            canvBack.strokeRect(cornerX, cornerY, wh, hg);
        }
        strokes.add(new Strokes.rectangular(wh, hg, cornerX, cornerY, lineSizeSlide.getValue(), drawFilled.isSelected(), colorPicker.getValue()));
        level++;
    }

    private void drawOval() {
        double wh = lastX - startX;
        double hg = lastY - startY;
        canvBack.setLineWidth(lineSizeSlide.getValue());

        double cornerX = (wh > 0 ? startX : startX + wh);
        double cornerY = (hg > 0 ? startY : startY + hg);
        wh = Math.abs(wh);
        hg = Math.abs(hg);

        if (drawFilled.isSelected()) {
            canvBack.setFill(colorPicker.getValue());
            canvBack.fillOval(cornerX, cornerY, wh, hg);
        } else {
            canvBack.setStroke(colorPicker.getValue());
            canvBack.strokeOval(cornerX, cornerY, wh, hg);
        }
        strokes.add(new Strokes.oval(wh, hg, cornerX, cornerY, lineSizeSlide.getValue(), drawFilled.isSelected(), colorPicker.getValue()));
        level++;
    }

    private void erasing() {
        double width = lineSizeSlide.getValue();
        ((Strokes.eraser) strokes.get(level - 1)).erases.add(new Strokes.eraser(lastX, lastY, width, width, 0, false, null));


        canvBack.clearRect(lastX, lastY, width, width);
        oldX = lastX;
        oldY = lastY;
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