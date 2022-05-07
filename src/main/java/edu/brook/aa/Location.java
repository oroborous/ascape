/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package edu.brook.aa;

import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.Conditional;

import java.io.DataInputStream;
import java.io.IOException;


public class Location extends Cell implements Comparable<Location> {

    public final static Conditional HAS_WATER = new Conditional() {

        private static final long serialVersionUID = -7865447439916098804L;

        public boolean meetsCondition(Object o) {
            return (((Location) o).isOnMap() && ((Location) o).isCurrentWaterSource());
        }
    };
    public final static Conditional LOW_EROSION = new Conditional() {

        private static final long serialVersionUID = 7358607915800653400L;

        public boolean meetsCondition(Object o) {
            return (((Location) o).isOnMap() && (((Location) o).getEnvironmentZone().getHydrology() <= 0.0));
        }
    };
    public final static Conditional KAYENTA_1_SETTLEMENT = new Conditional() {

        private static final long serialVersionUID = -3431191777565656996L;

        public boolean meetsCondition(Object o) {
            return ((((Location) o).isOnMap()) && (((Location) o).getFarm() == null) && (((Location) o).hasWithin(LOW_EROSION, true, 1.0)));
        }
    };
    public final static int[] frostModerate = {909, 934, 1057, 1076, 1109, 1329};
    public final static int[] frostSevere = {896};
    private static final long serialVersionUID = -748678910819856437L;
    private static int nextId = 1;
    public final double frostModerateFactor = .5;
    public final double frostSevereFactor = .0;
    int id;
    private double quality;
    private EnvironmentZone environmentZone;
    private YieldZone yieldZone;
    //Same as yieldZone != ENVIRON_EMPTY, but used for performance and clarity
    private boolean onMap = true;
    private boolean sandDune = false;
    private Clan clan;
    private Farm farm;
    private Settlement settlement;
    private WaterSource waterSource;
    private HistoricSettlement[] historicSettlements = new HistoricSettlement[0];

    public static Coordinate2DDiscrete getCoordinateFromMeters(int metersNorth, int metersEast) {
        int site2115east = 2392;
        int site2115north = 7954;
        float site2115x = 24.5f, site2115y = 37.6f;
        int locationX = (int) (site2115x + (float) (metersEast - site2115east) / 93.5f);
        int locationY = (int) (site2115y - (float) (metersNorth - site2115north) / 93.5f);
        return new Coordinate2DDiscrete(locationX, locationY);
    }

    // TODO Law of Demeter correct
    public void addHistoricSettlement(HistoricSettlement historicSettlement) {
        historicSettlement.setLocation(this);
        HistoricSettlement[] temp = new HistoricSettlement[historicSettlements.length + 1];
        for (int i = 0; i < historicSettlements.length; i++) {
            temp[i] = historicSettlements[i];
        }
        historicSettlements = temp;
        historicSettlements[historicSettlements.length - 1] = historicSettlement;
    }


    @Override
    public int compareTo(Location o) {
        if (this == o)
            return 0;
        return Double.compare(this.quality, o.quality);
    }

    public void createSettlement() {
        settlement = new Settlement();
        getLHVRoot().addSettlement(settlement);
        settlement.occupy(this);
        makeUnavailable();
        requestUpdate();
    }

    public double findRandomYield() {
        double yield = getBaseYield() * (Math.abs(getRandom().nextGaussian() * LHV.harvestVarianceLocation) + 1.0);
        return yield;
    }

    public final double getBaseYield() {
        if (yieldZone == null)
            return 0;
        return yieldZone.getYield() * quality * LHV.harvestAdjustment;
    }

    public Clan getClan() {
        return clan;
    }

