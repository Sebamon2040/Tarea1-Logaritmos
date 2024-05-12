import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Experimento {
    public static void main(String[]  args) {





        int i = Integer.parseInt(args[0]);

        int numPoints = (int) Math.pow(2, i);

        // Generar puntos aleatorios
        List<Point> points = generatePoints(numPoints);
        List<Point> searchPoints=  generatePoints(100);



        /*
        SEXTON SWINBANK
         */
        // tomamos el tiempo en crear el arbol
        // Crear una instancia de la clase que implementa el algoritmo SS

//        ss sextonSwinbank = new ss();
//        long startTimeSS = System.nanoTime();
//        MTreeNode rootSS = sextonSwinbank.makeTree(points, 146);
//        long endTimeSS = System.nanoTime();
//        double timeTakenSS = (endTimeSS - startTimeSS) / 1e6; // tiempo en milisegundos
//        List<Integer> diskAccessesListSS = new ArrayList<>();
//        MTree mtreeSS = new MTree(rootSS);
//
//
//        log.print("Searching");
//        for (Point point : searchPoints) {
//            log.print("searching "+ point.getX() + point.getY());
//            int diskAccesses = mtreeSS.search(point, 0.02);
//            diskAccessesListSS.add(diskAccesses);
//        }
//        //calculamos el promedio de los accesos a disco
//        double sumSS = 0;
//        for (int diskAccesses : diskAccessesListSS) {
//            sumSS += diskAccesses;
//        }
//        double  averageDiskAccesses = sumSS / diskAccessesListSS.size();
//
//        log.print("Average disk accesses SS: " + averageDiskAccesses + " Time taken to build : " + (timeTakenSS/1000) + "s" +  " for 2 ^ " + i + "points\n");
//        // guardamos el resultado en un archivo de texto
//        String filename = MessageFormat.format("resultados_ss_ {0}.txt", i);
//        try {
//            FileWriter fileWriterSS = new FileWriter(filename);
//            PrintWriter printWriterSS = new PrintWriter(fileWriterSS);
//            printWriterSS.println("Average disk accesses SS: " + averageDiskAccesses + " Time taken to build: " + (timeTakenSS /1000) + "seconds" +  " for 2 ^ " + i + "points\n");
//            printWriterSS.println("All disk accesess SS: " + diskAccessesListSS);
//            printWriterSS.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        /*
        CP
         */

        long startTimeCP = System.nanoTime();
        MTreeNode rootCP = cp.buildCp(points, 146, 146 / 2);
        long endTimeCP = System.nanoTime();
        double timeTakenCP = (endTimeCP - startTimeCP) / 1e6; // tiempo en milisegundos
        List<Integer> diskAccessesListCP = new ArrayList<>();
        MTree mtreeCP = new MTree(rootCP);
        log.print("Searching");
        for (Point point : searchPoints) {
            log.print("searching "+ point.getX() + point.getY());
            int diskAccesses = mtreeCP.search(point, 0.02);
            diskAccessesListCP.add(diskAccesses);
        }
        //calculamos el promedio de los accesos a disco
        double sumCP = 0;
        for (int diskAccesses : diskAccessesListCP) {
            sumCP += diskAccesses;
        }
        double  averageDiskAccessesCP = sumCP / diskAccessesListCP.size();

        log.print("Average disk accesses CP : " + averageDiskAccessesCP + " Time taken to build : " + (timeTakenCP /1000) + "s" +  " for 2 ^ " + i + "points\n");
        // guardamos el resultado en un archivo de texto
        String filenameCP = MessageFormat.format("resultados_cp_ {0}.txt", i);
        try {
            FileWriter fileWriterCP = new FileWriter(filenameCP);
            PrintWriter printWriterCP = new PrintWriter(fileWriterCP);
            printWriterCP.println("Average disk accesses CP : " + averageDiskAccessesCP + " Time taken to build: " + (timeTakenCP /1000) + "seconds" +  " for 2 ^ " + i + "points\n");
            printWriterCP.println("All disk accesess CP: " + diskAccessesListCP);
            printWriterCP.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }




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
