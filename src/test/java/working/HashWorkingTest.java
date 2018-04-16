package working;

import org.junit.*;
import ua.kyiv.kpi.fpm.km32.kohut.domain.HashMapOpenAddressing;
import ua.kyiv.kpi.fpm.km32.kohut.domain.Map;
import ua.kyiv.kpi.fpm.km32.kohut.exception.NotExistKeyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HashWorkingTest {

    private static final int INITIAL_MAP_CAPACITY = 7;

    private final Map map;

    public HashWorkingTest() {
        this.map = new HashMapOpenAddressing(INITIAL_MAP_CAPACITY);
    }

    @Before
    public void init() throws IOException {

        try (InputStream inputStream = getClass().getResourceAsStream("/testPut.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))  {

            while (reader.ready()) {
                final String[] line = reader.readLine().split(" ");

                map.put(Integer.parseInt(line[0]), Long.parseLong(line[1]));
            }
        }
    }

    @Test
    public void sizeTest() throws IOException {

        try (InputStream inputStream = getClass().getResourceAsStream("/testSize.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))  {

            final int expectedSize = Integer.parseInt(reader.readLine());

            assertEquals(expectedSize, map.size());
        }
    }

    @Test
    public void getTest() throws IOException {

        try (InputStream inputStream = map.getClass().getResourceAsStream("/testGet.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))  {

            reader.readLine(); // skip the first line

            while (reader.ready()) {
                final String[] line = reader.readLine().split(" ");

                Long mapValue = null;

                try {
                    mapValue = map.get(Integer.parseInt(line[0]));
                } catch (Exception e) {
                    assertTrue(e instanceof NotExistKeyException);
                    assertEquals(1, line.length);
                }

                if (mapValue != null) {
                    assertEquals(Integer.parseInt(line[1]), mapValue.longValue());
                }
            }
        }
    }
}