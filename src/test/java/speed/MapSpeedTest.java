package speed;

import com.google.common.collect.*;
import ua.kyiv.kpi.fpm.km32.kohut.domain.HashMapOpenAddressing;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.time.Instant;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class MapSpeedTest {

    private static final int ATTEMPT_NUMBER = 10;
    private static final int[] ELEMENTS_NUMBER = { 100_000, 500_000, 1_000_000 };

    private static final Table<String, Integer, List<Double>> GET_VALUES =
            Tables.synchronizedTable(HashBasedTable.create());

    private static final Table<String, Integer, List<Double>> PUT_VALUES =
            Tables.synchronizedTable(HashBasedTable.create());

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Wait for result...\n");

        Thread threadForStandardMap = null;
        Thread threadForCustomMap = null;

        for (int elementsNumber : ELEMENTS_NUMBER) {

            final Set<Integer> elements = new HashSet<>();
            for (int i = 0; i < elementsNumber; i++) {
                elements.add(ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            
            threadForStandardMap = new Thread(
                    new JavaHashMapTask(elements, elementsNumber, "JavaHashMap")
            );

            threadForStandardMap.start();

            threadForCustomMap = new Thread(
                    new HashMapOpenAddressingTask(elements, elementsNumber, "HashMapOpenAddressing")
            );

            threadForCustomMap.start(); 
        }

        if (threadForStandardMap != null) {
            threadForStandardMap.join();
        }

        if (threadForCustomMap != null) {
            threadForCustomMap.join();
        }

        printResult(PUT_VALUES, "PUT");
        System.out.println("------------------------------------------");
        printResult(GET_VALUES, "GET");
    }

    private static void printResult(final Table<String, Integer, List<Double>> values, final String operation) {

        System.out.println(operation);

        for (java.util.Map.Entry<String,java.util.Map<Integer, List<Double>>> entry : values.rowMap().entrySet()) {
            System.out.println("For " + entry.getKey());

            final StringBuilder resultElements = new StringBuilder("Elements(~):");

            final StringBuilder resultTimes = new StringBuilder(
                    String.format("%" + resultElements.length() + "s", "Time (avg):")
            );

            for (java.util.Map.Entry<Integer,List<Double>> entry2 : entry.getValue().entrySet()) {
                resultElements.append(
                        String.format(Locale.US, "%,10d", entry2.getKey()).replace(",", "")
                );

                final double averageDuration = entry2.getValue()
                        .stream()
                        .mapToDouble(Double::doubleValue)
                        .average().orElseThrow(() -> new RuntimeException("No existing values"));

                resultTimes.append(String.format("%10.5f", averageDuration));
            }

            System.out.println(resultElements);
            System.out.println(resultTimes);
        }
    }

    private abstract static class AbstractMapTask {
        final Set<Integer> elements;
        final int actualCapacity;
        final String name;

        final List<Double> putMethodDurationInSeconds = new ArrayList<>();
        final List<Double> getMethodDurationInSeconds = new ArrayList<>();

        AbstractMapTask(Set<Integer> elements, int actualCapacity, String name) {
            this.elements = elements;
            this.actualCapacity = actualCapacity;
            this.name = name;
        }

        void addPutMethodDuration(final Instant start, final Instant end) {
            putMethodDurationInSeconds.add(getDurationInSeconds(start, end));
        }

        void addGetMethodDuration(final Instant start, final Instant end) {
            getMethodDurationInSeconds.add(getDurationInSeconds(start, end));
        }

        private double getDurationInSeconds(final Instant start, final Instant end) {
            return Duration.between(start, end).toMillis() / 1000.0;
        }

        void pushData() {
            PUT_VALUES.put(name, actualCapacity, putMethodDurationInSeconds);
            GET_VALUES.put(name, actualCapacity, getMethodDurationInSeconds);
        }
    }

    static class HashMapOpenAddressingTask extends AbstractMapTask implements Runnable {

        HashMapOpenAddressingTask(Set<Integer> elements, int actualCapacity, String name) {
            super(elements, actualCapacity, name);
        }

        @Override
        public void run() {

            for (int i = 0; i < ATTEMPT_NUMBER; i++) {

                final ua.kyiv.kpi.fpm.km32.kohut.domain.Map map = new HashMapOpenAddressing(17);

                final Instant startPutting = Instant.now();
                for (Integer value : elements) {
                    map.put(value, value.longValue());
                }
                final Instant endPutting = Instant.now();

                addPutMethodDuration(startPutting, endPutting);

                final Instant startGetting = Instant.now();
                for (Integer value : elements) {
                    map.get(value);
                }
                final Instant endGetting = Instant.now();

                addGetMethodDuration(startGetting, endGetting);
            }

            pushData();
        }
    }

    static class JavaHashMapTask extends AbstractMapTask implements Runnable {

        JavaHashMapTask(Set<Integer> elements, int actualCapacity, String name) {
            super(elements, actualCapacity, name);
        }

        @Override
        public void run() {

            for (int i = 0; i < ATTEMPT_NUMBER; i++) {

                final java.util.Map<Integer, Long> map = new HashMap<>(17);

                final Instant startPutting = Instant.now();
                for (Integer value : elements) {
                    map.put(value, value.longValue());
                }
                final Instant endPutting = Instant.now();

                addPutMethodDuration(startPutting, endPutting);

                final Instant startGetting = Instant.now();
                for (Integer value : elements) {
                    map.get(value);
                }
                final Instant endGetting = Instant.now();

                addGetMethodDuration(startGetting, endGetting);
            }

            pushData();
        }
    }
}