/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.TooManyListenersException;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.LocatedAgent;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2DBase;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.vis.DrawFeature;

/**
 * A scape view that draws a view of a 2-dimensional lattice with a relative origin.
 * 
 * @author Miles Parker
 * @version 1.1.2
 * @history 1.1.2 5/17/99 updated for superclass border changes
 * @since 1.1.1
 */
public class OverheadRelative2DView extends Overhead2DView {

    /**
     * The origin.
     */
    private Coordinate2DDiscrete origin = new Coordinate2DDiscrete(0, 0);

    /**
     * The x offset.
     */
    private int xOffset;

    /**
     * The y offset.
     */
    private int yOffset;

    /**
     * The x max.
     */
    private int xMax;

    /**
     * The y max.
     */
    private int yMax;

    /**
     * Constructs an overhead relative two-dimensional view.
     */
    public OverheadRelative2DView() {
        this("Overhead Relative 2D View");
    }

    /**
     * Constructs an overhead relative two-dimensional view.
     * 
     * @param name
     *        a user relevant name for this view
     */
    public OverheadRelative2DView(String name) {
        super();
        this.name = name;
        setOffset(new Coordinate2DDiscrete(0, 0));
        drawSelectedNeighbors = true;
    }

    /**
     * Gets the origin.
     * 
     * @return the origin
     */
    public Coordinate2DDiscrete getOrigin() {
        return origin;
    }

    /**
     * Sets the offset.
     * 
     * @param origin
     *        the new offset
     */
    public void setOffset(Coordinate2DDiscrete origin) {
        this.origin = origin;
    }

    /**
     * The cell selected.
     */
    private boolean[][] cellSelected;

    /**
     * The draw border.
     */
    boolean drawBorder = false;

