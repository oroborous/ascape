package edu.brook.aa.log;

import edu.brook.aa.HistoricSettlement;
import org.ascape.model.Scape;
import org.ascape.util.data.StatCollectorCondCSA;

public class LoggingStatCollectorCondCSA extends StatCollectorCondCSA {

    private int myIndex;
    private Scape myScape;

    public LoggingStatCollectorCondCSA(String name, int myIndex, Scape myScape) {
        super(name);
        this.myIndex = myIndex;
        this.myScape = myScape;
    }

    @Override
    public void addValue(double value) {
        super.addValue(value);
        StatLogger.INSTANCE.log(myScape.getPeriod(), myIndex, value);
    }

    @Override
    public double getValue(Object object) {
        return ((HistoricSettlement) object).getHouseholdCount();
    }

    @Override
    public boolean meetsCondition(Object object) {
        return ((HistoricSettlement) object).isExtant();
    }
}
