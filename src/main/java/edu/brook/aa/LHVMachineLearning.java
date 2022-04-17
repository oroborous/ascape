package edu.brook.aa;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.util.vis.DrawFeature;
import org.ascape.util.vis.DrawSymbol;
import org.ascape.view.vis.Overhead2DView;

import java.awt.*;

import static edu.brook.aa.LHV.FillValleyCellFeature;


public class LHVMachineLearning extends Scape {

    public boolean farmSitesAvailable = true;
    public YieldZones yieldZones;
    private Scape valley;
    private Scape households;
    private Scape settlements;
    private Scape farms;
    private Scape waterSources;

    private HouseholdBase protoHousehold;

    public LHVMachineLearning(String name, HouseholdBase protoHousehold) {
        setName("Long House Valley " + name);

        this.protoHousehold = protoHousehold;

        setPrototypeAgent(new Scape());
        getRules().clear();
    }

    public void addSettlement(Settlement settlement) {
        this.settlements.add(settlement);
    }

    private void createDrawFeatures() {
        FillValleyCellFeature zoneFill =
                new FillValleyCellFeature("Environment Zone", new ColorFeatureConcrete() {

                    private static final long serialVersionUID = 8060365487620083420L;

                    public Color getColor(Object o) {
                        return (((Location) o).getEnvironmentZone().getColor());
                    }
                });
        valley.addDrawFeature(zoneFill);

        FillValleyCellFeature maizeZoneFill =
                new FillValleyCellFeature("Maize Zone", new ColorFeatureConcrete() {

                    private static final long serialVersionUID = -7825780654109831237L;

                    public Color getColor(Object o) {
                        return (((Location) o).getMaizeZone().getColor());
                    }
                });
        valley.addDrawFeature(maizeZoneFill);

        FillValleyCellFeature yieldZoneFill =
                new FillValleyCellFeature("Yield Zone", new ColorFeatureConcrete() {

                    private static final long serialVersionUID = -8182575802685007681L;

                    public Color getColor(Object o) {
                        return (((Location) o).getYieldZone().getColor());
                    }
                });
        valley.addDrawFeature(yieldZoneFill);

        FillValleyCellFeature hydroFill =
                new FillValleyCellFeature("Hydrology", new ColorFeatureGradiated(Color.blue, new UnitIntervalDataPoint() {

                    private static final long serialVersionUID = -2268057702246783384L;

                    public double getValue(Object object) {
                        return ((((Location) object).getEnvironmentZone().getHydrology()) / 10.0);
                    }
                }));
        valley.addDrawFeature(hydroFill);

        FillValleyCellFeature apdsiFill =
                new FillValleyCellFeature("APDSI", new ColorFeatureGradiated(Color.red, new UnitIntervalDataPoint() {

                    private static final long serialVersionUID = 7338600146527039554L;

                    public double getValue(Object object) {
                        return ((((Location) object).getEnvironmentZone().getAPDSI()) / 10.0);
                    }
                }));
        valley.addDrawFeature(apdsiFill);

        FillValleyCellFeature yieldFill =
                new FillValleyCellFeature("Plot Yield", new ColorFeatureGradiated(Color.orange, new UnitIntervalDataPoint() {
                    public double getValue(Object object) {
                        return ((((Location) object).getBaseYield()) / 1200.0);
                    }
                }));
        valley.addDrawFeature(yieldFill);

        FillValleyCellFeature zoneYieldFill =
                new FillValleyCellFeature("Zone Yield", new ColorFeatureGradiated(Color.orange, new UnitIntervalDataPoint() {

                    private static final long serialVersionUID = 322526470426236151L;

                    public double getValue(Object object) {
                        return ((double) (((Location) object).getYieldZone().getYield()) / 1200.0);
                    }
                }));
        valley.addDrawFeature(zoneYieldFill);

        DrawFeature drawWaterFeature = new DrawFeature("Water Sources") {
            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).isCurrentWaterSource()) {
                    g.setColor(Color.blue);
                    g.fillOval(0, 0, width - 2, height - 2);
                }
            }
        };
        valley.addDrawFeature(drawWaterFeature);

        DrawFeature drawFarmFeature = new DrawFeature("Farms") {
            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).getFarm() != null) {
                    g.setColor(Color.yellow);
                    DrawSymbol.DRAW_HATCH.draw(g, width - 2, height - 2);
                }
            }
        };
        valley.addDrawFeature(drawFarmFeature);

        DrawFeature sandDuneFeature = new DrawFeature("Sand Dunes") {
            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).isSandDune()) {
                    g.setColor(Color.green);
                    g.fillOval(0 + 2, 0 + 2, width - 2, height - 2);
                }
            }
        };
        valley.addDrawFeature(sandDuneFeature);

        final ColorFeatureGradiated settlementSizeColor = new ColorFeatureGradiated("Settlements");
        settlementSizeColor.setDataPoint(new UnitIntervalDataPoint() {
            private static final long serialVersionUID = 1044376827552903900L;

            public double getValue(Object object) {
                return ((double) (((Settlement) object).getSize() - 1) / 10.0);
            }
        });
        DrawFeature settlementFeature = new DrawFeature("Simulation Settlements") {
            public void draw(Graphics g, Object object, int width, int height) {
                if (((Location) object).getSettlement() != null) {
                    g.setColor(settlementSizeColor.getColor(((Location) object).getSettlement()));
                    DrawSymbol.FILL_OVOID.draw(g, width - 1, height - 1);
                    g.setColor(Color.black);
                    DrawSymbol.DRAW_OVOID.draw(g, width - 1, height - 1);
                }
            }
        };
        valley.addDrawFeature(settlementFeature);
    }

    public void createScape() {
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
         * Create Locations
         */
        valley = new ValleyLocations();
        add(valley);

        valley.createScape();

        DataImporter.importMap(valley, yieldZones);
        DataImporter.importWaterSources(valley, waterSources);

        /*
         * Create Households
         */
        households = new Scape();
        households.setName("Households");
        add(households);
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

        createDrawFeatures();

        setAutoCreate(false);
    }

    public void createViews() {
        Overhead2DView[] views = new Overhead2DView[1];
        views[0] = new Overhead2DView();
        views[0].setCellSize(5);
        views[0].setName(String.format("Simulation (%s)", name));
        valley.addViews(views);

        views[0].getDrawSelection().clearSelection();
        views[0].getDrawSelection().setSelected("Hydrology", true);
        views[0].getDrawSelection().setSelected("Farms", true);
        views[0].getDrawSelection().setSelected("Simulation Settlements", true);
        views[0].getDrawSelection().setSelected("Water Sources", true);
    }

    public Location removeBestLocation() {
        FindBestLocation finder = new FindBestLocation();
        yieldZones.executeOnMembers(finder);
        if ((finder.getBestLocation() != null) && (finder.getBestLocation().getBaseYield() > 0)) {
            Locations l = finder.getBestList();
            return (Location) l.remove(l.size() - 1);
        } else {
            return null;
        }
    }

    public void scapeIterated(ScapeEvent event) {
        farmSitesAvailable = true;
        super.scapeIterated(event);
    }

    public void setHouseholdCount(int count) {
        households.setExtent(new Coordinate1DDiscrete(count));
    }

    public void setHouseholdsExtent(Coordinate1DDiscrete extent) {
        this.households.setExtent(extent);
    }


}

//Replace w/ comparison
class FindBestLocation extends Rule {


    private static final long serialVersionUID = 4269530128883979605L;
    private Location bestLocation;
    private Locations bestList;

    public FindBestLocation() {
        super("Find Best Location");
    }

    public void execute(Agent agent) {
        try {
            Location currentLocation = null;
            Locations l = ((YieldZone) agent).getAvailableLocations();
            if (l.size() > 0) {
                currentLocation = (Location) (l.get(l.size() - 1));
            }
            if ((currentLocation != null) &&
                    ((bestLocation == null) ||
                            (currentLocation.getBaseYield() > bestLocation.getBaseYield()))) {
                bestLocation = currentLocation;
                bestList = l;
            }
        } catch (java.util.NoSuchElementException ignored) {
        }
    }

    public Locations getBestList() {
        return bestList;
    }

    public Location getBestLocation() {
        return bestLocation;
    }

    public boolean isRandomExecution() {
        return false;
    }
}
