import java.util.ArrayList;

public class MTree {
    private MTreeNode root;
    private int B;
    
    // Constructor
    public MTree(MTreeNode root) {
        this.root = root;
    }
    
    // Método de búsqueda
    public ArrayList<Point> search(Point q, double r) {
        ArrayList<Point> result = new ArrayList<>();
        searchHelper(root, q, r, result);
        return result;
    }
    
    // Método auxiliar para búsqueda recursiva
    private void searchHelper(MTreeNode node, Point q, double r, ArrayList<Point> result) {
        if (node.isLeaf()) {
            // Nodo es una hoja
            for (Entry entry : node.getEntries()) {
                if (distance(entry.getPoint(), q) <= r) {
                    result.add(entry.getPoint());
                    System.out.println("Punto " + entry.getPoint().getX() + entry.getPoint().getY() + " agregado al resultado.");
                }

            }
        } else {
            // Nodo es interno
            for (Entry entry : node.getEntries()) {
                if (distance(entry.getPoint(), q) <= entry.getCoveringRadius() + r) {
                    System.out.println("Explorando nodo interno con punto " + entry.getPoint().getX() + entry.getPoint().getY());
                    searchHelper(entry.getChildNode(), q, r, result);
                }
            }
        }
    }
    
    // Método para calcular la distancia euclidiana entre dos puntos
    private double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
    }


    public void addNode(MTreeNode node) {
        if (root == null) {
            root = node;
        } else {
            // Implementa lógica para agregar el nodo al árbol
        }
    }   
}
