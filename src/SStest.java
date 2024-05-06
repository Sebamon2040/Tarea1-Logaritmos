import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SStest {
    public static void main(String[] args) {
        // Generar 1000 puntos aleatorios
        List<Point> points = generatePoints(8192);

        // Crear una instancia de la clase que implementa el algoritmo SS
        ss sextonSwinbank = new ss();

        // Medir el tiempo inicial
        long startTime = System.currentTimeMillis();

        /*
         * El tamaño de una entrada es de 8 bytes, por lo que B es de 4096/8 = 512
         */
        // Crear el árbol M usando el algoritmo SS con un valor B de 5
        MTreeNode root = sextonSwinbank.makeTree(points, 512);

        // Medir el tiempo final
        long endTime = System.currentTimeMillis();

        // Calcular e imprimir el tiempo que se tardó en construir el árbol
        long duration = endTime - startTime;
        System.out.println("Tiempo que se tardó en construir el árbol: " + duration + " milisegundos");

        MTree tree = new MTree(root);
        List <Integer> diskAccesses = new ArrayList<>();

        for (Point point : points) {
  
            int accessCount = tree.search(point, 0.1);
            diskAccesses.add(accessCount);
        }

        // Calcular e imprimir el número promedio de accesos a disco
        int sum = 0;
        for (int accessCount : diskAccesses) {
            sum += accessCount;
        }
        double average = sum / diskAccesses.size();

        log.print("Número promedio de accesos a disco: " + average);
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