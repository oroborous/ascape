/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package edu.brook.aa;

import edu.brook.aa.log.BuildFarmDecision;
import edu.brook.aa.log.EventType;
import edu.brook.aa.log.HouseholdEvent;
import edu.brook.aa.log.Logger;
import org.ascape.model.Scape;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.Conditional;
import org.ascape.util.data.DataPoint;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.data.StatCollectorCond;

import java.util.ArrayList;
import java.util.List;

public abstract class HouseholdBase extends Scape {


    public static final String DEATHS_OLD_AGE = "Deaths Old Age";
    public static final String DEATHS_STARVATION = "Deaths Starvation";
    public static final String FISSIONS = "Fissions";
    public static final String BIRTHS = "Births";
    public static final String HOUSEHOLD_SIZE = "Household Size";
    public static final String HOUSEHOLDS_DISBANDED = "Households Disbanded";
    public static final String HOUSEHOLDS_FORMED = "Households Formed";
    public final static DataPoint BEST_FARM = new DataPoint() {
        public String getName() {
            return "Best Farm";
        }

        public double getValue(Object object) {
            if (((Location) object).isAvailable()) {
                return ((Location) object).getBaseYield();
            } else {
                return 0.0;
            }
        }
    };
    private static final long serialVersionUID = 4016766647753095651L;
    static int maximumFarmlandsSearched = 100;
    private static int nextId = 1;
    public int id;

    protected List<Farm> farms;

    protected Settlement settlement;

    protected int lastHarvest;

    protected int fertilityAge;

    protected int deathAge;

    protected int fertilityEndsAge;

    protected double fertility;

    //The last item in cornstocks is unusable, except as a holder for
    //use when aging a years cornstocks.
    protected int[] agedCornStocks;

    private Clan clan;
    public Conditional FIND_SETTLEMENT_RULE = new Conditional() {

        private static final long serialVersionUID = -1952585657699460589L;

        public boolean meetsCondition(Object o) {
            return ((((Location) o).isOnMap()) && (((Location) o).getFarm() == null) &&
                    ((((Location) o).getClan() == null) || (((Location) o).getClan() == clan)) &&
                    (((Location) o).hasWithin(Location.LOW_EROSION, true, 1.0)));
        }
    };

    protected Farm addFarm() {
        Farm farm = new Farm();
        farm.setHousehold(this);
        farms.add(farm);
        return farm;
    }

    /**
     * Clones the host cell, making occupant and neighbors null.
     */
    public Object clone() {
        HouseholdBase clone = (HouseholdBase) super.clone();
        //clone.valley = this.valley;
        clone.farms = new ArrayList<>();
        clone.settlement = null;
        //System.out.println(scape);
        return clone;
    }

    /**
     * Consume the given quantity of corn.
     * If the quantity available is greater than or equal to the quantity needed, removes
     * from stores the quantity requested, and returns 0.
     * If the quantity available is less than the quantity needed, removes from stores all
     * remaining food, and returns the amount of need not met.
     *
     * @param amount the quantity of food needed
     */
    public int consumeCorn(int amount) {
        //Loop down from 1 greater than the size of current year's stock
        for (int i = LHV.yearsOfStock; i >= 0; i--) {
            if (agedCornStocks[i] >= amount) {
                agedCornStocks[i] -= amount;
                return 0;
            } else {
                amount -= agedCornStocks[i];
                agedCornStocks[i] = 0;
            }
        }
        //Not all need was meet
        return amount;
    }

    public void depart() {
        die();
        getStatCollector("Departures").addValue(0.0);
    }

    public void die() {
        if (!scape.remove(this)) {
            throw new RuntimeException("Tried to kill an agent not a member of its Scape.");
        }
        leave();
    }

    public void findFarmAndSettlement() {
        List<Location> farmsSearched = new ArrayList<>();

        while (farms.size() == 0) {
            Location farmLocation = getLHVRoot().removeBestLocation();

            if (farmLocation != null) {

                if (farmLocation.getBaseYield() > 0) {

                    if ((farmLocation.getClan() == null) || (farmLocation.getClan() == clan)) {
                        Location nearestWaterInRange = (Location) farmLocation
                                .findNearest(Location.HAS_WATER, true, LHV.waterSourceDistance);

                        if (nearestWaterInRange != null) {

                            if (settlement != null) {
                                settlement.remove(this);
                                settlement = null;
                            }

                            addFarm().occupy(farmLocation);

                            Location nearestSettlementSite = (Location) nearestWaterInRange
                                    .findNearest(FIND_SETTLEMENT_RULE, true, Double.MAX_VALUE);

                            if (nearestSettlementSite == null) {
                                throw new RuntimeException("Unexpected model condition in household");
                            }
                            occupy(nearestSettlementSite);

                            if (!findFarmsForNutritionalNeed()) {
                                leave();
                                farmsSearched.add(farmLocation);
                                break;
                            }
                        } else {
                            //No nearby water location found...
                            farmsSearched.add(farmLocation);
                        }
                    } else {
                        //No nearby water location found...
                        farmsSearched.add(farmLocation);
                    }
                } else {
                    //Yield < need, so give up search...
                    farmsSearched.add(farmLocation);
                    break;
                }
            } else {
                //Null farmLocation, so no valley locations with any yield are left...
                return;
            }
        }
        for (Location unusedLocation : farmsSearched) {
            unusedLocation.makeAvailable();
        }
    }

