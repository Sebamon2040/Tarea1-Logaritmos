import java.util.ArrayList;

public class MTree {
    private static MTreeNode root;
    private double B;
    private int diskI_0s;

    // Constructor
    public MTree(MTreeNode root) {
        this.root = root;
    }

    public void resetDiskAccesess() {
        diskI_0s = 0;
    }

    public double getB() {
        return B;
    }

    public int getDiskAccesses() {
        return diskI_0s;
    }

    public void addDiskAccess() {
        diskI_0s++;
    }

    // Método de búsqueda
    public ArrayList<Point> search(Point q, double r) {
        resetDiskAccesess();
        ArrayList<Point> result = new ArrayList<>();
        searchHelper(root, q, r, result);
        System.err.println("Disk accesses: " + getDiskAccesses());
        return result;
    }

    // Método auxiliar para búsqueda recursiva
    private void searchHelper(MTreeNode node, Point q, double r, ArrayList<Point> result) {
        addDiskAccess();
        if (node.isLeaf()) {
            // Nodo es una hoja
            for (Entry entry : node.getEntries()) {
                if (distance(entry.getPoint(), q) <= r) {
                    result.add(entry.getPoint());

                }

            }
        } else {
            // Nodo es interno
            for (Entry entry : node.getEntries()) {
                if (distance(entry.getPoint(), q) <= entry.getCoveringRadius() + r) {

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

    public MTreeNode getRoot() {
        return root;
    }

    public int getHeight() {
        return root.getHeight();
    }
}
