/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package edu.brook.aa;

import org.ascape.model.Cell;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCondCSA;
import org.ascape.util.data.StatCollectorCondCSAMM;
import org.ascape.util.vis.Drawable;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;


public class HistoricSettlement extends Cell implements Drawable {


    private static final long serialVersionUID = -6290387961249914907L;

    private int SARGnumber;

    private int metersNorth;

    private int metersEast;

    private int startDate;

    private int endDate;

    private int medianDate;

    private int type;

    @SuppressWarnings("unused")
    private int size;

    @SuppressWarnings("unused")
    private int description;

    @SuppressWarnings("unused")
    private int roomCount;

    @SuppressWarnings("unused")
    private int elevation;

    private int baselineHouseholds;

    private Location location;
    private Coordinate2DDiscrete coordinates;

    public HistoricSettlement() {
    }

    public void draw(Graphics g, int width, int height) {
        g.setColor(Color.black);
        g.drawOval(0, 0, width - 1, height - 1);
        //g.setColor(settlementColorFeature.getColor(this));
        //g.fillOval(1, 1, width - 2, height - 2);
    }

    public int getBaselineHouseholds() {
        return baselineHouseholds;
    }

    public int getEndYear() {
        return endDate;
    }

    public int getHouseholdCount() {
        int period = getScape().getPeriod();
        if (period > medianDate) {
            return (int) Math.max(Math.ceil((baselineHouseholds * (float) (endDate - period)) / (float) (endDate - medianDate)), 1);
        } else if (period < medianDate) {
            return (int) Math.max(Math.ceil((baselineHouseholds * (float) (period - startDate)) / (float) (medianDate - startDate)), 1);
        } else {
            return baselineHouseholds;
        }
    }

    public int getMedianYear() {
        return medianDate;
    }

    public int getMetersEast() {
        return metersEast;
    }

    public int getMetersNorth() {
        return metersNorth;
    }

    public int getSize() {
        int period = getScape().getPeriod();
        if (period > medianDate) {
            return (int) Math.max(Math.ceil((baselineHouseholds * (float) (endDate - period)) / (float) (endDate - medianDate)), 1);
        } else if (period < medianDate) {
            return (int) Math.max(Math.ceil((baselineHouseholds * (float) (period - startDate)) / (float) (medianDate - startDate)), 1);
        } else {
            return baselineHouseholds;
        }
    }

    public int getStartYear() {
        return startDate;
    }

    public int getType() {
        return type;
    }

    public boolean isExtant() {
        //if ((startDate <= getScape().getPeriod()) && (endDate > getScape().getPeriod()))
        //System.out.println(getScape().getPeriod());
        return ((startDate <= getScape().getPeriod()) && (endDate > getScape().getPeriod()));
    }

    public void scapeCreated() {
        StatCollector[] stats = new StatCollector[2];
        stats[0] = new StatCollectorCondCSA("Historic Households") {

            private static final long serialVersionUID = -890759850053222387L;

            public double getValue(Object object) {
                return ((HistoricSettlement) object).getHouseholdCount();
            }

            public boolean meetsCondition(Object object) {
                return ((HistoricSettlement) object).isExtant();
            }
        };
        stats[1] = new StatCollectorCondCSAMM("Historic Household Size") {

            private static final long serialVersionUID = 1466828234754387324L;

            public final double getValue(Object object) {
                return 5 * ((HistoricSettlement) object).getHouseholdCount();
            }

            public boolean meetsCondition(Object object) {
                return ((HistoricSettlement) object).isExtant();
            }
        };

        getScape().addStatCollectors(stats);
    }

    public void setCoordinates(Coordinate2DDiscrete locCoordinate) {
        this.coordinates = locCoordinate;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void streamToState(DataInputStream s) throws IOException {
        SARGnumber = s.readShort();
        metersNorth = s.readShort();
        metersEast = s.readShort();
        startDate = s.readShort();
        endDate = s.readShort();
        medianDate = 1950 - s.readShort();
        //System.out.println(startDate + " - " + medianDate + " - " + endDate);
        type = s.readShort();
        size = s.readShort();
        description = s.readShort();
        roomCount = s.readShort();
        elevation = s.readShort();
        baselineHouseholds = s.readShort();
        //Per C++ code, correct typo in SARG data set
        if (SARGnumber == 2307) {
            medianDate = 1075;
        }
    }

    public String toString() {
        return String.format("Period: %d, SARG Number: %d, Household Count: %d, Size: %d, Coordinates: (%d,%d), Location [%s]",
                getScape().getPeriod(), SARGnumber,
                getHouseholdCount(), getSize(),
                coordinates.getXValue(), coordinates.getYValue(),
                this.location.toString());
    }

//    public String toString() {
//        return "Historic Settlement " + coordinate;
//    }
}
