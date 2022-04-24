package edu.brook.aa.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO Singleton
public enum StatLogger {
    INSTANCE;

    private LocalDateTime now = LocalDateTime.now();

    private Map<Integer, int[]> popMap = new HashMap<>();

    public void close() {
        try {
            String formattedNow = now.format(DateTimeFormatter.ISO_DATE_TIME).replaceAll(":", "-");

            File file = new File("C:\\Users\\moogi\\Documents\\data-weka\\anasazi\\anasazi-stats" +
                    formattedNow + ".csv");

            PrintWriter data = new PrintWriter(new FileWriter(file, true), true);

//            data.println("Year,Hist,RB,ML,ErrorRB^2,ErrorML%2");

            List<Integer> keys = popMap.keySet()
                    .stream().sorted()
                    .collect(Collectors.toList());

            for (Integer key : keys) {
                int[] pops = popMap.get(key);
                data.println(String.format("%d,%d,%d,%d,%d,%d",
                        key, pops[0], pops[1], pops[2],
                        (int) Math.pow(pops[1] - pops[0], 2),
                        (int) Math.pow(pops[2] - pops[0], 2)));
            }

            data.flush();
            data.close();
        } catch (IOException e) {
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
        popMap.clear();
    }
}
