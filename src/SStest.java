import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SStest {
    public static void main(String[]  args) {
        // Crear una instancia de la clase que implementa el algoritmo SS
        ss sextonSwinbank = new ss();



        int i = Integer.parseInt(args[0]);

        int numPoints = (int) Math.pow(2, i);

        // Generar puntos aleatorios
        List<Point> points = generatePoints(numPoints);
        // tomamos el tiempo en crear el arbol
        long startTime = System.nanoTime();
        // Crear el árbol M usando el algoritmo SS con un valor B de 512
        MTreeNode root = sextonSwinbank.makeTree(points, 512);
        long endTime = System.nanoTime();
        double timeTaken = (endTime - startTime) / 1e6; // tiempo en milisegundos
        List<Integer> diskAccessesList = new ArrayList<>();
        MTree mtree = new MTree(root);
        //buscar en el Mtree para cada punto
        for (Point point : points) {
            int diskAccesses = mtree.search(point, 0.1);
            diskAccessesList.add(diskAccesses);
        }
        //calculamos el promedio de los accesos a disco
        double sum = 0;
        for (int diskAccesses : diskAccessesList) {
            sum += diskAccesses;
        }
        double  averageDiskAccesses = sum / diskAccessesList.size();

        log.print("Average disk accesses : " + averageDiskAccesses + " Time taken: " + timeTaken /1000 + "ms" +  " for 2 ^ " + i + "points\n"); 
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