    public EnvironmentZone getEnvironmentZone() {
        return environmentZone;
    }

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        if (this.farm != farm) {
            if ((farm == null) && (settlement == null)) {
                makeAvailable();
            } else if (this.farm == null) {
                makeUnavailable();
            }
            this.farm = farm;
            // TODO Law of Demeter violation
            if ((farm != null)
                    && (farm.getHousehold() != null)
                    && (farm.getHousehold().getClan() != null)) {
                this.clan = farm.getHousehold().getClan();
            }
        }
        requestUpdate();
    }

    public final double getFrostYieldFactor() {
        int year = scape.getPeriod();
        for (int i = 0; i < frostModerate.length; i++) {
            if (year == frostModerate[i]) {
                return frostModerateFactor;
            }
        }
        for (int i = 0; i < frostSevere.length; i++) {
            if (year == frostSevere[i]) {
                return frostSevereFactor;
            }
        }
        return 1.0;
    }

    // TODO Law of Demeter correct
    public int getHistoricSettlementHouseholdCount() {
        int count = 0;
        for (int i = 0; i < historicSettlements.length; i++) {
            count += historicSettlements[i].getHouseholdCount();
        }
        return count;
    }

    public HistoricSettlement[] getHistoricSettlements() {
        return historicSettlements;
    }

    private LHVMachineLearning getLHVRoot() {
        Scape parent = getScape();
        while (!(parent instanceof LHVMachineLearning)) {
            parent = parent.getScape();
        }
        return (LHVMachineLearning) parent;
    }

    public MaizeZone getMaizeZone() {
        return yieldZone.getMaizeZone();
    }

    public double getQuality() {
        return quality;
    }

    public Settlement getSettlement() {
        return settlement;
    }

    public void setSettlement(Settlement settlement) {
        if (this.settlement != settlement) {
            if ((settlement == null) && (farm == null)) {
                makeAvailable();
            } else if (this.settlement == null) {
                makeUnavailable();
            }
            this.settlement = settlement;
            if (settlement != null) {
                this.clan = ((HouseholdBase) settlement.get(0)).getClan();
            }
            requestUpdate();
        }
    }

    public YieldZone getYieldZone() {
        return yieldZone;
    }

    public void setYieldZone(YieldZones yieldZones) {
        if ((environmentZone == LHV.ENVIRON_NORTH_VALLEY) && (sandDune)) {
            yieldZone = yieldZones.YIELD_NORTH_SAND_DUNE;
        } else if (environmentZone == LHV.ENVIRON_NORTH_VALLEY) {
            yieldZone = yieldZones.YIELD_NORTH_VALLEY;
        } else if (environmentZone == LHV.ENVIRON_KINBIKO_CANYON) {
            yieldZone = yieldZones.YIELD_KINBIKO_CANYON;
        } else if ((environmentZone == LHV.ENVIRON_MID_VALLEY) && (sandDune)) {
            yieldZone = yieldZones.YIELD_MID_SAND_DUNE;
        } else if ((environmentZone == LHV.ENVIRON_MID_VALLEY) && (((Coordinate2DDiscrete) getCoordinate()).getXValue() <= 34)) {
            yieldZone = yieldZones.YIELD_MID_VALLEY_WEST;
        } else if ((environmentZone == LHV.ENVIRON_MID_VALLEY) && (((Coordinate2DDiscrete) getCoordinate()).getXValue() > 34)) {
            yieldZone = yieldZones.YIELD_MID_VALLEY_EAST;
        } else if (environmentZone == LHV.ENVIRON_GENERAL_VALLEY) {
            yieldZone = yieldZones.YIELD_GENERAL_VALLEY;
        } else if (environmentZone == LHV.ENVIRON_UPLANDS_ARABLE) {
            yieldZone = yieldZones.YIELD_UPLANDS_ARABLE;
        } else if (environmentZone == LHV.ENVIRON_UPLANDS_NATURAL) {
            yieldZone = yieldZones.YIELD_UPLANDS_NATURAL;
        } else if (environmentZone == LHV.ENVIRON_EMPTY) {
            yieldZone = yieldZones.YIELD_EMPTY;
        } else {
            throw new RuntimeException("Bad data or logic in Location#CalculateMaizeZone");
        }
    }

    public void initialize() {
        super.initialize();
        quality = getRandom().nextGaussian() * LHV.harvestVarianceLocation + 1.0;
        quality = Math.max(quality, 0.0);
        //We'll sort them all at once...
        if (yieldZone != null)
            // TODO Law of Demeter violation
            yieldZone.getAvailableLocations().add(this);
        settlement = null;
        farm = null;
        clan = null;
    }

    public boolean isAvailable() {
        return ((farm == null) && (settlement == null));
    }

    public boolean isCurrentHistoricSettlement() {
        for (int i = 0; i < historicSettlements.length; i++) {
            if (historicSettlements[i].isExtant()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCurrentWaterSource() {
        return (environmentZone.isWaterSource() ||
                ((waterSource != null) &&
                        waterSource.isExtant()));
    }

    public boolean isOnMap() {
        return onMap;
    }

    public boolean isSandDune() {
        return sandDune;
    }

    public boolean isUpdateNeeded(int within) {
        //if ((super.isUpdateNeeded()) || (historicSettlements.length > 0) || (waterSource != null)) {
        if ((super.isUpdateNeeded(within)) ||
                (historicSettlements.length > 0) ||
                (environmentZone.isUpdateNeeded(within)) ||
                (waterSource != null)) {
            return true;
        }
        return false;
    }

    public void makeAvailable() {
        getYieldZone().getAvailableLocations().add(this);
    }

    public void makeUnavailable() {
        getYieldZone().getAvailableLocations().remove(this);
    }

    public void setWaterSource(WaterSource waterSource) {
        this.waterSource = waterSource;
    }

    public void streamToState(DataInputStream s) throws IOException {
        id = nextId++;

        int data = s.readShort();
        switch ((data / 10)) {
            case 0:
                environmentZone = LHV.ENVIRON_GENERAL_VALLEY;
                break;
            case 1:
                environmentZone = LHV.ENVIRON_NORTH_VALLEY;
                break;
            case 2:
                environmentZone = LHV.ENVIRON_MID_VALLEY;
                break;
            case 3:
                environmentZone = LHV.ENVIRON_UPLANDS_NATURAL;
                break;
            case 4:
                environmentZone = LHV.ENVIRON_UPLANDS_ARABLE;
                break;
            case 5:
                environmentZone = LHV.ENVIRON_KINBIKO_CANYON;
                break;
            case 6:
                environmentZone = LHV.ENVIRON_EMPTY;
                //Default is true...
                onMap = false;
                break;
            default:
                throw new RuntimeException("Bad data in map import.");
        }
        sandDune = ((data % 10) >= 5);
    }

    public String toString() {
        return String.format("ID: %d, Base Yield: %f, Quality: %f, Farm: %s",
                id, getBaseYield(), getQuality(),
                farm != null ? Integer.toString(farm.id) : "none");
    }
}
