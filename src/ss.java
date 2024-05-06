import java.util.ArrayList;
import java.util.List;

public class ss {
    

    public Point getCentroidPoint(List<Point> points){
        /*
         * Obtains the centroid of a list of points
         */
        double x = 0;
        double y = 0;
        for (Point p : points){
            x += p.getX();
            y += p.getY();
        }
        x = x/points.size();
        y = y/points.size();
        return new Point(x, y);
    }



    public double clusterDistance(List<Point> cluster1, List<Point> cluster2){
        /*
         * Obtains the distance between two clusters
         */
        Point centroid1 = getCentroidPoint(cluster1);
        Point centroid2 = getCentroidPoint(cluster2);
        return centroid1.distance(centroid2);
    }

    public List<List<Point>> findClosestClusters(List<List<Point>> clusters) {
        double minDistance = Double.MAX_VALUE;
        List<List<Point>> closestClusters = new ArrayList<>();

        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                double distance = clusterDistance(clusters.get(i), clusters.get(j));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestClusters.clear();
                    closestClusters.add(clusters.get(i));
                    closestClusters.add(clusters.get(j));
                }
            }
        }

        return closestClusters;
    }



    public List<List<Point>> minMaxSplitPolicy(List<Point> points, double B) {
        double minMaxRadius = Double.MAX_VALUE;
        List<Point> bestCluster1 = null;
        List<Point> bestCluster2 = null;
    
        // Considerar todos los posibles pares de puntos
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                List<Point> cluster1 = new ArrayList<>();
                List<Point> cluster2 = new ArrayList<>();
                cluster1.add(points.get(i));
                cluster2.add(points.get(j));
    
                // Alternativamente agregar el punto más cercano a uno de los centros
                for (int k = 0; k < points.size(); k++) {
                    if (k == i || k == j) continue;
                    Point point = points.get(k);
                    double distanceToCluster1 = getCentroidPoint(cluster1).distance(point);
                    double distanceToCluster2 = getCentroidPoint(cluster2).distance(point);
    
                    if (distanceToCluster1 < distanceToCluster2) {
                        cluster1.add(point);
                    } else {
                        cluster2.add(point);
                    }
                }
    
                // Calcular el radio cobertor máximo entre los dos grupos
                double maxRadius = Math.max(getRadius(cluster1), getRadius(cluster2));
    
                // Si el radio cobertor máximo es menor que el mínimo encontrado hasta ahora, actualizar los mejores clusters
                if (maxRadius < minMaxRadius) {
                    minMaxRadius = maxRadius;
                    bestCluster1 = cluster1;
                    bestCluster2 = cluster2;
                }
            }
        }
    
        // Devolver los mejores clusters encontrados
        List<List<Point>> result = new ArrayList<>();
        result.add(bestCluster1);
        result.add(bestCluster2);
        return result;
    }
    
    public double getRadius(List<Point> points) {
        Point centroid = getCentroidPoint(points);
        double maxDistance = 0;
        for (Point point : points) {
            double distance = centroid.distance(point);
            if (distance > maxDistance) {
                maxDistance = distance;
            }
        }
        return maxDistance;
    }



    public List<List<Point>> Cluster(List<Point> C_in,double B) {
        List <List<Point>> C_out = new ArrayList<>();
        List <List<Point>> C = new ArrayList<>();
        

        for (Point p : C_in){
            List <Point> temp = new ArrayList<>();
            temp.add(p);
            C.add(temp);
        }

        while (C.size() > 1){
            List<Point> cluster1 = findClosestClusters(C).get(0);
            List<Point> cluster2 = findClosestClusters(C).get(1);

            if (cluster2.size()> cluster1.size()){
                List<Point> temp = cluster1;
                cluster1 = cluster2;
                cluster2 = temp;
            } 

            List<Point> mergedCluster  = new ArrayList<>();
            mergedCluster.addAll(cluster1);
            mergedCluster.addAll(cluster2);

            if (mergedCluster.size() <= B){
                C.remove(cluster1);
                C.remove(cluster2);
                C.add(mergedCluster);
            }
            else {
                C_out.add(cluster1);
                C.remove(cluster1);
            }

        }
        List <Point> lastCluster = C.get(0);
        List<Point> c_prime = null;
        if (!C_out.isEmpty()){
            double minDistanceToLastCluster = Double.MAX_VALUE;
            

            for (List<Point> cluster : C_out) {
                double distance = clusterDistance(cluster, lastCluster);
                if (distance < minDistanceToLastCluster) {
                    minDistanceToLastCluster = distance;
                    c_prime = cluster;
                }
            }

            if (c_prime != null) {
                C_out.remove(c_prime);
            }

        }
        else {
            c_prime = new ArrayList<>();
        }

        List<Point> mergedCluster = new ArrayList<>();
        mergedCluster.addAll(lastCluster);
        mergedCluster.addAll(c_prime);
        if (mergedCluster.size() <= B){
            C_out.add(mergedCluster);
        }
        else {
            List<List<Point>> splitClusters = minMaxSplitPolicy(mergedCluster, B);
            C_out.add(splitClusters.get(0));
            C_out.add(splitClusters.get(1));
        }
        return C_out;
    }

    public List outputHoja ( List<Point> C_in,double B){
        Point g  = getCentroidPoint(C_in);
        MTreeNode C= new MTreeNode(true, B);
        double radius = 0;
        for (Point p : C_in){

            Entry e = new Entry(p, null,null);
            C.addEntry(e);
            double distance  = g.distance(p);
            if (distance > radius){
                radius = distance;
            }
        }
        List result = new ArrayList();
        result.add(g);
        result.add(radius);
        result.add(C);
        return result;
    }

    public List outputInterno(List<List> C_mraList, double B){
        
        List<Point> C_in = new ArrayList<>();
        for (List tuple : C_mraList){
            C_in.add((Point) tuple.get(0));    
        }
        Point G = getCentroidPoint(C_in);
        double RADIUS = 0;
        MTreeNode C = new MTreeNode(false, B);
        for (List tuple : C_mraList){
            Entry newEntry = new Entry((Point) tuple.get(0), (Double) tuple.get(1), (MTreeNode) tuple.get(2));
            double distance = G.distance((Point) tuple.get(0)) + (Double) tuple.get(1);
            if (distance > RADIUS){
                RADIUS = distance;
            }
            C.addEntry(newEntry);
        }

        List result = new ArrayList();
        result.add(G);
        result.add(RADIUS);
        result.add(C);
        return result;
    }

    public MTreeNode makeTree (List<Point> C_in,double B){
        if (C_in.size()<=B){
            List tuple = outputHoja(C_in, B);
            MTreeNode a = (MTreeNode) tuple.get(2);
            return a;
        }
        List<List<Point>> C_out = Cluster(C_in, B);


        List<List> C = new ArrayList<>();

        for (List<Point> cluster: C_out){
            C.add(outputHoja(cluster, B));
        }

        while (C.size() > B){
            List<Point> C_in_aux = new ArrayList<>();
            for (List tuple : C){
                C_in_aux.add((Point) tuple.get(0));
            }
            List<List> C_mra = new ArrayList<>();
            List<List<Point>> C_out_aux = Cluster(C_in_aux, B);
            
            for (List<Point> cluster : C_out_aux){
                List<Point> clusterPoints = new ArrayList<>();
                for (Point p : cluster){
                    clusterPoints.add(p);
                }
                List<List> s = new ArrayList<>();
                for (List tuple : C){
                    if (clusterPoints.contains((Point) tuple.get(0))){
                        s.add(tuple);
                    }
                }
                C_mra.add(s);
            }
            C = new ArrayList<>();
            for (List<List> s : C_mra){
                C.add(outputInterno(s, B));
            }
        }
        List result  = outputInterno(C, B);
        
        return (MTreeNode) result.get(2);
    }
}