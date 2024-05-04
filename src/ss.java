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
        if (C_out.size() > 0){
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
            
        }

        


        



        return C_out;
    }
    
}
