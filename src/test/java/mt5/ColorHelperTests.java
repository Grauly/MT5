package mt5;

import grauly.mt5.helpers.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ColorHelperTests {

    public static double EPSILON = 1E-5;

    @Test
    public void testConversionToOkLab1() {
        Vec3d baseColor = new Vec3d(0.950, 1, 1.089);
        Vec3d okLabColor = ColorHelper.sRGBtoOKLab(baseColor.toVector3f());
        boolean pass = true;
        StringBuilder message = new StringBuilder();
        pass = pass && testValue(okLabColor.getX(), 1, message, "L");
        pass = pass && testValue(okLabColor.getY(), 0, message, "a");
        pass = pass && testValue(okLabColor.getZ(), 0, message, "b");
        assertTrue(pass, message.toString());
    }

    @Test
    public void testConversionToOkLab2() {
        Vec3d baseColor = new Vec3d(1, 0, 0);
        Vec3d okLabColor = ColorHelper.sRGBtoOKLab(baseColor.toVector3f());
        boolean pass = true;
        StringBuilder message = new StringBuilder();
        pass = pass && testValue(okLabColor.getX(), 0.450, message, "L");
        pass = pass && testValue(okLabColor.getY(), 1.236, message, "a");
        pass = pass && testValue(okLabColor.getZ(), -0.019, message, "b");
        assertTrue(pass, message.toString());
    }

    @Test
    public void testConversionToOkLab3() {
        Vec3d baseColor = new Vec3d(0, 1, 0);
        Vec3d okLabColor = ColorHelper.sRGBtoOKLab(baseColor.toVector3f());
        boolean pass = true;
        StringBuilder message = new StringBuilder();
        pass = pass && testValue(okLabColor.getX(), 0.922, message, "L");
        pass = pass && testValue(okLabColor.getY(), -0.671, message, "a");
        pass = pass && testValue(okLabColor.getZ(), 0.263, message, "b");
        assertTrue(pass, message.toString());
    }

    @Test
    public void testConversionToOkLab4() {
        Vec3d baseColor = new Vec3d(0d, 0d, 1d);
        Vec3d okLabColor = ColorHelper.sRGBtoOKLab(baseColor.toVector3f());
        boolean pass = true;
        StringBuilder message = new StringBuilder();
        pass = pass && testValue(okLabColor.getX(), 0.153, message, "L");
        pass = pass && testValue(okLabColor.getY(), -1.415, message, "a");
        pass = pass && testValue(okLabColor.getZ(), -0.449, message, "b");
        assertTrue(pass, message.toString());
    }

    @Test
    public void testConversionFromOkLab1() {
        Vec3d okLabColor = new Vec3d(1.000, 0.000, 0.000);
        StringBuilder message = new StringBuilder();
        boolean pass = true;
        Vec3d color = ColorHelper.OKLabTosRGB(okLabColor);
        pass = pass && testValue(color.x, 0.950, message, "r");
        pass = pass && testValue(color.y, 1.000, message, "g");
        pass = pass && testValue(color.z, 1.089, message, "b");
        assertTrue(pass, message.toString());
    }

    @Test
    public void testConversionFromOkLab2() {
        Vec3d okLabColor = new Vec3d(0.450, 1.236, -0.019);
        StringBuilder message = new StringBuilder();
        boolean pass = true;
        Vec3d color = ColorHelper.OKLabTosRGB(okLabColor);
        pass = pass && testValue(color.x, 1.000, message, "r");
        pass = pass && testValue(color.y, 0.000, message, "g");
        pass = pass && testValue(color.z, 0.000, message, "b");
        assertTrue(pass, message.toString());
    }

    @Test
    public void testConversionFromOkLab3() {
        Vec3d okLabColor = new Vec3d(0.922, -0.671, 0.263);
        StringBuilder message = new StringBuilder();
        boolean pass = true;
        Vec3d color = ColorHelper.OKLabTosRGB(okLabColor);
        pass = pass && testValue(color.x, 0.000, message, "r");
        pass = pass && testValue(color.y, 1.000, message, "g");
        pass = pass && testValue(color.z, 0.000, message, "b");
        assertTrue(pass, message.toString());
    }

    @Test
    public void testConversionFromOkLab4() {
        Vec3d okLabColor = new Vec3d(0.153, -1.415, -0.449);
        StringBuilder message = new StringBuilder();
        boolean pass = true;
        Vec3d color = ColorHelper.OKLabTosRGB(okLabColor);
        pass = pass && testValue(color.x, 0.000, message, "r");
        pass = pass && testValue(color.y, 0.000, message, "g");
        pass = pass && testValue(color.z, 1.000, message, "b");
        assertTrue(pass, message.toString());
    }

    @Test
    public void testIfAnythingWorks() {
        Vec3d baseColor = new Vec3d(100, 100, 100);
        Vec3d okLabColor = ColorHelper.sRGBtoOKLab(baseColor.toVector3f());
        boolean pass = true;
        StringBuilder message = new StringBuilder();
        pass = pass && testValue(okLabColor.getX(), 0.5, message, "L");
        pass = pass && testValue(okLabColor.getY(), 0, message, "a");
        pass = pass && testValue(okLabColor.getZ(), 0, message, "b");
        assertTrue(pass, message.toString());
    }

    private boolean testValue(double value, double supposedValue, StringBuilder message, String place) {
        if (value - supposedValue <= EPSILON) return true;
        message.append(place).append(" outside of accepted bound, is: ").append(value).append(" should be ").append(supposedValue);
        return false;
    }
}
