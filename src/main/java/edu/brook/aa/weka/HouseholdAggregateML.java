package edu.brook.aa.weka;

import edu.brook.aa.HouseholdBase;
import edu.brook.aa.LHV;
import edu.brook.aa.log.EventType;
import org.ascape.model.Agent;
import org.ascape.model.rule.Rule;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;

public class HouseholdAggregateML extends HouseholdBase {

    private static final long serialVersionUID = 5091800912116536871L;
    private static int nextId = 1;
    private int nutritionNeedRemaining;
    private int age;
    private int nutritionNeed;
    public final Rule DECISION_TREE_RULE = new Rule("Obey Decision Tree") {

        public void execute(Agent agent) {
            HouseholdAggregateML hha = (HouseholdAggregateML) agent;

            Object[] props = new Object[]{(double) hha.getAge(),
                    Boolean.toString(hha.hasFarm()),
                    (double) hha.getNutritionNeed(),
                    (double) hha.getTotalCornStocks(),
                    (double) hha.getEstimateNextYearCorn(),
                    getRandom().nextDouble(),
                    hha.getFertility()};
            EventType decision = WekaDecisionClassifier.classify(props);

            switch (decision) {
                case DIE_OLD_AGE:
                    HouseholdAggregateML.this.getStatCollector(DEATHS_OLD_AGE).addValue(0.0);
                    hha.die();
                    break;
                case DIE_STARVATION:
                    HouseholdAggregateML.this.getStatCollector(DEATHS_STARVATION).addValue(0.0);
                    hha.die();
                    break;
                case DEPART:
                    hha.depart();
                    break;
                case MOVE:
                    hha.move();
                    break;
                case FISSION:
                    hha.fission();
                    break;
            }
        }

        public boolean isCauseRemoval() {
            return true;
        }

        public boolean isRandomExecution() {
            return false;
        }
    };

    public void fission() {
        HouseholdAggregateML child = new HouseholdAggregateML();//(Household) this.clone();
        scape.add(child);
        child.initialize();
        child.age = 0;
        giveMaizeGift(child);
        child.move();
        //if ((child.farm.getLocation() != null) && (child.settlement != null)) {
        //For now, record fissions regardless of successful move to match C++ code
        getStatCollector(FISSIONS).addValue(0.0);
        //}
        //System.out.println(child.age);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getNumAdults() {
        return 1;
    }

    public int getNutritionNeed() {
        return nutritionNeed;
    }

    public int getStatCollectorIndex() {
        return 2;
    }

    @Override
    public String getStatCollectorSuffix() {
        return " (ML)";
    }

    public void initialize() {
        super.initialize();
        this.id = nextId++;
        setMembersActive(false);

        age = randomInRange(LHV.householdMinInitialAge, LHV.householdMaxInitialAge);
        nutritionNeed = randomInRange(LHV.householdMinNutritionNeed, LHV.householdMaxNutritionNeed);
    }

    public void metabolism() {
        super.metabolism();
        age++;
        nutritionNeedRemaining = consumeCorn(nutritionNeed);
    }

    public void scapeCreated() {
        super.scapeCreated();

        scape.addRule(METABOLISM_RULE);
        scape.addRule(DECISION_TREE_RULE);

        String suffix = getStatCollectorSuffix();

        StatCollector[] stats = new StatCollector[4];
        stats[0] = new StatCollector(DEATHS_STARVATION + suffix, false);
        stats[1] = new StatCollector(DEATHS_OLD_AGE + suffix, false);
        stats[2] = new StatCollector(BIRTHS + suffix, false);
        stats[3] = new StatCollectorCSAMM(HOUSEHOLD_SIZE + suffix) {

            private static final long serialVersionUID = 5919164193477195628L;

            public final double getValue(Object o) {
                return 5;
            }
        };
        scape.addStatCollectors(stats);
    }
}
