/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package edu.brook.aa;

//Temporary for JDK 1.1 compatibility
//import com.sun.java.util.collections.*;

import edu.brook.aa.weka.HouseholdAggregateML;
import org.ascape.model.Scape;
import org.ascape.model.space.SpatialTemporalException;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.FillCellFeature;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.Overhead2DView;

import java.awt.*;


public class LHV extends Scape {

    /**
     * The size an aggregate (household not individual level) household is assumed to be.
     */
    public static final int typicalHouseholdSize = 5;
    /**
     * The amount of food assumed to be needed for one average adult.
     */
    public static final int baseNutritionNeed = 160;
    public static final int householdMinNutritionNeed = (int) Math.round(baseNutritionNeed * typicalHouseholdSize * 0.95);
    public static final int householdMaxNutritionNeed = (int) Math.round(baseNutritionNeed * typicalHouseholdSize * 1.05); //baseNutritionNeed * typicalHouseholdSize;
    public static final int minFertilityAge = 16;//16
    public static final int maxFertilityAge = 16;//16
    public static final int minDeathAge = 30;//30
    public static final int maxDeathAge = 30;//30
    //For Rob's experiment
    public static final int minFertilityEndsAge = 30;
    public static final int maxFertilityEndsAge = 30;
    public static final double maxFertility = 0.125;//0.125
    public static final double maizeGiftToChild = .33;
    public static final double waterSourceDistance = 16.0;
    public static final int yearsOfStock = 2;
    public static final int householdMinInitialAge = 0;
    public static final int householdMaxInitialAge = 29;
    public static final int householdMinInitialCorn = 2000;
    public static final int householdMaxInitialCorn = 2400;
    public static final double harvestAdjustment = 1.0;
    public static final double harvestVarianceYear = 0.1;
    public static final double harvestVarianceLocation = 0.1;
    private static final long serialVersionUID = -1892876266881188560L;
    public static double minFertility = 0.125;//0.125
    public static EnvironmentZone ENVIRON_EMPTY = new EnvironmentZone("Empty", Color.white);
    public static EnvironmentZone ENVIRON_GENERAL_VALLEY = new EnvironmentZone("General Valley Floor", Color.black);
    public static EnvironmentZone ENVIRON_NORTH_VALLEY = new EnvironmentZone("North Valley Floor", Color.red);
    public static EnvironmentZone ENVIRON_MID_VALLEY = new EnvironmentZone("Mid-Valley Floor", Color.green);
    public static EnvironmentZone ENVIRON_UPLANDS_NATURAL = new EnvironmentZone("Uplands Natural", Color.yellow);
    public static EnvironmentZone ENVIRON_UPLANDS_ARABLE = new EnvironmentZone("Uplands Arable", Color.blue);
    public static EnvironmentZone ENVIRON_KINBIKO_CANYON = new EnvironmentZone("Kinbiko Canyon", Color.pink);
    public static MaizeZone MAIZE_EMPTY = new MaizeZone("Empty", Color.white);
    public static MaizeZone MAIZE_NO_YIELD = new MaizeZone("No Yield", Color.white);
    public static MaizeZone MAIZE_YIELD_1 = new MaizeZone("Yield 1", Color.green);
    public static MaizeZone MAIZE_YIELD_2 = new MaizeZone("Yield 2", Color.blue);
    public static MaizeZone MAIZE_YIELD_3 = new MaizeZone("Yield 3", Color.gray);
    public static MaizeZone MAIZE_SAND_DUNE = new MaizeZone("Sand Dune", Color.yellow);
    protected static int EARLIEST_YEAR = 200;
    protected static int LATEST_YEAR = 1499;
    protected static int PDSI_EARLIEST_YEAR = 382;
    protected static int MAXIMUM_YEARS = LHV.LATEST_YEAR - LHV.EARLIEST_YEAR;
    protected LHVMachineLearning valleyRB, valleyML;
    protected HistoricSettlements historicSettlements;
    protected Scape environmentZones;
    protected Scape maizeZones;
    protected Overhead2DView view;

    public void createScape() {
        setAutoRestart(true);
        setPrototypeAgent(new Scape());
        getRules().clear();

        setEarliestPeriod(EARLIEST_YEAR);
        setLatestPeriod(LATEST_YEAR);
        setPeriodName("Year");

        /*
         * Create Environment Zones
         */
        environmentZones = new Scape();
        environmentZones.setName("Environment Zones");
        add(environmentZones);
        environmentZones.setPrototypeAgent(ENVIRON_EMPTY);
        environmentZones.add(ENVIRON_GENERAL_VALLEY);
        environmentZones.add(ENVIRON_NORTH_VALLEY);
        environmentZones.add(ENVIRON_MID_VALLEY);
        environmentZones.add(ENVIRON_UPLANDS_NATURAL);
        environmentZones.add(ENVIRON_UPLANDS_ARABLE);
        environmentZones.add(ENVIRON_KINBIKO_CANYON);
        environmentZones.add(ENVIRON_EMPTY);
        environmentZones.setAutoCreate(false);

        DataImporter.importEnvironmentalHistory(environmentZones);

        /*
         * Create Maize Zones
         */
        maizeZones = new Scape();
        maizeZones.setName("Maize Zones");
        //No need to add maize environmentZones to root, as no calculations needed
        maizeZones.setPrototypeAgent(MAIZE_EMPTY);
        maizeZones.add(MAIZE_YIELD_1);
        maizeZones.add(MAIZE_YIELD_2);
        maizeZones.add(MAIZE_YIELD_3);
        maizeZones.add(MAIZE_SAND_DUNE);
        maizeZones.add(MAIZE_NO_YIELD);
        maizeZones.add(MAIZE_EMPTY);
        maizeZones.setAutoCreate(false);

        /*
         * Create valleys
         */
        valleyRB = new LHVMachineLearning("RB", new HouseholdAggregate());
        valleyML = new LHVMachineLearning("ML", new HouseholdAggregateML());
        historicSettlements = new HistoricSettlements();

        add(valleyRB);
        add(valleyML);
        add(historicSettlements);

        setAutoRestart(false);
        try {
            setStartPeriod(800);
            setStopPeriod(1350);
        } catch (SpatialTemporalException e) {
            System.out.println("Bad start/stop periods: " + e);
        }


    }

    public void createViews() {
        super.createViews();

        //Create a new chart
        ChartView chart = new ChartView();
        //Add it to the agents view, just like any other view
//        valleyRB.addView(chart);
        historicSettlements.addView(chart);
        //And add some of the stat series we've just created to it
        chart.addSeries("Sum Historic Households", Color.red);
//        chart.addSeries("Count Households (RB)", Color.black);
//        chart.addSeries("Count Households (ML)", Color.blue);
    }


    public Scape getEnvironmentZones() {
        return environmentZones;
    }


    static class FillValleyCellFeature extends FillCellFeature {

        private static final long serialVersionUID = 3823585979723128174L;

        public FillValleyCellFeature(String name, ColorFeature colorFeature) {
            super(name, colorFeature);
        }

        public void draw(Graphics g, Object object, int width, int height) {
            if (((Location) object).getEnvironmentZone() != ENVIRON_EMPTY) {
                g.setColor(getColor(object));
                g.fillRect(0, 0, width, height);
            } else {
                g.setColor(Color.lightGray);
                g.fillRect(0, 0, width, height);
            }
        }
    }


}
