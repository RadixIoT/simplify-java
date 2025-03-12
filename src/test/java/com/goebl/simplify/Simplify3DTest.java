package com.goebl.simplify;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for {@link com.goebl.simplify.Simplify}.
 *
 * @author goebl
 * @since 06.07.13
 */
public class Simplify3DTest {

    private static final float[][] POINTS_3D = new float[][]{
            {3.14f, 5.2f, 4f}, {5.7f, 8.1f, 5f}, {4.6f, -1.3f, 6f}
    };

    @Test
    public void testCustomPoint3DExtractor() {
        Point3DExtractor<float[]> pointExtractor = new Point3DExtractor<>() {
            @Override
            public double getX(float[] point) {
                return point[0];
            }

            @Override
            public double getY(float[] point) {
                return point[1];
            }

            @Override
            public double getZ(float[] point) {
                return point[2];
            }
        };

        Simplify3D<float[]> simplify = new Simplify3D<>(pointExtractor);

        List<float[]> simplified = simplify.simplify(Arrays.stream(POINTS_3D).toList(), 5.0f, false);
        Assert.assertEquals("array should be simplified", 2, simplified.size());

        simplified = simplify.simplify(Arrays.stream(POINTS_3D).toList(), 5.0f, true);
        Assert.assertEquals("array should be simplified", 2, simplified.size());
    }

    @Test
    public void testDefaultPointExtractor() {
        List<Point3D> points = new ArrayList<>();
        for (float[] floats : POINTS_3D) {
            points.add(new MyPoint(floats[0], floats[1], floats[2]));
        }

        Simplify3D<Point3D> simplify3D = new Simplify3D<>();

        List<Point3D> simplified = simplify3D.simplify(points, 5.0d, false);
        Assert.assertEquals("array should be simplified", 2, simplified.size());

        simplified = simplify3D.simplify(points, 5.0d, true);
        Assert.assertEquals("array should be simplified", 2, simplified.size());
    }

    private static class MyPoint implements Point3D {
        double x;
        double y;
        double z;

        private MyPoint(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
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
        public double getZ() {
            return z;
        }

        @Override
        public String toString() {
            return "{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MyPoint myPoint = (MyPoint) o;

            if (Double.compare(myPoint.x, x) != 0) return false;
            if (Double.compare(myPoint.y, y) != 0) return false;
            return Double.compare(myPoint.z, z) == 0;
        }

    }

}
