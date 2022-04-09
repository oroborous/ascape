package edu.brook.aa;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.util.vis.DrawFeature;
import org.ascape.util.vis.DrawSymbol;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.Overhead2DView;

import java.awt.*;
import java.util.Collections;

import static edu.brook.aa.LHV.*;


public class LHVMachineLearning extends Scape {
    private YieldZone YIELD_EMPTY = new YieldZone("Empty", Color.white, ENVIRON_EMPTY, MAIZE_EMPTY);

    private YieldZone YIELD_GENERAL_VALLEY = new YieldZone("General Valley Floor", Color.black, ENVIRON_GENERAL_VALLEY, MAIZE_YIELD_2);

    private YieldZone YIELD_NORTH_SAND_DUNE = new YieldZone("North Valley Dunes", Color.white, ENVIRON_NORTH_VALLEY, MAIZE_SAND_DUNE);

    private YieldZone YIELD_NORTH_VALLEY = new YieldZone("North Valley Floor", Color.red, ENVIRON_NORTH_VALLEY, MAIZE_YIELD_1);

    private YieldZone YIELD_MID_SAND_DUNE = new YieldZone("Mid Valley Dunes", Color.white, ENVIRON_MID_VALLEY, MAIZE_SAND_DUNE);

    private YieldZone YIELD_MID_VALLEY_WEST = new YieldZone("West Mid-Valley Floor", Color.gray, ENVIRON_MID_VALLEY, MAIZE_YIELD_1);

    private YieldZone YIELD_MID_VALLEY_EAST = new YieldZone("East Mid-Valley Floor", Color.green, ENVIRON_MID_VALLEY, MAIZE_YIELD_2);

    private YieldZone YIELD_UPLANDS_NATURAL = new YieldZone("Uplands Natural", Color.yellow, ENVIRON_UPLANDS_NATURAL, MAIZE_NO_YIELD);

    private YieldZone YIELD_UPLANDS_ARABLE = new YieldZone("Uplands Arable", Color.blue, ENVIRON_UPLANDS_ARABLE, MAIZE_YIELD_3);

    private YieldZone YIELD_KINBIKO_CANYON = new YieldZone("Kinbiko Canyon", Color.pink, ENVIRON_KINBIKO_CANYON, MAIZE_YIELD_1);


    private Scape households;

    private Scape settlements;

    private Scape farms;

    private Scape yieldZones;

    private String name;

    public LHVMachineLearning(String name) {
        super(new Array2DMoore());
        this.name = name;
        setName("Long House Valley");
        setPrototypeAgent(new Location());
        setExtent(new Coordinate2DDiscrete(80, 120));
        setAutoCreate(false);
        getRules().clear();
    }

    public void createScape() {
        /*
         * Create Yield Zones
         */
        yieldZones = new Scape();
        yieldZones.setName("Yield Zones");
        add(yieldZones);
        yieldZones.setPrototypeAgent(YIELD_EMPTY);
        yieldZones.add(YIELD_EMPTY);
        yieldZones.add(YIELD_GENERAL_VALLEY);
        yieldZones.add(YIELD_NORTH_SAND_DUNE);
        yieldZones.add(YIELD_NORTH_VALLEY);
        yieldZones.add(YIELD_MID_SAND_DUNE);
        yieldZones.add(YIELD_MID_VALLEY_WEST);
        yieldZones.add(YIELD_MID_VALLEY_EAST);
        yieldZones.add(YIELD_UPLANDS_NATURAL);
        yieldZones.add(YIELD_UPLANDS_ARABLE);
        yieldZones.add(YIELD_KINBIKO_CANYON);
        yieldZones.setAutoCreate(false);
        //We sort all at once to avoid sorting penalties per addition
        yieldZones.addInitialRule(new Rule("Sort Available Locations") {
            private static final long serialVersionUID = 8923085455603538447L;

            public void execute(Agent agent) {
                Collections.sort(((YieldZone) agent).getAvailableLocations());
            }
        });

        DataImporter.importMap(this);


        /*
         * Create Households
         */
        households = new Scape();
        households.setName("Households");
        add(households);
        HouseholdAggregate protoHousehold = new HouseholdAggregate();
        protoHousehold.setMembersActive(false);
        households.setPrototypeAgent(protoHousehold);

        /*
         * Create Simulation Settlements
         */
        settlements = new Scape();
        settlements.setName("Settlements");
        Settlement protoSettlement = new Settlement();
        protoSettlement.setMembersActive(false);
        add(settlements);
        settlements.setPrototypeAgent(protoSettlement);

        /*
         * Create Farms
         */
        farms = new Scape();
        farms.setName("Farms");
        farms.setPrototypeAgent(new Farm());
    }

