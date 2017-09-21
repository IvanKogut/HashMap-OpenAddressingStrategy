package ua.kyiv.kpi.fpm.km32.kohut.editedRealization.entity;

import com.google.common.collect.*;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Locale;
import java.util.Set;
import java.time.Instant;
import java.time.Duration;

public class HashMapTest {

    private static final int ATTEMPT_NUMBER = 10;
    private static int[] elementsNumber = {100_000, 500_000, 1_000_000};

    private static Table<String, Integer, List<Double>> valuesPut = Tables.synchronizedTable(HashBasedTable.create());
    private static Table<String, Integer, List<Double>> valuesGet = Tables.synchronizedTable(HashBasedTable.create());

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Wait for result...");

        Thread threadForStandardMap = null;
        Thread threadForCustomMap = null;

        for (int elementsNumberValue : elementsNumber) {

            Set<Integer> set = new HashSet<>();
            for (int i = 0; i < elementsNumberValue; i++) {
                set.add((int) ((Integer.MAX_VALUE + Short.MAX_VALUE) * Math.random() - Short.MAX_VALUE));
            }
            
            threadForStandardMap = new Thread(new Task(set, elementsNumberValue, "JavaHashMap"));
            threadForStandardMap.start();

            threadForCustomMap = new Thread(new Task(set, elementsNumberValue, "HashMapOpenAddressing"));
            threadForCustomMap.start(); 
        }

        if (threadForStandardMap != null) {
            threadForStandardMap.join();
        }

        if (threadForCustomMap != null) {
            threadForCustomMap.join();
        }

        printResult(valuesPut, "PUT");        
        System.out.println("------------------------------------------");
        printResult(valuesGet, "GET");
    }

    private static void printResult(Table<String, Integer, List<Double>> table, String operation) {

        System.out.println(operation);

        for (Map.Entry<String,Map<Integer, List<Double>>> entry : table.rowMap().entrySet()) {
            System.out.println("For " + entry.getKey());
            StringBuilder resultElements = new StringBuilder("Elements(~):");
            StringBuilder resultTimes = new StringBuilder(String.format("%" + resultElements.length() + "s", "Time (avg):"));
            for (Map.Entry<Integer,List<Double>> entry2 : entry.getValue().entrySet()) {
                resultElements.append(String.format(Locale.US, "%,10d", entry2.getKey()).replace(",", ""));
                resultTimes.append(String.format("%10.5f", entry2.getValue().stream().mapToDouble(Double::doubleValue).sum() / ATTEMPT_NUMBER));
            }
            System.out.println(resultElements);
            System.out.println(resultTimes);
        }
    }

    static class Task implements Runnable {

        private Set<Integer> set;
        private int actualCapacity;
        private String mapRealization;

        Task(Set<Integer> set, int actualCapacity, String mapRealization) {
            this.set = set;
            this.actualCapacity = actualCapacity;
            this.mapRealization = mapRealization;
        }

        @Override
        public void run() {

            List<Double> resultPut = new ArrayList<>();
            List<Double> resultGet = new ArrayList<>();

            for (int i = 0; i < ATTEMPT_NUMBER; i++) {

                ua.kyiv.kpi.fpm.km32.kohut.entity.Map map = this.mapRealization.equals("JavaHashMap") ? new MapAdapter(new HashMap<>(17)) : new HashMapOpenAddressing(17);

                Instant startPutting = Instant.now();
                for (Integer value : set) {
                    map.put(value, value.longValue());
                }
                Instant endPutting = Instant.now();
                resultPut.add(1.0 * Duration.between(startPutting, endPutting).toMillis() / 1000);

                Instant startGetting = Instant.now();
                for (Integer value : set) {
                    map.get(value);
                }
                Instant endGetting = Instant.now();
                resultGet.add(1.0 * Duration.between(startGetting, endGetting).toMillis() / 1000);
            }

            valuesPut.put(mapRealization, actualCapacity, resultPut);
            valuesGet.put(mapRealization, actualCapacity, resultGet);
        }
    }
}