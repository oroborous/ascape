package edu.brook.aa;

import org.ascape.model.HistoryValueSetter;
import org.ascape.model.Scape;
import org.ascape.model.rule.SetValues;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Coordinate2DDiscrete;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import static edu.brook.aa.LHV.*;

public class DataImporter {
    static class GeneralValleyStreamSource extends WaterSource {

        private static final long serialVersionUID = -3153973451588490393L;

        public boolean isExtant() {
            return isStreamsExist(getScape().getPeriod());
        }
    }

    private static boolean isStreamsExist(int date) {
        return (((date >= 280) && (date < 360)) ||
                ((date >= 800) && (date < 930)) ||
                ((date >= 1300) && (date < 1450)));
    }

    private static boolean isAlluviumExists(int date) {
        return (((date >= 420) && (date < 560)) ||
                ((date >= 630) && (date < 680)) ||
                ((date >= 980) && (date < 1120)) ||
                ((date >= 1180) && (date < 1230)));
    }

    private static boolean isWaterSource(EnvironmentZone zone, int date) {
        if ((isAlluviumExists(date)) &&
                ((zone == ENVIRON_GENERAL_VALLEY) ||
                        (zone == ENVIRON_NORTH_VALLEY) ||
                        (zone == ENVIRON_MID_VALLEY) ||
                        (zone == ENVIRON_KINBIKO_CANYON))) {
            return true;
        }

        return (isStreamsExist(date)) && (zone == ENVIRON_KINBIKO_CANYON);
    }

