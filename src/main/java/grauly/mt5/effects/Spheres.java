package grauly.mt5.effects;

import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class Spheres {

    public static void fibonacciSphere(Vec3d center, float radius, int points, Consumer<Vec3d> pointActions) {
        for (int i = 0; i < points; i++) {
            double i2 = i + 0.5f;
            double phi = Math.acos(1 - 2*i2/points);
            double theta = Math.PI * (Math.pow(5,0.5) * i2);
            pointActions.accept(new Vec3d(Math.cos(theta) * Math.sin(phi), Math.sin(theta) * Math.sin(phi), Math.cos(phi)).multiply(radius).add(center));
        }
    }

    public static void quadSphere(Vec3d center, float radius, int segmentCount, int sliceCount, Consumer<Vec3d> pointActions) {
        double sliceHeight = radius * 2 / sliceCount;
        for (int i = 0; i <= sliceCount; i++) {
            var offset = -radius + i * sliceHeight;
            var interRadius = (float) Math.sqrt(1-Math.pow((-1 + i*2f/sliceCount),2));
            Circles.circle(center.add(0, offset, 0), (float) interRadius * radius, pointActions, segmentCount);
        }
    }

    public static void icoSphere(Vec3d center, float radius, int subdivisions, Consumer<Vec3d> pointActions) {
        //yes I fucking hate this code, thank you for asking, but generating ico-spheres is a mess
        //credits to http://blog.andreaskahler.com/2009/06/creating-icosphere-mesh-in-code.html
        ArrayList<Vec3d> points = new ArrayList<>();
        ArrayList<Tri> faces = new ArrayList<>();
        double t = (1.0 + Math.sqrt(5.0)) / 2.0;
        points.add(new Vec3d(-1, t, 0).normalize());
        points.add(new Vec3d(1, t, 0).normalize());
        points.add(new Vec3d(-1, -t, 0).normalize());
        points.add(new Vec3d(1, -t, 0).normalize());

        points.add(new Vec3d(0, -1, t).normalize());
        points.add(new Vec3d(0, 1, t).normalize());
        points.add(new Vec3d(0, -1, -t).normalize());
        points.add(new Vec3d(0, 1, -t).normalize());

        points.add(new Vec3d(t, 0, -1).normalize());
        points.add(new Vec3d(t, 0, 1).normalize());
        points.add(new Vec3d(-t, 0, -1).normalize());
        points.add(new Vec3d(-t, 0, 1).normalize());


        faces.add(new Tri(0, 11, 5));
        faces.add(new Tri(0, 5, 1));
        faces.add(new Tri(0, 1, 7));
        faces.add(new Tri(0, 7, 10));
        faces.add(new Tri(0, 10, 11));

        faces.add(new Tri(1, 5, 9));
        faces.add(new Tri(5, 11, 4));
        faces.add(new Tri(11, 10, 2));
        faces.add(new Tri(10, 7, 6));
        faces.add(new Tri(7, 1, 8));

        faces.add(new Tri(3, 9, 4));
        faces.add(new Tri(3, 4, 2));
        faces.add(new Tri(3, 2, 6));
        faces.add(new Tri(3, 6, 8));
        faces.add(new Tri(3, 8, 9));

        faces.add(new Tri(4, 9, 5));
        faces.add(new Tri(2, 4, 11));
        faces.add(new Tri(6, 2, 10));
        faces.add(new Tri(8, 6, 7));
        faces.add(new Tri(9, 8, 1));

        for (int i = 0; i < subdivisions; i++) {
            faces = subdivide(faces, points);
        }

        points.stream().map(p -> p.multiply(radius).add(center)).toList().forEach(pointActions);
    }

    @NotNull
    private static ArrayList<Tri> subdivide(ArrayList<Tri> faces, ArrayList<Vec3d> points) {
        ArrayList<Tri> generatedFaces = new ArrayList<>();
        for (Tri tri : faces) {
            // replace triangle by 4 triangles
            int a = getMiddlePoint(tri.a, tri.b, points);
            int b = getMiddlePoint(tri.b, tri.c, points);
            int c = getMiddlePoint(tri.c, tri.a, points);

            generatedFaces.add(new Tri(tri.b, b, a));
            generatedFaces.add(new Tri(tri.a, a, c));
            generatedFaces.add(new Tri(tri.c, c, b));
            generatedFaces.add(new Tri(a, b, c));
        }
        return generatedFaces;
    }

    private static int getMiddlePoint(int from, int to, ArrayList<Vec3d> points) {
        Vec3d middle = points.get(from).lerp(points.get(to), 0.5f).normalize();
        if (points.contains(middle)) {
            return points.indexOf(middle);
        }
        points.add(middle);
        return points.size() - 1;
    }

    private record Tri(int a, int b, int c) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tri tri = (Tri) o;
            return a == tri.a && b == tri.b && c == tri.c;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b, c);
        }
    }

}
