package grauly.mt5.helpers;

import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;

public class ColorHelper {

    //Credit to https://bottosson.github.io/posts/oklab/
    public static Vec3d sRGBtoOKLab(Vector3f color) {
        double l = 0.4122214708f * color.x + 0.5363325363f * color.y + 0.0514459929f * color.z;
        double m = 0.2119034982f * color.x + 0.6806995451f * color.y + 0.1073969566f * color.z;
        double s = 0.0883024619f * color.x + 0.2817188376f * color.y + 0.6299787005f * color.z;

        double l_ = Math.cbrt(l);
        double m_ = Math.cbrt(m);
        double s_ = Math.cbrt(s);

        return new Vec3d(
                0.2104542553f * l_ + 0.7936177850f * m_ - 0.0040720468f * s_,
                1.9779984951f * l_ - 2.4285922050f * m_ + 0.4505937099f * s_,
                0.0259040371f * l_ + 0.7827717662f * m_ - 0.8086757660f * s_
        );
    }

    public static Vec3d sRGBtoOKLab(Color color) {
        return sRGBtoOKLab(Vec3d.unpackRgb(color.getRGB()).toVector3f());
    }

    //Credit to https://bottosson.github.io/posts/oklab/
    public static Vec3d OKLabTosRGB(Vec3d color) {
        double l_ = (color.x + 0.3963377774f * color.y + 0.2158037573f * color.z);
        double m_ = (color.x - 0.1055613458f * color.y - 0.0638541728f * color.z);
        double s_ = (color.x - 0.0894841775f * color.y - 1.2914855480f * color.z);

        double l = l_ * l_ * l_;
        double m = m_ * m_ * m_;
        double s = s_ * s_ * s_;

        return new Vec3d(
                +4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s,
                -1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s,
                -0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s
        );
    }

    public static Color OKLabToColor(Vec3d color) {
        Vec3d rgb = OKLabTosRGB(color);
        return new Color((float) rgb.x, (float) rgb.y, (float) rgb.z);
    }

    public static ArrayList<Color> getAdjacentColors(Color origin, double Ldeviation, int amount) {
        Vec3d okLabColor = sRGBtoOKLab(Vec3d.unpackRgb(origin.getRGB()).toVector3f());
        ArrayList<Color> colors = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            colors.add(OKLabToColor(okLabColor.add((-1 + (2 * i)) * Ldeviation, 0, 0)));
        }
        return colors;
    }
}
