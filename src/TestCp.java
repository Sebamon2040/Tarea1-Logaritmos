import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestCp {

    public static void main(String[] args) {
        // Crea una lista de puntos
        List<Point> points = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            double x = random.nextDouble() * 100;
            double y = random.nextDouble() * 100;
            points.add(new Point(x, y));
        }

        // Construye un árbol M usando el método CP
        MTree mTree = cp.buildCp(points, 10, 5);

        // Imprime la altura del árbol resultante
        System.out.println("Altura del árbol: " + mTree.getHeight());
    }
}