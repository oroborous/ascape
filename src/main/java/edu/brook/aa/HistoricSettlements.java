package edu.brook.aa;

import org.ascape.model.Scape;
import org.ascape.model.rule.CollectStats;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.data.StatCollectorCondCSA;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.util.vis.DrawFeature;
import org.ascape.util.vis.DrawSymbol;
import org.ascape.view.vis.Overhead2DView;

import java.awt.*;
import java.util.Arrays;

public class HistoricSettlements extends Scape {

    private Scape historicSettlements;
    private Scape valley;
    private Scape waterSources;
    private YieldZones yieldZones;
    private LHVMachineLearning[] simScapes;

    public HistoricSettlements(LHVMachineLearning... simScapes) {
        this.simScapes = simScapes;

        setName("Long House Valley (Historical)");

        setPrototypeAgent(new Scape());
        getRules().clear();
    }

    private void createDrawFeatures() {
        LHV.FillValleyCellFeature zoneFill =
                new LHV.FillValleyCellFeature("Environment Zone", new ColorFeatureConcrete() {

                    private static final long serialVersionUID = 8060365487620083420L;

                    public Color getColor(Object o) {
                        return (((Location) o).getEnvironmentZone().getColor());
                    }
                });
        valley.addDrawFeature(zoneFill);

        LHV.FillValleyCellFeature maizeZoneFill =
                new LHV.FillValleyCellFeature("Maize Zone", new ColorFeatureConcrete() {

                    private static final long serialVersionUID = -7825780654109831237L;

                    public Color getColor(Object o) {
                        return (((Location) o).getMaizeZone().getColor());
                    }
                });
        valley.addDrawFeature(maizeZoneFill);

        LHV.FillValleyCellFeature yieldZoneFill =
                new LHV.FillValleyCellFeature("Yield Zone", new ColorFeatureConcrete() {

                    private static final long serialVersionUID = -8182575802685007681L;

                    public Color getColor(Object o) {
                        return (((Location) o).getYieldZone().getColor());
                    }
                });
        valley.addDrawFeature(yieldZoneFill);

        LHV.FillValleyCellFeature hydroFill =
                new LHV.FillValleyCellFeature("Hydrology", new ColorFeatureGradiated(Color.blue, new UnitIntervalDataPoint() {

                    private static final long serialVersionUID = -2268057702246783384L;

                    public double getValue(Object object) {
                        return ((((Location) object).getEnvironmentZone().getHydrology()) / 10.0);
                    }
                }));
        valley.addDrawFeature(hydroFill);

        LHV.FillValleyCellFeature apdsiFill =
                new LHV.FillValleyCellFeature("APDSI", new ColorFeatureGradiated(Color.red, new UnitIntervalDataPoint() {

                    private static final long serialVersionUID = 7338600146527039554L;

                    public double getValue(Object object) {
                        return ((((Location) object).getEnvironmentZone().getAPDSI()) / 10.0);
                    }
                }));
        valley.addDrawFeature(apdsiFill);

        LHV.FillValleyCellFeature yieldFill =
                new LHV.FillValleyCellFeature("Plot Yield", new ColorFeatureGradiated(Color.orange, new UnitIntervalDataPoint() {

                    private static final long serialVersionUID = -5294758012864949871L;

                    public double getValue(Object object) {
                        return ((((Location) object).getBaseYield()) / 1200.0);
                    }
                }));
        valley.addDrawFeature(yieldFill);

        LHV.FillValleyCellFeature zoneYieldFill =
                new LHV.FillValleyCellFeature("Zone Yield", new ColorFeatureGradiated(Color.orange, new UnitIntervalDataPoint() {

                    private static final long serialVersionUID = 322526470426236151L;

                    public double getValue(Object object) {
                        return ((double) (((Location) object).getYieldZone().getYield()) / 1200.0);
                    }
                }));
        valley.addDrawFeature(zoneYieldFill);

        DrawFeature drawWaterFeature = new DrawFeature("Water Sources") {

            private static final long serialVersionUID = 8533411178579775478L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).isCurrentWaterSource()) {
                    g.setColor(Color.blue);
                    g.fillOval(0, 0, width - 2, height - 2);
                }
            }
        };
        valley.addDrawFeature(drawWaterFeature);

        DrawFeature drawFarmFeature = new DrawFeature("Farms") {

            private static final long serialVersionUID = -1940011486883417752L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).getFarm() != null) {
                    g.setColor(Color.yellow);
                    DrawSymbol.DRAW_HATCH.draw(g, width - 2, height - 2);
                }
            }
        };
        valley.addDrawFeature(drawFarmFeature);

        DrawFeature sandDuneFeature = new DrawFeature("Sand Dunes") {

            private static final long serialVersionUID = -2391074808277172861L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).isSandDune()) {
                    g.setColor(Color.green);
                    g.fillOval(0 + 2, 0 + 2, width - 2, height - 2);
                }
            }
        };
        valley.addDrawFeature(sandDuneFeature);

        final ColorFeatureGradiated historicSettlementSizeColor = new ColorFeatureGradiated("Households");
        historicSettlementSizeColor.setDataPoint(new UnitIntervalDataPoint() {

            private static final long serialVersionUID = 6295840997659754327L;

            public double getValue(Object object) {
                return ((double) (((Location) object).getHistoricSettlementHouseholdCount() - 1) / 10.0);
            }
        });
        historicSettlementSizeColor.setMaximumColor(Color.red);
        DrawFeature historicSettlementFeature = new DrawFeature("Historic Settlements") {

            private static final long serialVersionUID = -3243407849851172816L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).isCurrentHistoricSettlement()) {
                    g.setColor(historicSettlementSizeColor.getColor(object));
                    DrawSymbol.FILL_OVOID.draw(g, width, height);
                    g.setColor(Color.red);
                    DrawSymbol.DRAW_OVOID.draw(g, width, height);
                }
            }
        };
        valley.addDrawFeature(historicSettlementFeature);

        DrawFeature histSettlementTierFeature = new DrawFeature("Historical Settlement Tier") {

            private static final long serialVersionUID = 8151081684304662162L;

            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).getSettlement() != null) {
                    if (((Location) object).getSettlement().getSize() < 5) {
                        g.setColor(Color.black);
                    } else if (((Location) object).getSettlement().getSize() < 20) {
                        g.setColor(Color.green);
                    } else {
                        g.setColor(Color.red);
                    }
                    DrawSymbol.FILL_OVOID.draw(g, width, height);
                }
            }
        };
        valley.addDrawFeature(histSettlementTierFeature);

    }

    public void createScape() {
        valley = new Scape(new Array2DMoore());
        add(valley);
        valley.setName("Locations");
        valley.setPrototypeAgent(new Location());
        valley.setExtent(new Coordinate2DDiscrete(80, 120));
        valley.getRules().clear();
        valley.setAutoCreate(false);

        /*
         * Create water sources
         */
        waterSources = new Scape();
        add(waterSources);
        waterSources.setName("Water Sources");
        waterSources.setPrototypeAgent(new WaterSource());
        waterSources.setAutoCreate(false);
        waterSources.getRules().clear();

        /*
         * Create Yield Zones
         */
        yieldZones = new YieldZones();
        yieldZones.createScape();
        add(yieldZones);


        /*
         * Create Historic Settlements
         */
        historicSettlements = new Scape() {

            private static final long serialVersionUID = 8710583027169504915L;

            public void initialize() {
                super.initialize();
                CollectStats collector = new CollectStats();
                StatCollectorCondCSA countHouseholds = new StatCollectorCondCSA() {
                    private static final long serialVersionUID = -8509781010737527704L;

                    public double getValue(Object object) {
                        return ((HistoricSettlement) object).getHouseholdCount();
                    }

                    public boolean meetsCondition(Object object) {
                        return ((HistoricSettlement) object).isExtant();
                    }
                };
                collector.addStatCollector(countHouseholds);
                historicSettlements.executeOnMembers(collector);
                int householdCount = (int) countHouseholds.getSum();
                Arrays.stream(HistoricSettlements.this.simScapes)
                        .forEach(s -> s.setHouseholdCount(householdCount));

            }
        };
        add(historicSettlements);
        historicSettlements.setPrototypeAgent(new HistoricSettlement());
        historicSettlements.setAutoCreate(false);
        historicSettlements.setName("Historic Settlements");
        historicSettlements.getRules().clear();

        valley.createScape();

        DataImporter.importMap(valley, yieldZones);
        DataImporter.importWaterSources(valley, waterSources);
        DataImporter.importSettlementHistory(valley, historicSettlements);

        createDrawFeatures();

        setAutoCreate(false);
    }

    @Override
    public void createViews() {
        Overhead2DView[] views = new Overhead2DView[1];
        views[0] = new Overhead2DView();
        views[0].setCellSize(5);
        views[0].setName("Historic");
        valley.addViews(views);

        views[0].getDrawSelection().clearSelection();
        views[0].getDrawSelection().setSelected("Hydrology", true);
        views[0].getDrawSelection().setSelected("Farms", true);
        views[0].getDrawSelection().setSelected("Historical Settlement Tier", true);
        views[0].getDrawSelection().setSelected("Water Sources", true);
    }

}
