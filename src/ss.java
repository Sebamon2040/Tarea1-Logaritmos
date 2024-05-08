import java.util.ArrayList;
import java.util.List;

public class ss {
    
    
    


    


    public double clusterDistance(Cluster cluster1, Cluster cluster2){
        
        /*
         * Obtains the distance between two clusters
         */
        Point centroid1 = cluster1.calculateMedioide();
        Point centroid2 = cluster2.calculateMedioide();
        
        double clusterDistance  = centroid1.distance(centroid2);
        
        return clusterDistance;
    }

    public List<Cluster> findClosestClusters(List<Cluster> clusters) {
        
        double minDistance = Double.MAX_VALUE;
        List<Cluster> closestClusters = new ArrayList<>();

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


    public List<Cluster> minMaxSplitPolicy(Cluster cluster, double B) {
        
        double minMaxRadius = Double.MAX_VALUE;
        Cluster bestCluster1 = null;
        Cluster bestCluster2 = null;
    
        // Considerar todos los posibles pares de puntos
        for (int i = 0; i < cluster.getSize(); i++) {
            log.print("progress: " + i + "/" + cluster.getSize() + " points processed.");
            for (int j = i + 1; j < cluster.getSize(); j++) {
                Cluster cluster1 = new Cluster();
                Cluster cluster2 = new Cluster();
                cluster1.addPunto(cluster.getPoint(i));
                cluster2.addPunto(cluster.getPoint(j));
                double distanceToCluster1 = cluster1.calculateMedioide().distance(cluster.getPoint(i));
                double distanceToCluster2 = cluster2.calculateMedioide().distance(cluster.getPoint(j));
    
                // Alternativamente agregar el punto más cercano a uno de los centros
                for (int k = 0; k < cluster.getSize(); k++) {
                    if (k == i || k == j) continue;
                    Point point = cluster.getPoint(k);
                    
    
                    if (distanceToCluster1 < distanceToCluster2) {
                        cluster1.addPunto(point);
                    } else {
                        cluster2.addPunto(point);
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
        List<Cluster> result = new ArrayList<>();
        result.add(bestCluster1);
        result.add(bestCluster2);
        
        return result;
    }
    public double getRadius(Cluster cluster) {
        
        Point centroid = cluster.getMedioide();
        double maxDistance = 0;
        List<Point> points = cluster.getPuntos();
        for (Point point : points) {
            double distance = centroid.distance(point);
            if (distance > maxDistance) {
                maxDistance = distance;
            }
        }
        
        return maxDistance;
    }


    
    public List<Cluster> MakeClusters(List<Point> C_in,double B) {
        
        List <Cluster> C_out = new ArrayList<>();
        List <Cluster> C = new ArrayList<>();
        
        
        for (Point p : C_in){
            


            Cluster c = new Cluster();
            c.addPunto(p);
            C.add(c);
            
        }

        while (C.size() > 1){
            log.print("progress of clustering: " + C.size() );
            List<Cluster> result = findClosestClusters(C);

            Cluster  cluster1 = result.get(0);
            Cluster  cluster2 = result.get(1);

            if (cluster2.getSize()> cluster1.getSize()){
                Cluster  temp = cluster1;
                cluster1 = cluster2;
                cluster2 = temp;
            } 

            Cluster mergedCluster  = new Cluster();
            List<Point> cluster1Points = cluster1.getPuntos();
            List<Point> cluster2Points = cluster2.getPuntos();
            
            for (Point p : cluster1Points){
                mergedCluster.addPunto(p);
            }

            for (Point p : cluster2Points){
                mergedCluster.addPunto(p);
            }




            if (mergedCluster.getSize() <= B){
                C.remove(cluster1);
                C.remove(cluster2);
                C.add(mergedCluster);
            }
            else {
                C_out.add(cluster1);
                C.remove(cluster1);
            }

        }
        Cluster lastCluster = C.get(0);
        Cluster c_prime = null;
        if (!C_out.isEmpty()){
            
            double minDistanceToLastCluster = Double.MAX_VALUE;
            
            log.print("Calculating closest cluster to c_prime");
            for (Cluster cluster : C_out) {
                double distance = clusterDistance(cluster, lastCluster);
                if (distance < minDistanceToLastCluster) {
                    minDistanceToLastCluster = distance;
                    c_prime = cluster;
                }
            }
            
            if (c_prime != null) {
                log.print("Removing c_prime from C_out");
                C_out.remove(c_prime);
            }

        }
        else {
            c_prime = new Cluster();
        }

        Cluster mergedCluster = new Cluster();
        List<Point> lastClusterPoints = lastCluster.getPuntos();
        List<Point> c_primePoints = c_prime.getPuntos();
        for (Point p : lastClusterPoints){
            mergedCluster.addPunto(p);
        }
        for (Point p : c_primePoints){
            mergedCluster.addPunto(p);
        }

        if (mergedCluster.getSize() <= B){
            log.print("Adding merged cluster to C_out");
            C_out.add(mergedCluster);
        }
        else {
            log.print("Splitting merged cluster");
            List<Cluster> splitClusters = minMaxSplitPolicy(mergedCluster, B);
            C_out.add(splitClusters.get(0));
            C_out.add(splitClusters.get(1));
        }
        log.print("Finished clustering");
        return C_out;
    }

    public List outputHoja ( List<Point> C_in,double B){
        Cluster C_in_aux = new Cluster();
        for (Point p : C_in){
            C_in_aux.addPunto(p);
        }
        
        Point g  = C_in_aux.calculateMedioide();
        MTreeNode C= new MTreeNode(true, B);
        double radius = 0;
        List<Point> points = C_in_aux.getPuntos();
        for (Point p : points){

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
        Cluster C_in_aux = new Cluster();
        for (Point p : C_in){
            C_in_aux.addPunto(p);
        }
        Point G = C_in_aux.calculateMedioide();
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
            log.print("C_in size is less than or equal to B, returning a leaf node.");
            List tuple = outputHoja(C_in, B);
            MTreeNode a = (MTreeNode) tuple.get(2);
            return a;
        }
        log.print("Clustering C_in");
        List<Cluster> C_out = MakeClusters(C_in, B);


        List<List> C = new ArrayList<>();

        for (Cluster cluster: C_out){
            List<Point> clusterPoints = cluster.getPuntos();
            C.add(outputHoja(clusterPoints, B));
        }

        while (C.size() > B){
            
            log.print("progress: " + C.size() );
            
            List<Point> C_in_aux = new ArrayList<>();

            for (List tuple : C){

                C_in_aux.add((Point) tuple.get(0));
            }
            List<List> C_mra = new ArrayList<>();
            log.print("Clustering C_in_aux");
            List<Cluster> C_out_aux = MakeClusters(C_in_aux, B);
            
            for (Cluster cluster : C_out_aux){
                List<Point> clusterPoints = cluster.getPuntos();
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
