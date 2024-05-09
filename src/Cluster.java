
import java.util.ArrayList;
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
    
}
