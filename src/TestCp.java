import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestCp {

    public static void main(String[] args) {
        // Crea una lista de puntos
        // Generar 1000 puntos aleatorios
        List<Point> points = generatePoints(1024);

        // Construye un árbol M usando el método CP

        MTree mTree = cp.buildCp(points, 512, 256);

        // Imprime la altura del árbol resultante
        System.out.println("Altura del árbol: " + mTree.getHeight());
    }

    public static List<Point> generatePoints(int numPoints) {
        List<Point> points = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numPoints; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            Point point = new Point(x, y);
            points.add(point);
        }

        return points;
    }
}