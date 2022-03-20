/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.pd;

import java.awt.Color;

import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2DVonNeumann;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.data.StatCollectorCond;
import org.ascape.util.data.StatCollectorCondCSAMM;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.Overhead2DView;

public class PD2DDemo extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = 1756086256638619391L;

    /**
     * The cooperation strategy symbol.
     */
    public static final int COOPERATE = 0;

    /**
     * The defection strategy symbol.
     */
    public static final int DEFECT = 1;

    private Scape lattice;

    private Scape agents;

    private Overhead2DView overheadView;

    public void createScape() {
        super.createScape();
        //Create a 2D lattice (we'll be using von Neumann Geometry.)
        lattice = new Scape(new Array2DVonNeumann());
        //Use the generic host cell as the agent that will fill the lattice.
        lattice.setPrototypeAgent(new HostCell());
        //Important: Clear rules so the default iterate rule won't be used for
        //the host cell. The model will run faster, since iterate won't be called
        //on each lattice cell.
        lattice.getRules().clear();
        //Set the size by setting the coordinate at the lattices largest extent.
        lattice.setExtent(new Coordinate2DDiscrete(latticeWidth, latticeHeight));
        //Set the lattice to expect cells to request updates only when neccesary.
        //This greatly enhances perfomance, but requires us to manually request view
        //updates if a change occurs to a lattice cell. In this case, the only change
        //that might occur to a cell is for an agent to move in or out of it, because
        //an agents strategy does not change, and that is the only aspect of agent
        //state that the view reflects. We do not need to request updates on agent
        //movement, since that is handled automatically by the framework.
        //lattice.setCellsRequestUpdates(true);

        //Create an instance of Player that we can set state for...
        Player player = new Player();
        //Set the host scape for the prototype agent,
        //So that all 'agents' members know where they live.
        player.setHostScape(lattice);
        //Create a scape for the agents (players) that will live on the lattice.
        //Vectors are appropriate whenever the size of a population will change.
        agents = new Scape();
        //Give the scape a short, descriptive name
        agents.setName("Players");
        //...and make the player the prototype that will be used to fill the initial scape.
        agents.setPrototypeAgent(player);
        //Tell the agents to execute in by rule order; each rule executes on every agent
        //before the next rule is executed.
        agents.setExecutionOrder(Scape.RULE_ORDER);

        //Finally, we need to add our vectors to the root we created at the beginning.
        //The root scape is the parent and controls its member scapes.
        add(lattice);
        add(agents);
    }

    public void createViews() {
        super.createViews();
        //This is a little more complex; here we are create anonymous
        //classes that are used to gather statistics from the running model.
        //You just need to pick the appropriate class for the statistics
        //you want to gather, and then subclass it, providing methods to
        //evaluate whether an agent should be included in a particular
        //statistic (if you are using a condition class), and/or get some value.
        final StatCollector[] stats = {
            new StatCollectorCond("Cooperate") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 5727339695903190034L;

                public boolean meetsCondition(Object object) {
                    return (((Player) object).strategy == PD2D.COOPERATE);
                }
            },
            new StatCollectorCond("Defect") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 6238817052413774650L;

                public boolean meetsCondition(Object object) {
                    return (((Player) object).strategy == PD2D.DEFECT);
                }
            },
            new StatCollectorCondCSAMM("Defect Wealth") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 4744520173387871334L;

                public boolean meetsCondition(Object object) {
                    return (((Player) object).strategy == PD2D.DEFECT);
                }

                public double getValue(Object object) {
                    return ((Player) object).getWealth();
                }
            },
            new StatCollectorCondCSAMM("Cooperate Wealth") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -2364422563246662316L;

                public boolean meetsCondition(Object object) {
                    return (((Player) object).strategy == PD2D.COOPERATE);
                }

                public double getValue(Object object) {
                    return ((Player) object).getWealth();
                }
            },
            new StatCollectorCSAMM("Overall Wealth") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -7758387941606861788L;

                public double getValue(Object object) {
                    return ((Player) object).getWealth();
                }
            }
        };