    public void createDrawFeatures() {
        LHV.FillValleyCellFeature zoneFill =
                new LHV.FillValleyCellFeature("Environment Zone", new ColorFeatureConcrete() {

                    private static final long serialVersionUID = 8060365487620083420L;

                    public Color getColor(Object o) {
                        return (((Location) o).getEnvironmentZone().getColor());
                    }
                });
        addDrawFeature(zoneFill);

        LHV.FillValleyCellFeature maizeZoneFill =
                new LHV.FillValleyCellFeature("Maize Zone", new ColorFeatureConcrete() {

                    private static final long serialVersionUID = -7825780654109831237L;

                    public Color getColor(Object o) {
                        return (((Location) o).getMaizeZone().getColor());
                    }
                });
        addDrawFeature(maizeZoneFill);

        LHV.FillValleyCellFeature yieldZoneFill =
                new LHV.FillValleyCellFeature("Yield Zone", new ColorFeatureConcrete() {

                    private static final long serialVersionUID = -8182575802685007681L;

                    public Color getColor(Object o) {
                        return (((Location) o).getYieldZone().getColor());
                    }
                });
        addDrawFeature(yieldZoneFill);

        LHV.FillValleyCellFeature hydroFill =
                new LHV.FillValleyCellFeature("Hydrology", new ColorFeatureGradiated(Color.blue, new UnitIntervalDataPoint() {

                    private static final long serialVersionUID = -2268057702246783384L;

                    public double getValue(Object object) {
                        return ((((Location) object).getEnvironmentZone().getHydrology()) / 10.0);
                    }
                }));
        addDrawFeature(hydroFill);

        LHV.FillValleyCellFeature apdsiFill =
                new LHV.FillValleyCellFeature("APDSI", new ColorFeatureGradiated(Color.red, new UnitIntervalDataPoint() {

                    private static final long serialVersionUID = 7338600146527039554L;

                    public double getValue(Object object) {
                        return ((((Location) object).getEnvironmentZone().getAPDSI()) / 10.0);
                    }
                }));
        addDrawFeature(apdsiFill);

        LHV.FillValleyCellFeature yieldFill =
                new LHV.FillValleyCellFeature("Plot Yield", new ColorFeatureGradiated(Color.orange, new UnitIntervalDataPoint() {

                    private static final long serialVersionUID = -5294758012864949871L;

                    public double getValue(Object object) {
                        return ((((Location) object).getBaseYield()) / 1200.0);
                    }
                }));
        addDrawFeature(yieldFill);

        LHV.FillValleyCellFeature zoneYieldFill =
                new LHV.FillValleyCellFeature("Zone Yield", new ColorFeatureGradiated(Color.orange, new UnitIntervalDataPoint() {

                    private static final long serialVersionUID = 322526470426236151L;

                    public double getValue(Object object) {
                        return ((double) (((Location) object).getYieldZone().getYield()) / 1200.0);
                    }
                }));
        addDrawFeature(zoneYieldFill);

        DrawFeature drawWaterFeature = new DrawFeature("Water Sources") {

            private static final long serialVersionUID = 8533411178579775478L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).isCurrentWaterSource()) {
                    g.setColor(Color.blue);
                    g.fillOval(0, 0, width - 2, height - 2);
                }
            }
        };
        addDrawFeature(drawWaterFeature);

        DrawFeature drawFarmFeature = new DrawFeature("Farms") {

            private static final long serialVersionUID = -1940011486883417752L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).getFarm() != null) {
                    g.setColor(Color.yellow);
                    DrawSymbol.DRAW_HATCH.draw(g, width - 2, height - 2);
                }
            }
        };
        addDrawFeature(drawFarmFeature);

        DrawFeature sandDuneFeature = new DrawFeature("Sand Dunes") {

            private static final long serialVersionUID = -2391074808277172861L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).isSandDune()) {
                    g.setColor(Color.green);
                    g.fillOval(0 + 2, 0 + 2, width - 2, height - 2);
                }
            }
        };
        addDrawFeature(sandDuneFeature);

        final ColorFeatureGradiated historicSettlementSizeColor = new ColorFeatureGradiated("Households");
        historicSettlementSizeColor.setDataPoint(new UnitIntervalDataPoint() {

            private static final long serialVersionUID = 6295840997659754327L;

            public double getValue(Object object) {
                return ((double) (((Location) object).getHistoricSettlementHouseholdCount() - 1) / 10.0);
            }
        });

        final ColorFeatureGradiated settlementSizeColor =
                new ColorFeatureGradiated(String.format("Settlements (%s)", name));
        settlementSizeColor.setDataPoint(new UnitIntervalDataPoint() {

            private static final long serialVersionUID = 1044376827552903900L;

            public double getValue(Object object) {
                return ((double) (((Settlement) object).getSize() - 1) / 10.0);
            }
        });
        DrawFeature settlementFeature =
                new DrawFeature(String.format("Simulation Settlements (%s)", name)) {

            private static final long serialVersionUID = 886210092045835742L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).getSettlement() != null) {
                    g.setColor(settlementSizeColor.getColor(((Location) object).getSettlement()));
                    DrawSymbol.FILL_OVOID.draw(g, width - 1, height - 1);
                    g.setColor(Color.black);
                    DrawSymbol.DRAW_OVOID.draw(g, width - 1, height - 1);
                }
            }
        };
        addDrawFeature(settlementFeature);
        DrawFeature simSettlementTierFeature =
                new DrawFeature(String.format("Simulation Settlement Tier (%s)", name)) {

            private static final long serialVersionUID = 2663578481949934207L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).isCurrentHistoricSettlement()) {
                    if (((Location) object).getHistoricSettlementHouseholdCount() < 5) {
                        g.setColor(Color.black);
                    } else if (((Location) object).getHistoricSettlementHouseholdCount() < 20) {
                        g.setColor(Color.green);
                    } else {
                        g.setColor(Color.red);
                    }
                    DrawSymbol.FILL_OVOID.draw(g, width, height);
                }
            }
        };
        addDrawFeature(simSettlementTierFeature);

    }

    public void createViews() {
        super.createViews();

        Overhead2DView[] views = new Overhead2DView[1];
        views[0] = new Overhead2DView();
        views[0].setCellSize(5);
        views[0].setName(String.format("Simulation (%s)", name));
        addViews(views);

        views[0].getDrawSelection().clearSelection();
        views[0].getDrawSelection().setSelected("Hydrology", true);
        views[0].getDrawSelection().setSelected("Farms", true);
        views[0].getDrawSelection().setSelected(String.format("Simulation Settlement Tier (%s)", name), true);
        views[0].getDrawSelection().setSelected("Water Sources", true);

    }

}
