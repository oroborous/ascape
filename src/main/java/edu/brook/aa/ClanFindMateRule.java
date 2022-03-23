/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package edu.brook.aa;

import org.ascape.model.Agent;

public class ClanFindMateRule extends FindMateRule {

    private static final long serialVersionUID = 938548733973182435L;

    public ClanFindMateRule(Person candidate) {
        super(candidate);
    }

    public void execute(Agent a) {
        Person p = (Person) a;
        boolean isSingle = p.getMate() == null;
        boolean isMale = p.getSex() == Person.MALE;
        boolean isOldEnough = p.getAge() > ((LHV) a.getRoot()).getMinFertilityAge();
        boolean differentClan = p.getHousehold().getClan() != candidate.getHousehold().getClan();
        boolean differentHousehold = p.getHousehold() != candidate.getHousehold();

        Logger.INSTANCE.log(getScape().getPeriod(), p.getHousehold().id,
                String.format("[ClanFindMateRule: single=%b, male=%b, oldEnough=%b, diffClan=%b, diffHhold=%b]",
                isSingle, isMale, isOldEnough, differentClan, differentHousehold));

        if ((p.getMate() == null) &&
                (p.getSex() == Person.MALE) &&
                (p.getAge() > ((LHV) a.getRoot()).getMinFertilityAge()) &&
                (p.getHousehold().getClan() != candidate.getHousehold().getClan()) &&
                (p.getHousehold() != candidate.getHousehold())) {
            lastMate = p;
        }
    }
}

;
