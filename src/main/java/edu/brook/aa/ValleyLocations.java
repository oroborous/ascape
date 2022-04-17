package edu.brook.aa;

import org.ascape.model.Scape;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate2DDiscrete;

public class ValleyLocations extends Scape {
    public ValleyLocations() {
        super(new Array2DMoore());
        setName("Locations");
        setPrototypeAgent(new Location());
        setExtent(new Coordinate2DDiscrete(80, 120));
        getRules().clear();
        setAutoCreate(false);
    }

    @Override
    public void initialize() {
        super.initialize();
        System.out.println("Init Valley");
    }
}
