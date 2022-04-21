package edu.brook.aa.log;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum StatLogger {
    INSTANCE;

    private Map<Integer, int[]> popMap = new HashMap<>();

    public void close() {
        try {
            LocalDateTime now = LocalDateTime.now();
            String formattedNow = now.format(DateTimeFormatter.ISO_DATE_TIME).replaceAll(":", "-");
            PrintWriter data = new PrintWriter("C:\\Users\\moogi\\Documents\\data-weka\\anasazi\\anasazi-stats" +
                    formattedNow + ".log");

            data.println("Year,Hist,RB,ML");

            List<Integer> keys = popMap.keySet()
                    .stream().sorted()
                    .collect(Collectors.toList());

            for (Integer key : keys) {
                int[] pops = popMap.get(key);
                data.println(String.format("%d,%d,%d,%d", key, pops[0], pops[1], pops[2]));
            }

            data.flush();
            data.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void log(int year, int index, double count) {
        if (!popMap.containsKey(year)) {
            popMap.put(year, new int[3]);
        }

        popMap.get(year)[index] += count == 0 ? 1 : count;
    }

    public void open() {

    }
}
