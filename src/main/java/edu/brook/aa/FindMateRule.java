/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package edu.brook.aa;

import org.ascape.model.Agent;
import org.ascape.model.rule.Rule;

public class FindMateRule extends Rule {

    private static final long serialVersionUID = 6567466917857780539L;
    public Person candidate;
    public Person lastMate;

    public FindMateRule(Person candidate) {
        super("Find Mate");
        this.candidate = candidate;
    }

    public void clear() {
        lastMate = null;
    }

    public void execute(Agent a) {
        Person p = (Person) a;
        boolean isSingle = p.getMate() == null;
        boolean isMale = p.getSex() == Person.MALE;
        boolean isOldEnough = p.getAge() > LHV.minFertilityAge;
        boolean differentHousehold = p.getHousehold() != candidate.getHousehold();

        if (lastMate == null) {
            if ((p.getMate() == null) &&
                    (p.getSex() == Person.MALE) &&
                    (p.getAge() > LHV.minFertilityAge) &&
                    (p.getHousehold() != candidate.getHousehold())) {
                lastMate = p;
            }
        }
    }

    public Person getMate() {
        return lastMate;
    }
}
