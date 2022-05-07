package edu.brook.aa.log;

import org.ascape.model.Scape;
import org.ascape.util.data.StatCollector;

public class LoggingStatCollector extends StatCollector {
    private int myIndex;
    private Scape myScape;

    public LoggingStatCollector(String name, int myIndex, Scape myScape) {
        super(name);
        this.myIndex = myIndex;
        this.myScape = myScape;
    }

    @Override
    public void addValue(double count) {
        super.addValue(count);
        StatLogger.INSTANCE.log(myScape.getPeriod(), myIndex, count);

    }
}