    /**
     * The cells selected.
     */
    private int cellsSelected;

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentView#scapeStarted(org.ascape.model.event.ScapeEvent)
     */
    public void scapeSetup(ScapeEvent scapeEvent) {
        cellSelected = new boolean[((Array2DBase) getScape().getSpace()).getXSize()][((Array2DBase) getScape()
                .getSpace()).getYSize()];
        cellsSelected = 0;
        xOffset = 0;
        yOffset = 0;
        xMax = ((Array2DBase) getScape().getSpace()).getXSize();
        yMax = ((Array2DBase) getScape().getSpace()).getYSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.Overhead2DView#drawCellAtIfUpdate(int, int)
     */
    public void drawCellAtIfUpdate(int x, int y) {
        Cell cell = (Cell) ((Array2DBase) getScape().getSpace()).get((x + xOffset) % xMax, (y + yOffset) % yMax);
        if (cell.isUpdateNeeded(iterationsPerRedraw)) {
            for (int i = 0; i < drawFeatures.length; i++) {
                ((DrawFeature) drawFeatures[i]).draw(bufferedGraphics, cell, agentSize, agentSize);
            }
        }
        /*
         * if (borderSize > 0) { bufferedGraphics.setColor(Color.black); bufferedGraphics.drawLine(0, agentSize,
         * agentSize, agentSize); bufferedGraphics.drawLine(agentSize, 0, agentSize, agentSize); }
         */
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.Overhead2DView#drawCellAt(int, int)
     */
    public void drawCellAt(int x, int y) {
        Cell cell = (Cell) ((Array2DBase) getScape().getSpace()).get((x + xOffset) % xMax, (y + yOffset) % yMax);
        for (int i = 0; i < drawFeatures.length; i++) {
            ((DrawFeature) drawFeatures[i]).draw(bufferedGraphics, cell, agentSize, agentSize);
        }
        /*
         * if (borderSize > 0) { bufferedGraphics.setColor(Color.black); bufferedGraphics.drawLine(0, agentSize,
         * agentSize, agentSize); bufferedGraphics.drawLine(agentSize, 0, agentSize, agentSize); }
         */
    }

    /**
     * On notification of a scape update, draws the actual overhead view.
     */
    public void updateScapeGraphics() {
        if ((getAgentCustomizer() != null) && ((Cell) getAgentCustomizer().getAgent() != null)) {
            scape.requestUpdate();
        }
        xOffset = origin.getXValue();
        yOffset = origin.getYValue();
        super.updateScapeGraphics();
/*if ((scape != null) && (scape.isInitialized())) {
		    Coordinate2DDiscrete extent = ((Coordinate2DDiscrete) ((ScapeDiscrete) scape).getExtent());
		    Object[] drawFeatures = drawSelection.getSelection();
		    if (scape.isCellsRequestUpdates() && !updateAllRequested && !drawNetwork) {
    		    for (int x = 0; x < xMax; x++) {
    			    for (int y = 0; y < yMax; y++) {
				        Cell cell = ((Array2DBase) getScape().getSpace()).getCell((x + xOffset) % xMax, (y + yOffset) % yMax);
				        if (cell.isUpdateNeeded()) {
        			        for (int i = 0; i < drawFeatures.length; i++) {
				                ((DrawFeature) drawFeatures[i]).draw(bufferedGraphics, cell, agentSize, agentSize);
				            }
				        }
    			        bufferedGraphics.translate(0, agentSize);
    			    }
			        bufferedGraphics.translate(agentSize, (-extent.getYValue()) * agentSize);
    		    }
    		}
    		else {
    		    if (cellsSelected > 0) {
    		        updateAllRequested = true;
    }
    		    else {
    		        updateAllRequested = false;
    		    }
    		    for (int x = 0; x < xMax; x++) {
    			    for (int y = 0; y < yMax; y++) {
        			    for (int i = 0; i < drawFeatures.length; i++) {
				            ((DrawFeature) drawFeatures[i]).draw(bufferedGraphics, ((Array2DBase) getScape().getSpace()).getCell((x + xOffset) % xMax, (y + yOffset) % yMax), agentSize, agentSize);
				        }
    			        bufferedGraphics.translate(0, agentSize);
    			    }
			        bufferedGraphics.translate(agentSize, (-extent.getYValue()) * agentSize);
    		    }
		        bufferedGraphics.translate((-extent.getXValue()) * agentSize, 0);
    		    for (int x = 0; x < xMax; x++) {
    			    for (int y = 0; y < yMax; y++) {
                        if (cellSelected[(x + xOffset) % xMax][(y + yOffset) % yMax]) {
            			    Cell[] network = ((Array2DBase) getScape().getSpace()).getCell((x + xOffset) % xMax, (y + yOffset) % yMax).getNetwork();
            			    bufferedGraphics.setColor(Color.black);
            			    //bufferedGraphics.drawString(Integer.toString(((SugarAgent) occupant).getVision()), x * agentSize, y * agentSize);
            			    for (int i= 0; i < network.length; i++) {
            			        Coordinate2DDiscrete c = ((Coordinate2DDiscrete) network[i].getCoordinate());
            			        if (c != null) {
                			        int x2 = (c.getXValue() - xOffset + xMax) % xMax;
                			        int y2 = (c.getYValue() - yOffset + yMax) % yMax;
                			        //bufferedGraphics.drawLine(((x + xOffset) % xMax) * agentSize + agentSize / 2 - 1, ((y + yOffset) % yMax) * agentSize + agentSize / 2 - 1, x2 * agentSize + agentSize / 2 - 1, y2 * agentSize + agentSize / 2 - 1);
                			        bufferedGraphics.drawLine(x * agentSize + agentSize / 2 - 1, y * agentSize + agentSize / 2 - 1, x2 * agentSize + agentSize / 2 - 1, y2 * agentSize + agentSize / 2 - 1);
                			    }
            			    }
            			}
    			    }
    		    }
    		    updateAllRequested = false;
    		}
			if (drawNetwork) {
    			for (int x = 0; x < extent.getXValue(); x++) {
    				for (int y = 0; y < extent.getYValue(); y++) {
				        Cell cell = ((Array2DBase) getScape().getSpace()).getCell((x + xOffset) % xMax, (y + yOffset) % yMax);
            			Cell[] network = cell.getNetwork();
            			bufferedGraphics.setColor(Color.black);
            			//bufferedGraphics.drawString(Integer.toString(((SugarAgent) occupant).getVision()), x * agentSize, y * agentSize);
            			for (int i= 0; i < network.length; i++) {
            			    Coordinate2DDiscrete c = ((Coordinate2DDiscrete) network[i].getCoordinate());
            			    if (network[i].getCoordinate() != null) {
                			    int x2 = (c.getXValue()+ xOffset) % xMax;
                			    int y2 = (c.getYValue() + yOffset) % yMax;
                			    bufferedGraphics.drawLine(((x + xOffset) % xMax) * agentSize + agentSize / 2 - 1, ((y + yOffset) % yMax) * agentSize + agentSize / 2 - 1, x2 * agentSize + agentSize / 2 - 1, y2 * agentSize + agentSize / 2 - 1);
                			}
            			}
    				}
    			}
    		}
		}*/
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.Overhead2DView#getAgentAtPixel(int, int)
     */
    public Agent getAgentAtPixel(int x, int y) {
        int td = agentSize + borderSize;
        x = (((x / td) + origin.getXValue()) % ((Array2DBase) getScape().getSpace()).getXSize());
        y = (((y / td) + origin.getYValue()) % ((Array2DBase) getScape().getSpace()).getYSize());
        return (Cell) ((Array2DBase) getScape().getSpace()).get(x, y);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.Overhead2DView#drawSelectedAgent(java.awt.Graphics, org.ascape.model.LocatedAgent)
     */
    public void drawSelectedAgent(Graphics g, LocatedAgent a) {
        Coordinate2DDiscrete coor = (Coordinate2DDiscrete) a.getCoordinate();
        int x = coor.getXValue() - xOffset;
        if (x < 0) {
            x += ((Array2DBase) getScape().getSpace()).getXSize();
        }
        int y = coor.getYValue() - yOffset;
        if (y < 0) {
            y += ((Array2DBase) getScape().getSpace()).getYSize();
        }
        int td = agentSize + borderSize;
        g.translate(x * td, y * td);
        drawSelectedAgentAt(g, a);
        if (drawSelectedNeighbors) {
            drawNeighborsFor(g, a);
        }
        g.translate(-x * td, -y * td);
        if (scape.isCellsRequestUpdates()) {
            // check x bounds;
            int xmin = (x - 1 < 0 ? x = 0 : x - 1);
            int xmax = (x + 1 < ((Array2DBase) getScape().getSpace()).getXSize() ? x + 1 : x);
            // check y bounds;
            int ymin = (y - 1 < 0 ? y = 0 : y - 1);
            int ymax = (y + 1 < ((Array2DBase) getScape().getSpace()).getXSize() ? y + 1 : y);
            for (int i = xmin; i <= xmax; i++) {
                for (int j = ymin; j <= ymax; j++) {
                    ((Cell) ((Array2DBase) getScape().getSpace()).get(i, j)).requestUpdateNext();
                }
            }
        }
    }

    /**
     * Override addNotify to build buffer.
     */
    public void addNotify() {
        super.addNotify();
        /*
         * if (drawBorder) { bufferedGraphics.setColor(Color.black); int xMax = ((Array2DBase)
         * getScape().getSpace()).getXSize(); int yMax = ((Array2DBase) getScape().getSpace()).getYSize(); for (int x =
         * 0; x < xMax; x++) { bufferedGraphics.drawLine(x agentSize, 0, x agentSize, (yMax + 1) agentSize - 1); } for
         * (int y = 0; y < yMax; y++) { bufferedGraphics.drawLine(0, y agentSize, (xMax + 1) agentSize - 1, y
         * agentSize); } }
         */
        // this.addMouseListener(new MouseListener() {
        // public void mouseClicked(MouseEvent e) {
        // if (e.getClickCount() == 1) {
        // int x = (((e.getX() / agentSize) + origin.getXValue()) % ((Array2DBase) getScape().getSpace()).getXSize());
        // int y = (((e.getY() / agentSize) + origin.getYValue()) % ((Array2DBase) getScape().getSpace()).getYSize());
        // if (cellSelected[x][y]) {
        // cellSelected[x][y] = false;
        // cellsSelected--;
        // } else {
        // cellSelected[x][y] = true;
        // cellsSelected++;
        // }
        // //Cell cell = ((Array2DBase) getScape().getSpace()).getCell(x, y);
        // //System.out.println(y);
        // }
        // }
        //
        // public void mouseEntered(MouseEvent e) {
        // }
        //
        // public void mouseExited(MouseEvent e) {
        // }
        //
        // public void mousePressed(MouseEvent e) {
        // }
        //
        // public void mouseReleased(MouseEvent e) {
        // }
        // });
    }

    /**
     * Notifies the view that the scape has added it. Sets up current max and min.
     * 
     * @param scapeEvent
     *        the scape added notification event
     * @throws TooManyListenersException
     *         the too many listeners exception
     * @exception TooManyListenersException
     *            on attempt to add this listener to another scape when one has already been assigned
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        // Coordinate2DDiscrete extent = (Coordinate2DDiscrete) ((Scape) scapeEvent.getSource()).getCoordinate();
        // cellSelected = new boolean[extent.getXValue()][extent.getYValue()];
    }

    /**
     * Returns a description of this view.
     * 
     * @return the string
     */
    public String toString() {
        return "Two-dimensional lattice view";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.HostedAgentView#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(new Integer(xOffset));
        out.writeObject(new Integer(xMax));
        out.writeObject(new Integer(yOffset));
        out.writeObject(new Integer(yMax));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.HostedAgentView#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        xOffset = ((Integer) in.readObject()).intValue();
        xMax = ((Integer) in.readObject()).intValue();
        yOffset = ((Integer) in.readObject()).intValue();
        yMax = ((Integer) in.readObject()).intValue();
    }
}