    public boolean findFarmsForNutritionalNeed() {
        int need = getNutritionNeed();
        int adultCount = 0;
        int adults = getNumAdults();
        double currentEstimate = 0;

        for (Farm farm : farms) {
            currentEstimate += farm.getLocation().getBaseYield();
            adultCount++;
        }
        while (true) {
            if (currentEstimate >= need) {
                return true;
            }
            adultCount++;
            if (adultCount > adults) {
                return false;
            }
            Location best = (Location) settlement.getLocation()
                    .findMaximumWithin(BEST_FARM, false, LHV.waterSourceDistance);
            Location nearestWater = (Location) best.findNearest(Location.HAS_WATER, true, Double.MAX_VALUE);
            int distanceToWater = (int) best.calculateDistance(nearestWater);

            if (best != null) {
                boolean isOccupy = (best.isAvailable()) && (best.getBaseYield() > 0.0);
                Logger.INSTANCE.log(new BuildFarmDecision(getScape().getPeriod(), EventType.BUILD_FARM, isOccupy,
                        (HouseholdAggregate) this, best, distanceToWater));
            }
            if ((best != null) && (best.isAvailable()) && (best.getBaseYield() > 0.0)) {
                addFarm().occupy(best);
                currentEstimate += best.getBaseYield();
                //System.out.println(adults+" "+adultCount+", "+getSize()+"-"+currentEstimate);
            } else {
                return false;
            }
        }
        //if (farmLocation.getBaseYield() >= getLHVRoot().getHouseholdMinNutritionNeed()) {
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    private double getDistance(Location l1, Location l2) {
        if (l1 == null || l2 == null)
            return -1;
        Coordinate2DDiscrete c1 = (Coordinate2DDiscrete) l1.getCoordinate();
        Coordinate2DDiscrete c2 = (Coordinate2DDiscrete) l2.getCoordinate();
        return Math.sqrt(Math.pow(c1.getXValue() - c2.getXValue(), 2)
                + Math.pow(c1.getYValue() - c2.getYValue(), 2));
    }

    public int getEstimateNextYearCorn() {
        return getTotalCornStocks() + lastHarvest;
    }

    public double getEstimatedNutritionAvailable() {
        return farms.stream().mapToDouble(farm -> farm.getLocation().getBaseYield()).sum();
    }

    private LHVMachineLearning getLHVRoot() {
        Scape parent = getScape();
        while (!(parent instanceof LHVMachineLearning)) {
            parent = parent.getScape();
        }
        return (LHVMachineLearning) parent;
    }

    public abstract int getNumAdults();

    public int getNutritionNeed() {
        return 0;
    }

    public Settlement getSettlement() {
        return settlement;
    }

    protected StatCollector getStatCollector(String name) {
        return scape.getData().getStatCollector(name + getStatCollectorSuffix());
    }

    public abstract String getStatCollectorSuffix();

    public int getTotalCornStocks() {
        int total = 0;
        for (int i = 0; i < LHV.yearsOfStock; i++) {
            total += agedCornStocks[i];
        }
        return total;
    }

    public void giveMaizeGift(HouseholdBase recipient) {
        for (int i = 0; i < agedCornStocks.length - 1; i++) {
            recipient.agedCornStocks[i] = (int) (agedCornStocks[i] * LHV.maizeGiftToChild);
            agedCornStocks[i] = (int) (agedCornStocks[i] * (1.0 - LHV.maizeGiftToChild));
        }
    }

    public boolean hasFarm() {
        return !farms.isEmpty();
    }

    public boolean hasSettlement() {
        return settlement != null;
    }

    public void initialize() {
        super.initialize();
        id = nextId++;

        setMembersActive(false);
        agedCornStocks = new int[LHV.yearsOfStock + 1];
        for (int i = 0; i < agedCornStocks.length - 1; i++) {
            agedCornStocks[i] = randomInRange(LHV.householdMinInitialCorn, LHV.householdMaxInitialCorn);
        }
        lastHarvest = 0;
        //Change mtp 8/18/00
        //farm = new Farm();
        farms = new ArrayList<>();
        settlement = null;
        clan = null;

        //Support for hetergeneous fertility, etc..
        //added 6/7/2000 mtp

        fertilityAge = randomInRange(LHV.minFertilityAge, LHV.maxFertilityAge);
        deathAge = randomInRange(LHV.minDeathAge, LHV.maxDeathAge);
        fertilityEndsAge = randomInRange(LHV.minFertilityEndsAge, LHV.maxFertilityEndsAge);
        fertility = randomInRange(LHV.minFertility, LHV.maxFertility);
    }

    public void leave() {
        leaveAllFarms();

        if (settlement != null) {
            int currentSize = settlement.getSize();
            settlement.remove(this);
            int newSize = settlement.getSize();
            settlement = null;
        }
    }

    public void leaveAllFarms() {
        for (Farm farm : farms) {
            farm.leave();
        }
        farms.clear();
    }

    public void metabolism() {
        lastHarvest = 0;
        int adultCount = 0;
        int adults = getNumAdults();

        for (Farm farm : farms) {
            lastHarvest += farm.getLocation().findRandomYield();
            adultCount++;
            if (adultCount >= adults) {
                break;
            }
        }
        //lastHarvest = (int) ((Location) farm.getLocation()).findRandomYield();
        for (int i = LHV.yearsOfStock - 1; i >= 0; i--) {
            agedCornStocks[i + 1] = agedCornStocks[i];
        }
        agedCornStocks[0] = lastHarvest;
    }

    public void move() {
        leave();

        findFarmAndSettlement();

        boolean isMove = farms.isEmpty() || settlement == null;

        Logger.INSTANCE.log(new HouseholdEvent(getScape().getPeriod(),
                EventType.DEPART, isMove, this));

        if ((farms.size() == 0) || (settlement == null)) {
            depart();
        }
    }

    public boolean movementCondition() {
        if (getEstimateNextYearCorn() < getNutritionNeed()) {
            if (!findFarmsForNutritionalNeed()) {
                Logger.INSTANCE.log(new HouseholdEvent(getScape().getPeriod(),
                        EventType.MOVE, true, this));
                getStatCollector("Movements").addValue(0.0);
                return true;
            } else {
                Logger.INSTANCE.log(new HouseholdEvent(getScape().getPeriod(),
                        EventType.MOVE, false, this));
                return false;
            }
        }
        Logger.INSTANCE.log(new HouseholdEvent(getScape().getPeriod(),
                EventType.MOVE, false, this));
        return false;
    }

    public void occupy(Location location) {
        if (location.getSettlement() == null) {
            location.createSettlement();
        }
        settlement = location.getSettlement();
        settlement.add(this, false);
    }

    public void scapeCreated() {
        scape.addInitialRule(FORCE_MOVE_RULE);
        String suffix = getStatCollectorSuffix();

        StatCollector[] stats = new StatCollector[5];
        stats[0] = new StatCollector("Households" + suffix);
        stats[1] = new StatCollector("Movements" + suffix, false);
        stats[2] = new StatCollector("Fissions" + suffix, false);
        stats[3] = new StatCollector("Departures" + suffix, false);
        stats[4] = new StatCollectorCSAMM("Farms Per Household" + suffix) {

            private static final long serialVersionUID = -3060585396202230071L;

            public double getValue(Object o) {
                return ((HouseholdBase) o).farms.size();
            }
        };
        scape.addStatCollectors(stats);

        for (int i = 0; i < ((LHV) scape.getRoot()).getEnvironmentZones().getSize(); i++) {
            SettlementZoneStatCollector settlementStatCollector = new SettlementZoneStatCollector();
            settlementStatCollector.zone = (EnvironmentZone) ((LHV) scape.getRoot()).getEnvironmentZones().get(i);
            scape.addStatCollector(settlementStatCollector);
//            FarmZoneStatCollector farmStatCollector = new FarmZoneStatCollector();
//            farmStatCollector.zone = (EnvironmentZone) ((LHV) scape.getRoot()).getEnvironmentZones().get(i);
//            scape.addStatCollector(farmStatCollector);
        }
    }

    /*public Farm getFarm() {
        return farm;
    }*/

    public String toInnerString() {
        return "household size " + getSize() + (getClan() != null ? getClan().toString() : "");
    }

    /**
     * Returns a description of the person including age, sex and household.
     */
    public String toString() {
        return "A " + toInnerString();
    }

    static class SettlementZoneStatCollector extends StatCollectorCond {

        private static final long serialVersionUID = 5283713940057557749L;
        EnvironmentZone zone;

        public String getName() {
            return "Households in " + zone.getName();
        }

        public boolean meetsCondition(Object o) {
            Settlement s = ((HouseholdBase) o).getSettlement();
            if (s != null) {
                return (s.getLocation().getEnvironmentZone() == zone);
            } else {
                return false;
            }
        }
    }

    static class FarmZoneStatCollector extends StatCollectorCond {


        private static final long serialVersionUID = 6863625481169667385L;
        EnvironmentZone zone;

        public String getName() {
            return "Farms in " + zone.getName();
        }

        public boolean meetsCondition(Object o) {
            /*Location f = ((HouseholdBase) o).getFarm().getLocation();
            if (f != null) {
                return (f.getEnvironmentZone() == zone);
            }
            else {
                return false;
            }*/
            //Fix
            return false;
        }
    }
}
