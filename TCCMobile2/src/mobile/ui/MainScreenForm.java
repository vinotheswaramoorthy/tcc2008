package mobile.ui;

import com.sun.lwuit.Component;
import com.sun.lwuit.Form;
import com.sun.lwuit.Graphics;
import com.sun.lwuit.Image;
import com.sun.lwuit.Painter;
import com.sun.lwuit.RGBImage;
import com.sun.lwuit.animations.Animation;
import com.sun.lwuit.animations.Motion;
import com.sun.lwuit.animations.Transition;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.geom.Rectangle;
import com.sun.lwuit.util.Resources;
import java.io.IOException;
import java.util.Vector;
import mobile.midlet.*;

public class MainScreenForm extends Form {

    private Component dragged;
    private int oldx;
    private int oldy;
    private int draggedx;
    private int draggedy;
    private Image draggedImage;
    private Vector cmps;
    private Transition cmpTransition;

    public MainScreenForm(String title) {
        setTitle(title);
    }

    public void pointerDragged(int x, int y) {

        if (draggedImage == null) {
            dragged = getComponentAt(x, y);
            if (dragged == null || !getContentPane().contains(dragged)) {
                super.pointerDragged(x, y);
                return;
            }
            draggedImage = Image.createImage(dragged.getWidth(), dragged.getHeight());
            Graphics g = draggedImage.getGraphics();
            g.setClip(0, 0, dragged.getWidth(), dragged.getHeight());
            //choose a rare color
            g.setColor(0xff7777);
            g.fillRect(0, 0, dragged.getWidth(), dragged.getHeight());
            g.translate(-dragged.getX(), -dragged.getY());
            dragged.paint(g);
            g.translate(dragged.getX(), dragged.getY());

            //remove all occurences of the rare color
            draggedImage = draggedImage.modifyAlpha((byte)0x55, 0xff7777);
            /*int[] bufferArray = draggedImage.getRGB();
            for (int iter = 0; iter < bufferArray.length; iter++) {
                bufferArray[iter] = ((bufferArray[iter] & 0xFFFFFF) | 0x55000000);
            }
            draggedImage = new RGBImage(bufferArray, draggedImage.getWidth(), draggedImage.getHeight());
            */
            oldx = x;
            oldy = y;
            draggedx = dragged.getAbsoluteX();
            draggedy = dragged.getAbsoluteY();
            dragged.setVisible(false);
            Painter glassPane = new Painter() {

                public void paint(Graphics g, Rectangle rect) {

                    if (draggedImage != null) {
                        g.drawImage(draggedImage, draggedx, draggedy);
                    }

                }
            };
            setGlassPane(glassPane);
            cmpTransition = MainMID.getComponentTransition();
            MainMID.setComponentTransition(null);
            return;
        }


        repaint(draggedx, draggedy, dragged.getWidth(), dragged.getHeight());

        draggedx = draggedx + (x - oldx);
        draggedy = draggedy + (y - oldy);
        oldx = x;
        oldy = y;
        repaint(draggedx, draggedy, dragged.getWidth(), dragged.getHeight());
        super.pointerDragged(x, y);
    }