//Add the new values stats to agents
        agents.addStatCollectors(stats);

        if (!(((DesktopEnvironment) getUIEnvironment()).isInApplet())) {
            //Create a new chart
            ChartView chart = new ChartView();
            //Add the chart view
            agents.addView(chart);
            //And add some of the stat series we've just created to it
            chart.addSeries("Count Cooperate", Color.blue);
            chart.addSeries("Count Defect", Color.red);
            //Set starting data point we're interested in
            chart.setDisplayPoints(100);
        }

//Now, add a simple overhead view so that we can view the lattice:
        overheadView = new Overhead2DView();
//Set its cell size to 12
        overheadView.setCellSize(12);
//add it to the lattice so that it can view and be controlled by it
        lattice.addView(overheadView);
    }

    public void scapeSetup(ScapeEvent scapeEvent) {
        //Set the extent of the scape, which is simply the number of agents we want.
        agents.setExtent(new Coordinate1DDiscrete(nAgents));
    }

    private int nAgents = 100;

    private int latticeWidth = 30;

    private int latticeHeight = 30;

    public int getNumberOfAgents() {
        return nAgents;
    }

    public void setNumberOfAgents(int _nAgents) {
        nAgents = _nAgents;
    }

    /*public int getLatticeWidth() {
        return latticeWidth;
    }

    public void setLatticeWidth(int _latticeWidth) {
        latticeWidth = _latticeWidth;
    }

    public int getLatticeHeight() {
        return latticeHeight;
    }

    public void setLatticeHeight(int _latticeHeight) {
        latticeHeight = _latticeHeight;
    }*/

    /**
     * The payoff when both scape cooperate.
     */
    private int payoff_C_C = 2;

    /**
     * The payoff when the agent defects and the other agent cooperates.
     */
    private int payoff_D_C = 6;

    /**
     * The payoff when an agent cooperates and the other agent defects. (Sucker!)
     */
    private int payoff_C_D = -6;

    /**
     * The payoff when both scape defect.
     */
    private int payoff_D_D = -5;

    /**
     * The wealth level at which an agent can fission.
     */
    private int fissionWealth = 11;

    /**
     * The wealth that is passed from parent to child.
     */
    private int inheiritedWealth = 6;

    /**
     * The wealth that is passed from parent to child.
     */
    private int initialWealth = 6;

    /**
     * The cahnce that strategy will not be passed from parent to child.
     */
    private double mutationRate = .1;

    /**
     * Agents die at death age.
     */
    private boolean dieAtDeathAge = true;

    /**
     * The age at which the agent dies.
     */
    private int deathAge = 100;

    public int getFissionWealth() {
        return fissionWealth;
    }

    public void setFissionWealth(int _fissionWealth) {
        fissionWealth = _fissionWealth;
    }

    public int getInitialWealth() {
        return initialWealth;
    }

    public void setInitialWealth(int _initialWealth) {
        initialWealth = _initialWealth;
    }

    public int getInheritedWealth() {
        return inheiritedWealth;
    }

    public void setInheiritedWealth(int _inheiritedWealth) {
        inheiritedWealth = _inheiritedWealth;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(double _mutationRate) {
        mutationRate = _mutationRate;
    }

    public int getDeathAge() {
        return deathAge;
    }

    public void setDeathAge(int _deathAge) {
        deathAge = _deathAge;
    }

    public boolean isDieAtDeathAge() {
        return dieAtDeathAge;
    }

    public void setDieAtDeathAge(boolean _dieAtDeathAge) {
        dieAtDeathAge = _dieAtDeathAge;
    }

    public int getPayoff_C_C() {
        return payoff_C_C;
    }

    public void setPayoff_C_C(int _payoff_C_C) {
        payoff_C_C = _payoff_C_C;
    }

    public int getPayoff_C_D() {
        return payoff_C_D;
    }

    public void setPayoff_C_D(int _payoff_C_D) {
        payoff_C_D = _payoff_C_D;
    }

    public int getPayoff_D_C() {
        return payoff_D_C;
    }

    public void setPayoff_D_C(int _payoff_D_C) {
        payoff_D_C = _payoff_D_C;
    }

    public int getPayoff_D_D() {
        return payoff_D_D;
    }

    public void setPayoff_D_D(int _payoff_D_D) {
        payoff_D_D = _payoff_D_D;
    }
}
