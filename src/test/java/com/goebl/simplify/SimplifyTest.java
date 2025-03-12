package com.goebl.simplify;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Test class for {@link Simplify}.
 *
 * @author goebl
 * @since 06.07.13
 */
public class SimplifyTest {

    private static Point[] allPoints;
    private static final float[] TOLERANCES = new float[]{
            1.0f, 1.5f, 2.0f, 4.0f, 5.0f
    };

    private static final float[][] POINTS_2D = new float[][]{
            {3.14f, 5.2f}, {5.7f, 8.1f}, {4.6f, -1.3f}
    };

    @BeforeClass
    public static void readPoints() throws Exception {
        allPoints = readPoints("points-all.txt");
    }

    @Test
    public void testSimpleQuality() throws Exception {
        for (float tolerance : TOLERANCES) {
            assertPointsEqual(tolerance, false);
        }
    }

    @Test
    public void testHighQuality() throws Exception {
        for (float tolerance : TOLERANCES) {
            assertPointsEqual(tolerance, true);
        }
    }

    @Test
    public void testCustomPointExtractor() {
        PointExtractor<float[]> pointExtractor = new PointExtractor<>() {
            @Override
            public double getX(float[] point) {
                return point[0];
            }

            @Override
            public double getY(float[] point) {
                return point[1];
            }
        };

        Simplify<float[]> simplify = new Simplify<>(pointExtractor);

        List<float[]> simplified = simplify.simplify(Arrays.stream(POINTS_2D).toList(), 5.0f, false);
        Assert.assertEquals("array should be simplified", 2, simplified.size());

        simplified = simplify.simplify(Arrays.stream(POINTS_2D).toList(), 5.0f, true);
        Assert.assertEquals("array should be simplified", 2, simplified.size());
    }

    @Test
    public void testInvalidPointsParam() {
        Simplify<Point> aut = new Simplify<>();
        Assert.assertThrows("throws when point-array is null", IllegalArgumentException.class,
                () -> aut.simplify(null, 1f, false));

        List<Point> only2 = new ArrayList<>();
        only2.add(new MyPoint(1, 2));
        only2.add(new MyPoint(2, 3));

        Assert.assertEquals("return identical array when less than 3 points",
                only2, aut.simplify(only2, 1f, false));
    }

    private void assertPointsEqual(float tolerance, boolean highQuality) throws Exception {
        Point[] pointsExpected = readPoints(tolerance, highQuality);
        long start = System.nanoTime();

        Simplify<Point> aut = new Simplify<>();
        List<Point> pointsActual = aut.simplify(Arrays.stream(allPoints).toList(), tolerance, highQuality);
        long end = System.nanoTime();
        System.out.println("tolerance=" + tolerance + " hq=" + highQuality
                + " nanos=" + (end - start));

        Assert.assertNotNull("wrong test setup", pointsExpected);
        Assert.assertNotNull("simplify must return Point[]", pointsActual);

        Assert.assertEquals("tolerance=" + tolerance + " hq=" + highQuality,
                Arrays.stream(pointsExpected).toList(), pointsActual);
    }

    private static Point[] readPoints(float tolerance, boolean highQuality) throws Exception {
        return readPoints(String.format(Locale.US, "points-%01.1f-%s.txt",
                tolerance, highQuality ? "hq" : "sq"));
    }

    static Point[] readPoints(String fileName) throws Exception {
        List<MyPoint> pointList = new ArrayList<>();
        File file = new File("src/test/resources", fileName);
        try (InputStream is = new FileInputStream(file)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] xy = line.split(",");
                double x = Double.parseDouble(xy[0]);
                double y = Double.parseDouble(xy[1]);
                pointList.add(new MyPoint(x, y));
            }
        }
        return pointList.toArray(new MyPoint[0]);
    }

    private static class MyPoint implements Point {
        double x;
        double y;

        private MyPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

        @Override
        public String toString() {
            return "{" + "x=" + x + ", y=" + y + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MyPoint myPoint = (MyPoint) o;

            if (Double.compare(myPoint.x, x) != 0) return false;
            return Double.compare(myPoint.y, y) == 0;
        }

    }

}