    public void pointerReleased(int x, int y) {
        if (dragged == null) {
            super.pointerReleased(x, y);
            return;
        }
        setVisible(false);
        oldx = 0;
        oldy = 0;

        Component cmp = getFocused();//getComponentAt(x, y);

        final int index = getContentPane().getComponentIndex(cmp);
        cmps = new Vector();
        if (index >= 0) {// && getContentPane().contains(x, y)) {

            int draggedIndex = getContentPane().getComponentIndex(dragged);
            int startIndex = Math.min(index, draggedIndex);
            for (int i = startIndex; i < getContentPane().getComponentCount(); i++) {
                Component toMove = getContentPane().getComponentAt(i);
                LayoutAnimation la = new LayoutAnimation(toMove);
                la.setFrom(new Dimension(toMove.getX(), toMove.getY()));
                cmps.addElement(la);
            }

            removeComponent(dragged);
            addComponent(index, dragged);

            layoutContainer();

            LayoutAnimation la = new LayoutAnimation(dragged);
            la.setFrom(new Dimension(draggedx - (dragged.getAbsoluteX() - dragged.getX()),
                    draggedy - (dragged.getAbsoluteY() - dragged.getY())));
            la.setTo(new Dimension(dragged.getX(), dragged.getY()));
            la.init();

            for (int i = 0; i < cmps.size(); i++) {
                LayoutAnimation l = (LayoutAnimation) cmps.elementAt(i);
                l.setTo(new Dimension(l.toAnimate.getX(), l.toAnimate.getY()));
                l.init();
            }
            cmps.addElement(la);

            removeComponent(dragged);
            addComponent(draggedIndex, dragged);
            layoutContainer();
            la.init();
        } else {
            finishDrag();
            return;
//                LayoutAnimation la = new LayoutAnimation(dragged);
//                la.setFrom(new Dimension(draggedx - (dragged.getAbsoluteX() - dragged.getX()),
//                        draggedy - (dragged.getAbsoluteY() - dragged.getY())));
//                la.setTo(new Dimension(dragged.getX(), dragged.getY()));
//                la.init();
        //cmps.addElement(la);
        }

        registerAnimated(new Animation() {

            public boolean animate() {
                //
                boolean retVal = false;
                for (int i = 0; i < cmps.size(); i++) {
                    LayoutAnimation la = (LayoutAnimation) cmps.elementAt(i);
                    if (la.animate()) {
                        retVal = true;
                    }
                }
                //if finished
                if (!retVal) {
                    deregisterAnimated(this);
                    if(getContentPane().contains(dragged)){
                        removeComponent(dragged);
                        addComponent(index, dragged);
                    }
                    finishDrag();
                }

                return retVal;
            }

            public void paint(Graphics g) {
                for (int i = 0; i < cmps.size(); i++) {
                    LayoutAnimation la = (LayoutAnimation) cmps.elementAt(i);
                    la.paint(g);
                    repaint(la.toAnimate.getAbsoluteX(), la.toAnimate.getAbsoluteY(),
                            la.toAnimate.getWidth(), la.toAnimate.getHeight());
                }
                if (!dragged.isVisible()) {
                    dragged.setVisible(true);
                    dragged.requestFocus();
                }
            }
        });


        setVisible(true);
        repaint();
        setGlassPane(null);
    }

    private void finishDrag() {
        setGlassPane(null);
        if (dragged != null) {
            dragged.requestFocus();
            if (!dragged.isVisible()) {
                dragged.setVisible(true);
                dragged.requestFocus();
            }
            dragged = null;
        }
        draggedImage = null;
        MainMID.setComponentTransition(cmpTransition);
        repaint();
    }

    class LayoutAnimation implements Animation {

        private Component toAnimate;
        private Dimension from;
        private Dimension to;
        private Motion xMotion;
        private Motion yMotion;

        LayoutAnimation(Component toAnimate) {
            this.toAnimate = toAnimate;
        }

        public void setFrom(Dimension from) {
            this.from = from;
        }

        public void setTo(Dimension to) {
            this.to = to;
        }

        public void init() {
            toAnimate.setX(from.getWidth());
            toAnimate.setY(from.getHeight());
            xMotion = Motion.createSplineMotion(from.getWidth(), to.getWidth(), 500);
            yMotion = Motion.createSplineMotion(from.getHeight(), to.getHeight(), 500);
            xMotion.start();
            yMotion.start();
        }

        public boolean animate() {
            boolean retVal = !xMotion.isFinished() || !yMotion.isFinished();
            toAnimate.setX(xMotion.getValue());
            toAnimate.setY(yMotion.getValue());
            return !xMotion.isFinished() && !yMotion.isFinished();
        }

        public void paint(Graphics g) {

            toAnimate.paintComponent(g);
        }
    }
}



