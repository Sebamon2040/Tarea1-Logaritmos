
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Cluster {
    private Point medioide;
    private List<Point> puntos;


    public Cluster() {
        this.medioide = null;
        this.puntos = new ArrayList<>();
    }

    public Point getMedioide() {
        return medioide;
    }

    public void setMedioide(Point medioide) {
        this.medioide = medioide;
    }

    public List<Point> getPuntos() {
        return puntos;
    }

    public void addPunto(Point punto) {
        this.puntos.add(punto);
    }


    public int  getSize() {
        return this.puntos.size();
    }

    public Point getPoint(int i) {
        return this.puntos.get(i);
    }


    public Point calculateMedioide() {
        if (getMedioide() != null) {
            return getMedioide();
        }
        else{
            List<Point> points = getPuntos();
        //usamos algoritmo K-medoids para que tome menos tiempo
        if (points.isEmpty()) {
            return null;
        }
    
        Point medoid = points.get(0);
        double minTotalDistance = Double.MAX_VALUE;
    
        // Number of maximum iterations
        int maxIterations = 1000;
    
        for (int iter = 0; iter < maxIterations; iter++) {
            double totalDistance = 0.0;
    
            for (Point p : points) {
                double distance = 0.0;
                for (Point q : points) {
                    distance += p.distance(q);
                }
                totalDistance += distance;
    
                // Si la distancia total es mayor que la distancia mínima actual, detener las iteraciones
                if (totalDistance >= minTotalDistance) {
                    break;
                }
            }
    
            // Si la distancia total es menor que la distancia mínima actual, actualizar el medoide y la distancia mínima
            if (totalDistance < minTotalDistance) {
                minTotalDistance = totalDistance;
                medoid = points.get((int) (Math.random() * points.size())); // Selecciona un punto aleatorio como nuevo medoide
            } else {
                break; // Termina las iteraciones si la distancia total no mejora
            }
        }

        setMedioide(medoid);
        return medoid;
            
        }
        
    }
    public static double distance(Cluster c1, Cluster c2) {
        return c1.getMedioide().distance(c2.getMedioide());
    }

    public static ClusterPair closestPair(List<Cluster> clusters) {
        clusters.sort(Comparator.comparingDouble(c -> c.calculateMedioide().getX()));
        return closestPairUtil(clusters, clusters.size());
    }

    private static ClusterPair closestPairUtil(List<Cluster> clusters, int n) {
        if (n <= 3) {
            return bruteForce(clusters, n);
        }

        int mid = n / 2;
        Cluster midCluster = clusters.get(mid);

        ClusterPair dl = closestPairUtil(new ArrayList<>(clusters.subList(0, mid)), mid);
        ClusterPair dr = closestPairUtil(new ArrayList<>(clusters.subList(mid, clusters.size())), n - mid);

        double dlDist = distance(dl.getCluster1(), dl.getCluster2());
        double drDist = distance(dr.getCluster1(), dr.getCluster2());

        double d = Math.min(dlDist, drDist);
        ClusterPair dPair = dlDist < drDist ? dl : dr;

        List<Cluster> strip = new ArrayList<>();
        for (Cluster c : clusters) {
            if (Math.abs(c.getMedioide().getX() - midCluster.getMedioide().getX()) < d) {
                strip.add(c);
            }
        }

        strip.sort(Comparator.comparingDouble(c -> c.getMedioide().getY()));

        double min = d;
        ClusterPair minPair = dPair;

        for (int i = 0; i < strip.size(); ++i) {
            for (int j = i + 1; j < strip.size() && (strip.get(j).getMedioide().getY() - strip.get(i).getMedioide().getY()) < min; ++j) {
                if (distance(strip.get(i), strip.get(j)) < min) {
                    min = distance(strip.get(i), strip.get(j));
                    minPair = new ClusterPair(strip.get(i), strip.get(j));
                }
            }
        }

        return minPair;
    }

    private static ClusterPair bruteForce(List<Cluster> clusters, int n) {
        double min = Double.MAX_VALUE;
        ClusterPair minPair = null;

        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                if (distance(clusters.get(i), clusters.get(j)) < min) {
                    min = distance(clusters.get(i), clusters.get(j));
                    minPair = new ClusterPair(clusters.get(i), clusters.get(j));
                }
            }
        }

        return minPair;
    }
    
}
