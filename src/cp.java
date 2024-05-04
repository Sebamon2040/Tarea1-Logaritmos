import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author elcar
 */
public class cp{
    
    public static MTree buildCp(List<Point> points , int B, int b) {
        if (points.size() <=B){ // si el numero de puntos es menor o igual a la capacidad minima del nodo, se crea un arbol y se insertan todos los puntos
            MTreeNode leafNode = new MTreeNode(true , B);
            for (Point p : points){
                leafNode.addEntry(new Entry(p, null, null));
            }
            return new MTree(leafNode);        
        }
        
        int k = Math.min(B, points.size()/B); // se calcula el numero de puntos que se van a seleccionar para ser los centroides
        List<Point> samples = chooseRandomSamples(points, k); // se seleccionan los centroides

        List<List<Point>> F = assingPointsToClosestSamples(points, samples);
        List<List<Point>> Fr = redistributeSamples(F, b); // se redistribuyen los puntos en los clusters



        
    }

    private static List<Point> chooseRandomSamples(List<Point> points, int k){
        Random random = new Random();
        List<Point> samples = new ArrayList<>();
        for (int i = 0; i<k; i++){
            int index = random.nextInt(points.size());
            samples.add(points.get(index));
        }
        return samples;
    }

    private static List<List<Point>> assingPointsToClosestSamples(List<Point> points, List<Point> samples){
        List<List<Point>> clusters = new ArrayList<>(samples.size());
        for (int i=0; i<samples.size(); i++){
            clusters.add(new ArrayList<>());
        }
        
        for (Point point : points){
            int closestSample = findClosestSample(point, samples);
            clusters.get(closestSample).add(point);
        }
        return clusters;

    }

    private static int findClosestSample(Point point, List<Point> samples){
        double minDistance = Double.MAX_VALUE;
        int closestSample = -1;
        for (int i=0; i<samples.size(); i++){
            double distance = point.distance(samples.get(i));
            if (distance < minDistance){
                minDistance = distance;
                closestSample = i;
            }
        }
        return closestSample;
    }

    private static List<List<Point>> redistributeSamples(List<List<Point>> F, int b){
        List<List<Point>> balancedClusters = new ArrayList<>();
        for (List<Point> cluster : F){
            if (cluster.size() >= b){
                balancedClusters.add(cluster);
            } else {
                
            }
        }
    }


}

