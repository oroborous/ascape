package edu.brook.aa;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;

import java.awt.*;
import java.util.Collections;

import static edu.brook.aa.LHV.*;

public class YieldZones extends Scape {
    public YieldZone YIELD_EMPTY = new YieldZone("Empty", Color.white, ENVIRON_EMPTY, MAIZE_EMPTY);

    public YieldZone YIELD_GENERAL_VALLEY = new YieldZone("General Valley Floor", Color.black, ENVIRON_GENERAL_VALLEY, MAIZE_YIELD_2);

    public YieldZone YIELD_NORTH_SAND_DUNE = new YieldZone("North Valley Dunes", Color.white, ENVIRON_NORTH_VALLEY, MAIZE_SAND_DUNE);

    public YieldZone YIELD_NORTH_VALLEY = new YieldZone("North Valley Floor", Color.red, ENVIRON_NORTH_VALLEY, MAIZE_YIELD_1);

    public YieldZone YIELD_MID_SAND_DUNE = new YieldZone("Mid Valley Dunes", Color.white, ENVIRON_MID_VALLEY, MAIZE_SAND_DUNE);

    public YieldZone YIELD_MID_VALLEY_WEST = new YieldZone("West Mid-Valley Floor", Color.gray, ENVIRON_MID_VALLEY, MAIZE_YIELD_1);

    public YieldZone YIELD_MID_VALLEY_EAST = new YieldZone("East Mid-Valley Floor", Color.green, ENVIRON_MID_VALLEY, MAIZE_YIELD_2);

    public YieldZone YIELD_UPLANDS_NATURAL = new YieldZone("Uplands Natural", Color.yellow, ENVIRON_UPLANDS_NATURAL, MAIZE_NO_YIELD);

    public YieldZone YIELD_UPLANDS_ARABLE = new YieldZone("Uplands Arable", Color.blue, ENVIRON_UPLANDS_ARABLE, MAIZE_YIELD_3);

    public YieldZone YIELD_KINBIKO_CANYON = new YieldZone("Kinbiko Canyon", Color.pink, ENVIRON_KINBIKO_CANYON, MAIZE_YIELD_1);

    public void createScape() {
        setName("Yield Zones");
        setPrototypeAgent(YIELD_EMPTY);
        add(YIELD_EMPTY);
        add(YIELD_GENERAL_VALLEY);
        add(YIELD_NORTH_SAND_DUNE);
        add(YIELD_NORTH_VALLEY);
        add(YIELD_MID_SAND_DUNE);
        add(YIELD_MID_VALLEY_WEST);
        add(YIELD_MID_VALLEY_EAST);
        add(YIELD_UPLANDS_NATURAL);
        add(YIELD_UPLANDS_ARABLE);
        add(YIELD_KINBIKO_CANYON);
        setAutoCreate(false);
        //We sort all at once to avoid sorting penalties per addition
        addInitialRule(new Rule("Sort Available Locations") {
            private static final long serialVersionUID = 8923085455603538447L;

            public void execute(Agent agent) {
                Collections.sort(((YieldZone) agent).getAvailableLocations());
            }
        });
    }
}