    /**
     * Import map and water sources from binary data files.
     */
    public static void importMap(Scape valley) {
        try (InputStream fs = DataImporter.class.getResourceAsStream("MapData/map.bin")) {
            assert fs != null;
            try (DataInputStream ds = new DataInputStream(fs)) {
                for (int x = 0; x < 80; x++) {
                    for (int y = 0; y < 120; y++) {
                        Location location = (Location) ((Array2D) valley.getSpace()).get(x, y);
                        location.streamToState(ds);
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("IO exception while importing data " + ex);
        }

        Scape waterSources = new Scape();
        waterSources.setName("Water Sources");
        waterSources.setPrototypeAgent(new WaterSource());
        waterSources.setAutoCreate(false);
        valley.add(waterSources);
        waterSources.getRules().clear();

        try (InputStream fws = DataImporter.class.getResourceAsStream("MapData/water.bin")) {
            assert fws != null;
            try (DataInputStream dws = new DataInputStream(fws)) {
                for (int i = 0; i < 108; i++) {
                    //Not used
                    WaterSource importedSource = new WaterSource();
                    importedSource.streamToState(dws);
                    Coordinate2DDiscrete locCoordinate = Location.getCoordinateFromMeters(importedSource.getMetersNorth(), importedSource.getMetersEast());
                    if (((Array2D) valley.getSpace()).isValid(locCoordinate)) {
                        Location location = (Location) valley.get(locCoordinate);
                        location.setWaterSource(importedSource);
                        waterSources.add(importedSource);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("IO exception while importing data " + e);
        }

        int[][] streamLocations = {{72, 5}, {70, 6}, {69, 7}, {68, 8}, {67, 9}, {66, 10}, {65, 11}, {65, 12}};
        for (int[] streamLocation : streamLocations) {
            WaterSource streamSource = new GeneralValleyStreamSource();
            waterSources.add(streamSource);
            Location location = (Location) ((Array2D) valley.getSpace()).get(streamLocation[0], streamLocation[1]);
            location.setWaterSource(streamSource);
        }

    }

    public static void importEnvironmentalHistory(Scape environmentZones) {
        int zoneCount = 6;
        HistoryValueSetter[][] historyValues = new HistoryValueSetter[zoneCount][5];
        for (int zoneIndex = 0; zoneIndex < zoneCount; zoneIndex++) {
            SetValues valueSetter = new SetValues();
            historyValues[zoneIndex][0] = new HistoryValueSetter() {
                public String getName() {
                    return "Set Zone History Values";
                }

                public void setValue(Object object) {
                    ((EnvironmentZone) object).setPDSI(getValue(object));
                }
            };
            historyValues[zoneIndex][1] = new HistoryValueSetter() {
                public String getName() {
                    return "Hydrology";
                }

                public void setValue(Object object) {
                    ((EnvironmentZone) object).setHydrology(getValue(object));
                }
            };
            historyValues[zoneIndex][2] = new HistoryValueSetter() {
                public String getName() {
                    return "Soil Agredation";
                }

                public void setValue(Object object) {
                    ((EnvironmentZone) object).setAggredation(getValue(object));
                }
            };
            historyValues[zoneIndex][3] = new HistoryValueSetter() {
                public String getName() {
                    return "Adjusted PDSI";
                }

                public void setValue(Object object) {
                    ((EnvironmentZone) object).setAPDSI(getValue(object));
                }
            };
            historyValues[zoneIndex][4] = new HistoryValueSetter() {
                public String getName() {
                    return "Water Source";
                }

                public void setValue(Object object) {
                    ((EnvironmentZone) object).setIsWaterSource(getValue(object) == 1.0);
                }
            };
            valueSetter.addSetters(historyValues[zoneIndex]);
            ((Scape) environmentZones.get(zoneIndex)).addRule(valueSetter);
            ((Scape) environmentZones.get(zoneIndex)).addInitialRule(valueSetter);
        }

        for (HistoryValueSetter[] historyValue : historyValues) {
            for (int k = 0; k < historyValues[0].length; k++) {
                historyValue[k].setPeriodRange(LHV.EARLIEST_YEAR, LHV.LATEST_YEAR);
            }
        }

        try (DataInputStream envStream = new DataInputStream(DataImporter.class.getResourceAsStream("MapData/environment.bin"))) {
            for (int period = LHV.EARLIEST_YEAR; period <= LHV.LATEST_YEAR; period++) {
                for (int zoneIndex = 0; zoneIndex < 5; zoneIndex++) {
                    if (period >= LHV.PDSI_EARLIEST_YEAR) {
                        historyValues[zoneIndex][0].setValueFor(period, envStream.readFloat());
                    } else {
                        historyValues[zoneIndex][0].setValueFor(period, 0.0);
                    }
                    historyValues[zoneIndex][1].setValueFor(period, envStream.readFloat());
                    historyValues[zoneIndex][2].setValueFor(period, envStream.readFloat());
                    //Since water source data is expressed in years not iterations.
                    boolean isSource = isWaterSource((EnvironmentZone) environmentZones.get(zoneIndex), period);
                    historyValues[zoneIndex][4].setValueFor(period, isSource ? 1.0 : 0.0);
                }
                historyValues[5][0].setValueFor(period, historyValues[1][0].getValueFor(period));
                historyValues[5][1].setValueFor(period, historyValues[1][1].getValueFor(period));
                historyValues[5][2].setValueFor(period, historyValues[1][2].getValueFor(period));
                boolean isSource = isWaterSource((EnvironmentZone) environmentZones.get(5), period);
                historyValues[5][4].setValueFor(period, isSource ? 1.0 : 0.0);
            }
        } catch (IOException e) {
            throw new RuntimeException("IO exception while importing environment " + e);
        }

        try (DataInputStream apdsiStream = new DataInputStream(DataImporter.class.getResourceAsStream("MapData/adjustedPDSI.bin"))) {
            //Don't know why the original data was encoded this way!
            for (int zoneIndex = 0; zoneIndex < 4; zoneIndex++) {
                for (int period = LHV.EARLIEST_YEAR; period <= LHV.LATEST_YEAR; period++) {
                    historyValues[zoneIndex][3].setValueFor(period, apdsiStream.readFloat());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("IO exception while importing environment " + e);
        }

        for (int period = LHV.EARLIEST_YEAR; period <= LHV.LATEST_YEAR; period++) {
            historyValues[4][3].setValueFor(period, historyValues[3][3].getValueFor(period));
            historyValues[5][3].setValueFor(period, historyValues[1][3].getValueFor(period));
        }

    }

    /**
     * Import historical settlement data from binary data files.
     */
    public static void importSettlementHistory(Scape valley, Scape historicSettlements) {
        try (InputStream fss = DataImporter.class.getResourceAsStream("MapData/settlements.bin")) {
            assert fss != null;
            try (DataInputStream dss = new DataInputStream(fss)) {

                for (int i = 0; i < 488; i++) {
                    //Not used
                    HistoricSettlement settlement = new HistoricSettlement();
                    settlement.streamToState(dss);
                    Coordinate2DDiscrete locCoordinate = Location.getCoordinateFromMeters(settlement.getMetersNorth(), settlement.getMetersEast());
                    if (settlement.getType() == 1) {
                        if (((Array2D) valley.getSpace()).isValid(locCoordinate)) {
                            Location location = (Location) valley.get(locCoordinate);
                            location.addHistoricSettlement(settlement);
                            settlement.setCoordinates(locCoordinate);
                            historicSettlements.add(settlement);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("IO exception while importing data " + e);
        }
    }
}
