/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package edu.brook.aa;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Singleton;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A list that keeps itself sorted.
 */
// TODO: LSP violation
class Locations extends ArrayList {

    private static final long serialVersionUID = 5450029191071931492L;
    private boolean needsSort = true;

    public boolean add(Object o) {
        needsSort = true;
        return super.add(o);
    }

    public void checkSort() {
        if (needsSort) {
            needsSort = false;
            Collections.sort(this);
        }
    }

    public final Object get(int index) {
        checkSort();
        return super.get(index);
    }

    public boolean remove(Object o) {
        needsSort = true;
        return super.remove(o);
    }

    public final Object remove(int index) {
        checkSort();
        return super.remove(index);
    }

    /*public Object removeFirst() {
        checkSort();
	    return super.removeFirst();
    }

    public Object getFirst() {
        checkSort();
	    return super.getFirst();
    }

    public Object removeLast() {
        checkSort();
	    return super.removeLast();
    }

    public Object getLast()  {
        checkSort();
	    return super.getLast();
    }*/
}

public class YieldZone extends Scape {


    private static final long serialVersionUID = 6889645892625033089L;
    protected Color color;
    private EnvironmentZone environmentZone;
    private MaizeZone maizeZone;
    private Locations locations;
    private int yield;

    public YieldZone(String name, Color color, EnvironmentZone environmentZone, MaizeZone maizeZone) {
        super(new Singleton());
        this.environmentZone = environmentZone;
        this.maizeZone = maizeZone;
        setName(name);
        this.color = color;
        locations = new Locations();
    }

    public void calculateYield() {
        double apdsi = environmentZone.getAPDSI();
        if ((maizeZone == LHV.MAIZE_NO_YIELD) || (maizeZone == LHV.MAIZE_EMPTY)) {
            yield = 0;
        } else if (maizeZone == LHV.MAIZE_YIELD_1) {
            if (apdsi >= 3.0) {
                yield = 1153;
            } else if (apdsi >= 1.0) {
                yield = 988;
            } else if (apdsi > -1.0) {
                yield = 821;
            } else if (apdsi > -3.0) {
                yield = 719;
            } else {
                yield = 617;
            }
        } else if (maizeZone == LHV.MAIZE_YIELD_2) {
            if (apdsi >= 3.0) {
                yield = 961;
            } else if (apdsi >= 1.0) {
                yield = 824;
            } else if (apdsi > -1.0) {
                yield = 684;
            } else if (apdsi > -3.0) {
                yield = 599;
            } else {
                yield = 514;
            }
        } else if (maizeZone == LHV.MAIZE_YIELD_3) {
            if (apdsi >= 3.0) {
                yield = 769;
            } else if (apdsi >= 1.0) {
                yield = 659;
            } else if (apdsi > -1.0) {
                yield = 547;
            } else if (apdsi > -3.0) {
                yield = 479;
            } else {
                yield = 411;
            }
        } else if (maizeZone == LHV.MAIZE_SAND_DUNE) {
            if (apdsi >= 3.0) {
                yield = 1201;
            } else if (apdsi >= 1.0) {
                yield = 1030;
            } else if (apdsi > -1.0) {
                yield = 855;
            } else if (apdsi > -3.0) {
                yield = 749;
            } else {
                yield = 642;
            }
        } else {
            throw new RuntimeException("Bad data or logic in Location#getYield");
        }
        //yield = (int) (yield * getFrostYieldFactor());
    }

    public Locations getAvailableLocations() {
        return locations;
    }

    public Color getColor() {
        return color;
    }

    public MaizeZone getMaizeZone() {
        return maizeZone;
    }

    public int getYield() {
        return yield;
    }

    public void initialize() {
        locations.clear();
    }

    public void scapeCreated() {
        scape.getRules().clear();
        Rule calculateYield = new Rule("Calculate Yield") {

            private static final long serialVersionUID = -6031208102620304399L;

            public void execute(Agent agent) {
                ((YieldZone) agent).calculateYield();
            }
            //public boolean isRandomExecution() {
            //return false;
            //}
        };
        scape.addInitialRule(calculateYield);
        scape.addRule(calculateYield);
    }
}
