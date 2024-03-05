package grauly.mt5.helpers;

import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;

public class ColorHelper {

    //Credit to https://bottosson.github.io/posts/oklab/
    public static Vec3d sRGBtoOKLab(Color color) {
        float l = 0.4122214708f * color.getRed() + 0.5363325363f * color.getGreen() + 0.0514459929f * color.getBlue();
        float m = 0.2119034982f * color.getRed() + 0.6806995451f * color.getGreen() + 0.1073969566f * color.getBlue();
        float s = 0.0883024619f * color.getRed() + 0.2817188376f * color.getGreen() + 0.6299787005f * color.getBlue();

        float l_ = (float) Math.cbrt(l);
        float m_ = (float) Math.cbrt(m);
        float s_ = (float) Math.cbrt(s);

        return new Vec3d(
                0.2104542553f * l_ + 0.7936177850f * m_ - 0.0040720468f * s_,
                1.9779984951f * l_ - 2.4285922050f * m_ + 0.4505937099f * s_,
                0.0259040371f * l_ + 0.7827717662f * m_ - 0.8086757660f * s_
        );
    }

    //Credit to https://bottosson.github.io/posts/oklab/
    public static Color OKLabTosRGB(Vec3d color) {
        float l_ = (float) (color.x + 0.3963377774f * color.y + 0.2158037573f * color.z);
        float m_ = (float) (color.x - 0.1055613458f * color.y - 0.0638541728f * color.z);
        float s_ = (float) (color.x - 0.0894841775f * color.y - 1.2914855480f * color.z);

        float l = l_ * l_ * l_;
        float m = m_ * m_ * m_;
        float s = s_ * s_ * s_;

        return new Color(
                +4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s,
                -1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s,
                -0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s
        );
    }

    public static ArrayList<Color> getAdjacentColors(Color origin, float Ldeviation, int amount) {
        Vec3d okLabColor = sRGBtoOKLab(origin);
        ArrayList<Color> colors = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            colors.add(OKLabTosRGB(okLabColor.add((-1 + (2 * i)) * Ldeviation, 0, 0)));
        }
        return colors;
    }
}
