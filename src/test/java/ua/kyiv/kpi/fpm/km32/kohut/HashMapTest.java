package ua.kyiv.kpi.fpm.km32.kohut;

import org.junit.*;
import ua.kyiv.kpi.fpm.km32.kohut.entity.HashMapOpenAddressing;
import ua.kyiv.kpi.fpm.km32.kohut.entity.Map;
import ua.kyiv.kpi.fpm.km32.kohut.exception.NotExistKeyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by i.kohut on 8/16/2017.
 */
public class HashMapTest {

    private Map map;

    @Before
    public void fillMap() throws IOException {
        try (InputStream inputStream = this.getClass().getResourceAsStream("/testPut.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))  {

            map = new HashMapOpenAddressing(Integer.parseInt(reader.readLine().split(" ")[1]));

            while (reader.ready()) {
                String[] line = reader.readLine().split(" ");
                map.put(Integer.parseInt(line[0]), Long.parseLong(line[1]));
            }
        }
    }

    @Test
    public void sizeTest() {
        Assert.assertEquals(10_000, map.size());
    }

    @Test()
    public void getTest() throws IOException {

        try (InputStream inputStream = map.getClass().getResourceAsStream("/testGet.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))  {

            reader.readLine(); // skip the first line

            while (reader.ready()) {
                String[] line = reader.readLine().split(" ");

                Long value = null;
                try {
                    value = map.get(Integer.parseInt(line[0]));
                } catch (Exception e) {
                    Assert.assertTrue(e instanceof NotExistKeyException);
                }

                if (value != null) {
                    Assert.assertEquals(Integer.parseInt(line[1]), value.longValue());
                }
            }
        }
    }
}