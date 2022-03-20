/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */



package org.ascape.examples.bionland;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JPanel;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.rule.Diffusable;
import org.ascape.model.rule.ParameterizedDiffusion;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Array2DMoore;
import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.util.Conditional;
import org.ascape.util.data.DataPoint;
import org.ascape.util.data.DataPointConcrete;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.swing.CustomSliderPanel;
import org.ascape.util.vis.DrawFeature;
import org.ascape.util.vis.ImageFeatureFixed;
import org.ascape.view.custom.AutoCustomizerSwing;
import org.ascape.view.vis.Overhead2DView;

public class Model_M extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -8681426110449045693L;

    protected Scape territory;

    private Scape redBions;

    private Scape orangeBions;

    private Scape blueBions;

    private double initialDensity = 0.28f;

    private double initialStoredEnergy = 1.5f;

    private double bionEnergyLeakage = 0.02f;

    private double bionEnergyConsumption = 0.015f;

    private double bionEnergyGain = 1.0f;

    private int minVision = 8;

    private int maxVision = 8;

    private class BionTile extends HostCell implements Diffusable {

        /**
         * 
         */
        private static final long serialVersionUID = -609440555902653488L;
        public double redEnergy;
        public double orangeEnergy;
        public double blueEnergy;

        public Color getColor() {
            return new Color(Math.min(1.0f, (float) (redEnergy + orangeEnergy)), (float) orangeEnergy * .8f, (float) blueEnergy);
        }

        public double getRedEnergy() {
            return redEnergy;
        }

        public void setRedEnergy(double redEnergy) {
            this.redEnergy = redEnergy;
        }

        public double getOrangeEnergy() {
            return orangeEnergy;
        }

        public void setOrangeEnergy(double orangeEnergy) {
            this.orangeEnergy = orangeEnergy;
        }

        public double getBlueEnergy() {
            return blueEnergy;
        }

        public void setBlueEnergy(double blueEnergy) {
            this.blueEnergy = blueEnergy;
        }

        private double diffusionTemp;

        public double getDiffusionTemp() {
            return diffusionTemp;
        }

        public void setDiffusionTemp(double diffusionTemp) {
            this.diffusionTemp = diffusionTemp;
        }
    }

    public static DataPointConcrete REDNESS = new DataPointConcrete() {
        /**
         * 
         */
        private static final long serialVersionUID = 4191447335134246865L;

        public double getValue(Object o) {
            return ((BionTile) o).getRedEnergy();
        }
    };

    public static DataPointConcrete ORANGENESS = new DataPointConcrete() {
        /**
         * 
         */
        private static final long serialVersionUID = 1465197441512701379L;

        public double getValue(Object o) {
            return ((BionTile) o).getOrangeEnergy();
        }
    };

    public static DataPointConcrete BLUENESS = new DataPointConcrete() {
        /**
         * 
         */
        private static final long serialVersionUID = 7414328998464939479L;

        public double getValue(Object o) {
            return ((BionTile) o).getBlueEnergy();
        }
    };

    private final ParameterizedDiffusion diffuse_red_energy = new ParameterizedDiffusion("Diffuse Red Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = -289583719686028857L;

        public double getDiffusionValue(Agent agent) {
            return ((BionTile) agent).getRedEnergy();
        }

        public void setDiffusionValue(Agent agent, double value) {
            ((BionTile) agent).setRedEnergy(value);
        }
    };

    private final ParameterizedDiffusion diffuse_orange_energy = new ParameterizedDiffusion("Diffuse Orange Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = 2107006819709146219L;

        public double getDiffusionValue(Agent agent) {
            return ((BionTile) agent).getOrangeEnergy();
        }

        public void setDiffusionValue(Agent agent, double value) {
            ((BionTile) agent).setOrangeEnergy(value);
        }
    };

    private final ParameterizedDiffusion diffuse_blue_energy = new ParameterizedDiffusion("Diffuse Blue Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = -7348814706806585797L;

        public double getDiffusionValue(Agent agent) {
            return ((BionTile) agent).getBlueEnergy();
        }

        public void setDiffusionValue(Agent agent, double value) {
            ((BionTile) agent).setBlueEnergy(value);
        }
    };

    private class Bion extends CellOccupant {

        /**
         * 
         */
        private static final long serialVersionUID = 6438999743543308723L;
        protected int vision;
        protected double storedEnergy;

        public void initialize() {
            super.initialize();
            vision = 8;
            storedEnergy = initialStoredEnergy;
        }

        public void leaveEnergy() {
            storedEnergy -= bionEnergyLeakage * (1.0f / bionEnergyGain);
        }

        public void consumeRedEnergy() {
            double energy = Math.min(((BionTile) getHostCell()).getRedEnergy(), bionEnergyConsumption);
            ((BionTile) getHostCell()).setRedEnergy(((BionTile) getHostCell()).getRedEnergy() - energy);
            storedEnergy += energy;
        }

        public void consumeOrangeEnergy() {
            double energy = Math.min(((BionTile) getHostCell()).getOrangeEnergy(), bionEnergyConsumption);
            ((BionTile) getHostCell()).setOrangeEnergy(((BionTile) getHostCell()).getOrangeEnergy() - energy);
            storedEnergy += energy;
        }

        public void consumeBlueEnergy() {
            double energy = Math.min(((BionTile) getHostCell()).getBlueEnergy(), bionEnergyConsumption);
            ((BionTile) getHostCell()).setBlueEnergy(((BionTile) getHostCell()).getBlueEnergy() - energy);
            storedEnergy += energy;
        }

        public boolean deathCondition() {
            return storedEnergy <= 0.0f;
        }

        public boolean fissionCondition() {
            return storedEnergy > 2.0f;
        }

        public void fission() {
            if (getHostCell().isNeighborAvailable()) {
                Bion child = (Bion) this.clone();
                getScape().add(child);
                child.moveTo(getHostCell().findRandomAvailableNeighbor());
                child.storedEnergy = storedEnergy / 2.0f;
                storedEnergy = storedEnergy / 2.0f;
            }
        }

        public int getVision() {
            return vision;
        }

        public void setVision(int vision) {
            this.vision = vision;
        }

        public double getStoredEnergy() {
            return storedEnergy;
        }

        public void setStoredEnergy(double storedEnergy) {
            this.storedEnergy = storedEnergy;
        }
    }

    class RedBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = -7046618598510455740L;

        public Color getColor() {
            return Color.red;
        }

        public Image getImage() {
            return ImageFeatureFixed.redBall;
        }

        public void leaveEnergy() {
            super.leaveEnergy();
            ((BionTile) getHostCell()).setRedEnergy(Math.min(((BionTile) getHostCell()).getRedEnergy() + bionEnergyLeakage, 1.0f));
        }
    }

    class OrangeBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = 6976821748733633057L;

        public Color getColor() {
            return Color.orange;
        }

        public Image getImage() {
            return ImageFeatureFixed.orangeBall;
        }

        public void leaveEnergy() {
            super.leaveEnergy();
            ((BionTile) getHostCell()).setOrangeEnergy(Math.min(((BionTile) getHostCell()).getOrangeEnergy() + bionEnergyLeakage, 1.0f));
        }
    }

    class BlueBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = 1613770523862127614L;

        public Color getColor() {
            return Color.blue;
        }

        public Image getImage() {
            return ImageFeatureFixed.blueBall;
        }

        public void leaveEnergy() {
            super.leaveEnergy();
            ((BionTile) getHostCell()).setBlueEnergy(Math.min(((BionTile) getHostCell()).getBlueEnergy() + bionEnergyLeakage, 1.0f));
        }
    }

    public static Conditional CONTAINS_RED = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = -5432334135730478673L;

        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof RedBion);
        }
    };

    public static Conditional CONTAINS_ORANGE = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = 4038640760712646360L;

        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof OrangeBion);
        }
    };

    public static Conditional CONTAINS_BLUE = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = -4970186986677816099L;

        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof BlueBion);
        }
    };

    class MoveTowardConditionRule extends Rule {

        /**
         * 
         */
        private static final long serialVersionUID = -788828565514865207L;
        String name;
        Conditional condition;

        public MoveTowardConditionRule(String name, Conditional condition) {
            super(name);
            this.name = name;
            this.condition = condition;
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findNearest(condition, false, ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveToward(target);
            }
        }
    };

    class MoveAwayConditionRule extends MoveTowardConditionRule {

        /**
         * 
         */
        private static final long serialVersionUID = 774448964046194005L;

        public MoveAwayConditionRule(String name, Conditional condition) {
            super(name, condition);
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findNearest(condition, false, ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveAway(target);
            }
        }
    };

    public final Rule MOVE_TOWARD_RED_RULE = new MoveTowardConditionRule("Move Toward Red", CONTAINS_RED);

    public final Rule MOVE_TOWARD_ORANGE_RULE = new MoveTowardConditionRule("Move Toward Orange", CONTAINS_ORANGE);

    public final Rule MOVE_TOWARD_BLUE_RULE = new MoveTowardConditionRule("Move Toward Blue", CONTAINS_BLUE);

    public final Rule MOVE_AWAY_RED_RULE = new MoveAwayConditionRule("Move Away Red", CONTAINS_RED);

    public final Rule MOVE_AWAY_ORANGE_RULE = new MoveAwayConditionRule("Move Away Orange", CONTAINS_ORANGE);

    public final Rule MOVE_AWAY_BLUE_RULE = new MoveAwayConditionRule("Move Away Blue", CONTAINS_BLUE);

    class MoveTowardMaximumRule extends Rule {

        /**
         * 
         */
        private static final long serialVersionUID = 4330599035034725642L;
        String name;
        DataPoint point;

        public MoveTowardMaximumRule(String name, DataPoint point) {
            super(name);
            this.name = name;
            this.point = point;
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findMaximumWithin(point, true, ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveToward(target);
            }
        }
    }

    class MoveAwayMaximumRule extends MoveTowardMaximumRule {

        /**
         * 
         */
        private static final long serialVersionUID = 2856925969425309150L;

        public MoveAwayMaximumRule(String name, DataPoint point) {
            super(name, point);
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findMaximumWithin(point, true, ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveAway(target);
            }
        }
    }

    public final Rule MOVE_TOWARD_RED_ENERGY_RULE = new MoveTowardMaximumRule("Move Toward Red Energy", REDNESS);

    public final Rule MOVE_TOWARD_ORANGE_ENERGY_RULE = new MoveTowardMaximumRule("Move Toward Orange Energy", ORANGENESS);

    public final Rule MOVE_TOWARD_BLUE_ENERGY_RULE = new MoveTowardMaximumRule("Move Toward Blue Energy", BLUENESS);

    public final Rule MOVE_AWAY_RED_ENERGY_RULE = new MoveAwayMaximumRule("Move Away Red Energy", REDNESS);

    public final Rule MOVE_AWAY_ORANGE_ENERGY_RULE = new MoveAwayMaximumRule("Move Away Orange Energy", ORANGENESS);

    public final Rule MOVE_AWAY_BLUE_ENERGY_RULE = new MoveAwayMaximumRule("Move Away Blue Energy", BLUENESS);

    public final static Rule LEAVE_ENERGY_RULE = new Rule("Leave Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = -6282573031997509178L;

        public void execute(Agent a) {
            ((Bion) a).leaveEnergy();
        }
    };

    public final static Rule CONSUME_RED_ENERGY_RULE = new Rule("Consume Red Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = -8205275214840369886L;

        public void execute(Agent a) {
            ((Bion) a).consumeRedEnergy();
        }
    };

    public final static Rule CONSUME_ORANGE_ENERGY_RULE = new Rule("Consume Orange Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = 7919053441973303875L;

        public void execute(Agent a) {
            ((Bion) a).consumeOrangeEnergy();
        }
    };

    public final static Rule CONSUME_BLUE_ENERGY_RULE = new Rule("Consume Blue Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = 6093071952416840447L;

        public void execute(Agent a) {
            ((Bion) a).consumeBlueEnergy();
        }
    };

    public void createScape() {
        super.createScape();
        territory = new Scape(new Array2DMoore());
        territory.setName("Bionland");
        territory.setPrototypeAgent(new BionTile());
        ((Array2D) territory.getSpace()).setExtent(25, 25);
        add(territory);
        territory.setExecutionOrder(RULE_ORDER);
        territory.addRule(diffuse_red_energy);
        territory.addRule(diffuse_orange_energy);
        territory.addRule(diffuse_blue_energy);
        redBions = new Scape();
        createBions(redBions, new RedBion(), "Red");
        redBions.getRules().setSelected(MOVE_AWAY_BLUE_ENERGY_RULE, true);
        redBions.getRules().setSelected(MOVE_TOWARD_ORANGE_ENERGY_RULE, true);
        redBions.getRules().setSelected(CONSUME_BLUE_ENERGY_RULE, true);
        redBions.getRules().setSelected(CONSUME_ORANGE_ENERGY_RULE, true);
        orangeBions = new Scape();
        createBions(orangeBions, new OrangeBion(), "Orange");
        orangeBions.getRules().setSelected(MOVE_AWAY_RED_ENERGY_RULE, true);
        orangeBions.getRules().setSelected(MOVE_TOWARD_BLUE_ENERGY_RULE, true);
        orangeBions.getRules().setSelected(CONSUME_RED_ENERGY_RULE, true);
        orangeBions.getRules().setSelected(CONSUME_BLUE_ENERGY_RULE, true);
        blueBions = new Scape();
        createBions(blueBions, new BlueBion(), "Blue");
        blueBions.getRules().setSelected(MOVE_AWAY_ORANGE_ENERGY_RULE, true);
        blueBions.getRules().setSelected(MOVE_TOWARD_RED_ENERGY_RULE, true);
        blueBions.getRules().setSelected(CONSUME_RED_ENERGY_RULE, true);
        blueBions.getRules().setSelected(CONSUME_ORANGE_ENERGY_RULE, true);
    }

    public void scapeSetup(ScapeEvent scapeEvent) {
        int popSize = (int) ((territory.getSize() * initialDensity) / 3.0f);
        redBions.setExtent(popSize);
        orangeBions.setExtent(popSize);
        blueBions.setExtent(popSize);
    }

    protected void createBions(Scape bions, CellOccupant protoCell, String name) {
        bions.setName(name);
        protoCell.setHostScape(territory);
        bions.setPrototypeAgent(protoCell);
        bions.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        bions.addRule(RANDOM_WALK_RULE);
        bions.addRule(MOVE_TOWARD_RED_ENERGY_RULE, false);
        bions.addRule(MOVE_TOWARD_ORANGE_ENERGY_RULE, false);
        bions.addRule(MOVE_TOWARD_BLUE_ENERGY_RULE, false);
        bions.addRule(MOVE_AWAY_RED_ENERGY_RULE, false);
        bions.addRule(MOVE_AWAY_ORANGE_ENERGY_RULE, false);
        bions.addRule(MOVE_AWAY_BLUE_ENERGY_RULE, false);
        bions.addRule(MOVE_TOWARD_RED_RULE, false);
        bions.addRule(MOVE_TOWARD_ORANGE_RULE, false);
        bions.addRule(MOVE_TOWARD_BLUE_RULE, false);
        bions.addRule(MOVE_AWAY_RED_RULE, false);
        bions.addRule(MOVE_AWAY_ORANGE_RULE, false);
        bions.addRule(MOVE_AWAY_BLUE_RULE, false);
        bions.addRule(CONSUME_RED_ENERGY_RULE, false);
        bions.addRule(CONSUME_ORANGE_ENERGY_RULE, false);
        bions.addRule(CONSUME_BLUE_ENERGY_RULE, false);
        bions.addRule(LEAVE_ENERGY_RULE);
        bions.addRule(FISSIONING_RULE);
        bions.addRule(DEATH_RULE);
        add(bions);
        bions.addStatCollector(new StatCollectorCSAMM(name + " Stored Energy") {
            /**
             * 
             */
            private static final long serialVersionUID = 8836308946722098459L;

            public double getValue(Object o) {
                return ((Bion) o).getStoredEnergy();
            }
        });
    }

    public void createViews() {
        super.createViews();
        DrawFeature drawTest = new DrawFeature("Draw Test") {
            /**
             * 
             */
            private static final long serialVersionUID = -4346248335063348922L;

            public void draw(Graphics g, Object object, int width, int height) {
                g.setColor(Color.black);
                g.fillRect(0, 0, width, height);
                g.setColor(((Agent) object).getColor());
                g.fillRect(0, 0, width - 1, height - 1);
                Bion b = (Bion) ((HostCell) object).getOccupant();
                if (b != null) {
                    g.setColor(((Cell) ((HostCell) object).getOccupant()).getColor());
                    if (b.getStoredEnergy() > 1.7) {
                        g.drawOval(0, 0, width - 2, height - 2);
                    } else if (b.getStoredEnergy() > 1.2) {
                        g.drawOval(0, 0, width - 3, height - 3);
                    } else if (b.getStoredEnergy() > .7) {
                        g.drawOval(0, 0, width - 4, height - 4);
                    } else if (b.getStoredEnergy() > .4) {
                        g.drawOval(0, 0, width - 5, height - 5);
                    } else {
                        g.drawOval(0, 0, width - 6, height - 6);
                    }
                }
            }
        };
        territory.addDrawFeature(drawTest);

        Overhead2DView mapView = new Overhead2DView("Map");
        mapView.setCellSize(15);
        territory.addView(mapView);

        mapView.getDrawSelection().clearSelection();
        mapView.getDrawSelection().setSelected("Draw Test", true);

        /*ChartView chart = new ChartView("Population");
        addView(chart);
        chart.addSeries("Count Red Stored Energy", Color.red);
        chart.addSeries("Count Orange Stored Energy", Color.orange);
        chart.addSeries("Count Blue Stored Energy", Color.blue);*/

        AutoCustomizerSwing customizer;
        final JPanel customPanel = new JPanel();
        GridBagConstraints cgbc;

        customizer = new AutoCustomizerSwing() {
            public void build() {
                super.build();
                getTabPane().addTab("Sliders", DesktopEnvironment.getIcon("FingerUp"), customPanel, "Change Values Using Sliders");
                getTabPane().setSelectedComponent(customPanel);
            }
        };
        getUIEnvironment().setCustomizer(customizer);
        customizer.setPreferredSize(new Dimension(900, 480));
        GridBagLayout cgbl = new GridBagLayout();
        customPanel.setLayout(cgbl);
        cgbc = cgbl.getConstraints(customPanel);
        customPanel.removeAll();
        cgbc.gridwidth = 1;
        cgbc.fill = GridBagConstraints.HORIZONTAL;
        cgbc.insets = new Insets(3, 6, 3, 6);
        cgbc.anchor = GridBagConstraints.NORTH;
        cgbc.weightx = 1.0;
        cgbc.weighty = 1.0;
        cgbc.gridy = 0;
        cgbc.gridx = 0;

        CustomSliderPanel diffusionConstantSlider = new CustomSliderPanel("Diffusion Constant", 0.0, 1.0, 2) {
            /**
             * 
             */
            private static final long serialVersionUID = -8655774249043160976L;

            public double getActualValue() {
                //usage of specific model version is a bit awkward, but neccessitated by tutorial format
                return Model_M.this.getDiffusionConstant();
            }

            public void setActualValue(double value) {
                Model_M.this.setDiffusionConstant(value);
            }
        };
        customPanel.add(diffusionConstantSlider, cgbc);
        diffusionConstantSlider.build();
        cgbc.gridy++;

        CustomSliderPanel evaporationRateSlider = new CustomSliderPanel("Evaporation Rate", 0.0, 0.2, 3) {
            /**
             * 
             */
            private static final long serialVersionUID = -8783009221567339358L;

            public double getActualValue() {
                return Model_M.this.getEvaporationRate();
            }

            public void setActualValue(double value) {
                Model_M.this.setEvaporationRate(value);
            }
        };
        customPanel.add(evaporationRateSlider, cgbc);
        evaporationRateSlider.build();
        cgbc.gridy++;

        CustomSliderPanel energyGainSlider = new CustomSliderPanel("Bion Energy gain", 0.8f, 3.0f, 3) {
            /**
             * 
             */
            private static final long serialVersionUID = -4550671915216175436L;

            public double getActualValue() {
                return Model_M.this.getBionEnergyGain();
            }

            public void setActualValue(double value) {
                Model_M.this.setBionEnergyGain(value);
            }
        };
        customPanel.add(energyGainSlider, cgbc);
        energyGainSlider.build();
        cgbc.gridy++;
    }

    public double getInitialPopulationDensity() {
        return initialDensity;
    }

    public void setInitialPopulationDensity(double initialDensity) {
        this.initialDensity = initialDensity;
    }

    public void setBionEnergyGain(double bionEnergyGain) {
        this.bionEnergyGain = bionEnergyGain;
    }

    public double getBionEnergyGain() {
        return bionEnergyGain;
    }

    public double getBionEnergyLeakage() {
        return bionEnergyLeakage;
    }

    public void setBionEnergyLeakage(double bionEnergyLeakage) {
        this.bionEnergyLeakage = bionEnergyLeakage;
    }

    public double getBionEnergyConsumption() {
        return bionEnergyConsumption;
    }

    public void setBionEnergyConsumption(double bionEnergyConsumption) {
        this.bionEnergyConsumption = bionEnergyConsumption;
    }

    public double getInitialStoredEnergy() {
        return initialStoredEnergy;
    }

    public void setInitialStoredEnergy(double initialStoredEnergy) {
        this.initialStoredEnergy = initialStoredEnergy;
    }

    public int getMinimumVision() {
        return minVision;
    }

    public void setMinimumVision(int minVision) {
        this.minVision = minVision;
    }

    public int getMaximumVision() {
        return maxVision;
    }

    public void setMaximumVision(int maxVision) {
        this.maxVision = maxVision;
    }

    public double getDiffusionConstant() {
        //red stands in for all diffusion..
        return diffuse_red_energy.getDiffusionConstant();
    }

    public void setDiffusionConstant(double diffuseConstant) {
        diffuse_red_energy.setDiffusionConstant(diffuseConstant);
        diffuse_orange_energy.setDiffusionConstant(diffuseConstant);
        diffuse_blue_energy.setDiffusionConstant(diffuseConstant);
    }

    public double getEvaporationRate() {
        return diffuse_red_energy.getEvaporationRate();
    }

    public void setEvaporationRate(double evaporationRate) {
        diffuse_red_energy.setEvaporationRate(evaporationRate);
        diffuse_orange_energy.setEvaporationRate(evaporationRate);
        diffuse_blue_energy.setEvaporationRate(evaporationRate);
    }
}
