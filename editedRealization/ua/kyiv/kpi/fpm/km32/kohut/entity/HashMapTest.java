package ua.kyiv.kpi.fpm.km32.kohut.entity;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Locale;
import java.util.Set;
import java.util.Random;
import java.time.Instant;
import java.time.Duration;

public class HashMapTest {

    private static final int ATTEMPT_NUMBER = 10;
    private static int[] elementsNumber = {100_000, 500_000, 1_000_000};

    private static Map<String, Map<Integer, List<Double>>> valuesPut = new ConcurrentHashMap<>();
    private static Map<String, Map<Integer, List<Double>>> valuesGet = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Wait for result...");

        Random random = new Random();

        Thread threadForStandartMap = null;
        Thread threadForCustomMap = null;

        for (int elementsNumberValue : elementsNumber) {
            Set<Integer> set = new HashSet<>();
            for (int i = 0; i < elementsNumberValue; i++) {
                set.add((int) ((Integer.MAX_VALUE + Short.MAX_VALUE) * Math.random() - Short.MAX_VALUE));
            }
            
            threadForStandartMap = new Thread(new Task(set, elementsNumberValue, "JavaHashMap"));
            threadForStandartMap.start();  

            threadForCustomMap = new Thread(new Task(set, elementsNumberValue, "HashMapOpenAddressing"));
            threadForCustomMap.start(); 
        }

        if (threadForStandartMap != null) {
            threadForStandartMap.join();
        }

        if (threadForCustomMap != null) {
            threadForCustomMap.join();
        }

        printResult(valuesPut, "PUT");        
        System.out.println("------------------------------------------");
        printResult(valuesGet, "GET");
    }

    private static void printResult(Map<String, Map<Integer, List<Double>>> values, String operation) {
        System.out.println(operation);

        for (Map.Entry<String,Map<Integer, List<Double>>> entry : values.entrySet()) {
            System.out.println("For " + entry.getKey());
            String resultElements = "Elements(~):";
            String resultTimes = String.format("%" + resultElements.length() + "s", "Time (avg):");
            for (Map.Entry<Integer,List<Double>> entry2 : entry.getValue().entrySet()) {
                resultElements += String.format(Locale.US, "%,10d", entry2.getKey()).replace(",", ".");
                resultTimes += String.format("%10.5f", entry2.getValue().stream().mapToDouble(Double::doubleValue).sum() / 10);
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

                ua.kyiv.kpi.fpm.km32.kohut.entity.Map map = this.mapRealization.equals("JavaHashMap") ? new MapAdapter(new HashMap<Integer, Long>(17)) : new HashMapOpenAddressing(17);

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

            if (!valuesPut.containsKey(mapRealization)) {
                Map<Integer, List<Double>> temp = new HashMap<>();
                temp.put(this.actualCapacity, resultPut);
                valuesPut.put(mapRealization, temp);
            }

            valuesPut.get(mapRealization).put(this.actualCapacity, resultPut);

            if (!valuesGet.containsKey(mapRealization)) {
                Map<Integer, List<Double>> temp = new HashMap<>();
                temp.put(this.actualCapacity, resultGet);
                valuesGet.put(mapRealization, temp);
            }

            valuesGet.get(mapRealization).put(this.actualCapacity, resultGet);
        }
    }
}