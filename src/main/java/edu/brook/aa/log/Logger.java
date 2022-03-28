package edu.brook.aa.log;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public enum Logger {
    INSTANCE;

    private PrintWriter writer;
    private boolean isClosed = false;


    Logger() {
        try {
            writer = new PrintWriter("settlements.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void log(HouseholdEvent event) {
        if (!isClosed)
            writer.println(event.toString());
    }


    public void close() {
        isClosed = true;
        writer.flush();
        writer.close();
    }


}
