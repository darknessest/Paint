package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

import java.util.ArrayList;

class Strokes {
    public abstract static class stroke {
        private double wh;
        private double hg;
        private double cornerX;
        private double cornerY;
        private double brushWidth;
        private boolean isFilled;
        private Paint color;

        public stroke(double wh, double hg, double cornerX, double cornerY, double brushWidth, boolean isFilled, Paint color) {
            this.wh = wh;
            this.hg = hg;
            this.cornerX = cornerX;
            this.cornerY = cornerY;
            this.brushWidth = brushWidth;
            this.isFilled = isFilled;
            this.color = color;
        }

        public double getWh() {
            return wh;
        }

        public double getHg() {
            return hg;
        }

        public double getCornerX() {
            return cornerX;
        }

        public double getCornerY() {
            return cornerY;
        }

        public double getBrushWidth() {
            return brushWidth;
        }

        public boolean isFilled() {
            return isFilled;
        }

        public Paint getColor() {
            return color;
        }

        abstract void reDraw(GraphicsContext gc);
    }

    public static class line extends stroke {

        public line(double wh, double hg, double cornerX, double cornerY, double brushWidth, boolean isFilled, Paint color) {
            super(wh, hg, cornerX, cornerY, brushWidth, isFilled, color);
        }

        @Override
        void reDraw(GraphicsContext gc) {
            gc.setLineWidth(super.brushWidth);
            gc.setStroke(super.color);
            gc.strokeLine(super.cornerX, super.cornerY, super.wh, super.hg);
        }
    }


    public static class rectangular extends stroke {
        public rectangular(double wh, double hg, double cornerX, double cornerY, double brushWidth, boolean isFilled, Paint color) {
            super(wh, hg, cornerX, cornerY, brushWidth, isFilled, color);
        }

        @Override
        void reDraw(GraphicsContext gc) {
            gc.setFill(super.color);
            if (super.isFilled) {
                gc.setFill(super.color);
                gc.fillRect(super.cornerX, super.cornerY, super.wh, super.hg);
            } else {
                gc.setStroke(super.color);
                gc.strokeRect(super.cornerX, super.cornerY, super.wh, super.hg);
            }
        }
    }

    public static class oval extends stroke {
        public oval(double wh, double hg, double cornerX, double cornerY, double brushWidth, boolean isFilled, Paint color) {
            super(wh, hg, cornerX, cornerY, brushWidth, isFilled, color);
        }

        @Override
        void reDraw(GraphicsContext gc) {

            if (super.isFilled) {
                gc.setFill(super.color);
                gc.fillOval(super.cornerX, super.cornerY, super.wh, super.hg);
            } else {
                gc.setStroke(super.color);
                gc.strokeOval(super.cornerX, super.cornerY, super.wh, super.hg);
            }

        }
    }

    public static class brush extends stroke {

        public ArrayList<brush> brushes = new ArrayList<>(10000);

        public brush(double wh, double hg, double cornerX, double cornerY, double brushWidth, boolean isFilled, Paint color) {
            super(wh, hg, cornerX, cornerY, brushWidth, isFilled, color);
        }

        public brush() {
            super(0, 0, 0, 0, 0, false, null);
        }

        @Override
        void reDraw(GraphicsContext gc) {
            for (brush x : brushes) {
                gc.setLineWidth(x.getBrushWidth());
                gc.setStroke(x.getColor());
                gc.strokeLine(x.getWh(), x.getHg(), x.getCornerX(), x.getCornerY());
            }
        }
    }

    public static class eraser extends stroke {
        public ArrayList<eraser> erases = new ArrayList<>(10000);

        public eraser() {
            super(0, 0, 0, 0, 0, false, null);
        }

        public eraser(double wh, double hg, double cornerX, double cornerY, double brushWidth, boolean isFilled, Paint color) {
            super(wh, hg, cornerX, cornerY, brushWidth, isFilled, color);
        }

        @Override
        void reDraw(GraphicsContext gc) {
            for (eraser x : erases) {
                gc.clearRect(x.getWh(), x.getHg(), x.getCornerX(), x.getCornerY());
            }
        }
    }
}