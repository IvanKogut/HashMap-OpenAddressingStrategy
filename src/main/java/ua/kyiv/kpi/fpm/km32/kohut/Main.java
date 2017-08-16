package ua.kyiv.kpi.fpm.km32.kohut;

import ua.kyiv.kpi.fpm.km32.kohut.entity.HashMapOpenAddressing;
import ua.kyiv.kpi.fpm.km32.kohut.entity.Map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by i.kohut on 8/16/2017.
 */
public class Main {

    //TODO JUnit
    public static void main(String[] args) throws IOException {

        Map map;

        // put
        //TODO Change the System.out to other object
        try (InputStream inputStream = System.out.getClass().getResourceAsStream("/testPut.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))  {

            map = new HashMapOpenAddressing(Integer.parseInt(reader.readLine().split(" ")[1]));

            while (reader.ready()) {
                String[] line = reader.readLine().split(" ");
                map.put(Integer.parseInt(line[0]), Long.parseLong(line[1]));
            }
        }

        // size
        try (InputStream inputStream = map.getClass().getResourceAsStream("/testSize.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))  {

            System.out.println(map.size() == Integer.parseInt(reader.readLine().split(" ")[1]));
        }

        // get
        try (InputStream inputStream = map.getClass().getResourceAsStream("/testGet.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))  {

            reader.readLine(); // skip the first line

            while (reader.ready()) {
                String[] line = reader.readLine().split(" ");
                System.out.println(Integer.parseInt(line[1]) == map.get(Integer.parseInt(line[0])));
            }
        }
    }
}