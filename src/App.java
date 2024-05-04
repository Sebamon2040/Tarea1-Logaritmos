import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        // Creamos algunos puntos para nuestro árbol

        Point queryPoint = new Point(2.0, 2.0); // Punto de búsqueda
        double searchRadius = 10.0; // Radio de búsqueda

        // Creamos un árbol M-Tree con una capacidad máxima de 2 entradas por nodo
        MTree tree = buildExampleTree();

        // Ahora podemos probar la función de búsqueda
        ArrayList<Point> searchResult = tree.search(queryPoint, searchRadius);
        System.out.println("Puntos encontrados dentro del radio de búsqueda:");
        for (Point point : searchResult) {
            System.out.println("(" + point.getX() + ", " + point.getY() + ")");
        }
    }

    // Método para construir un árbol de ejemplo
    public static MTree buildExampleTree() {
        // Creamos nodos hoja y agregamos puntos a ellos
        MTreeNode leafNode1 = new MTreeNode(true);
        leafNode1.addEntry(new Entry(new Point(1.0, 2.0), null, null)); // Punto 1
        leafNode1.addEntry(new Entry(new Point(3.0, 4.0), null, null)); // Punto 2

        MTreeNode leafNode2 = new MTreeNode(true);
        leafNode2.addEntry(new Entry(new Point(5.0, 6.0), null, null)); // Punto 3
        leafNode2.addEntry(new Entry(new Point(7.0, 8.0), null, null)); // Punto 4

        // Creamos un nodo interno y agregamos entradas que apuntan a los nodos hoja
        MTreeNode internalNode = new MTreeNode(false);
        internalNode.addEntry(new Entry(new Point(2.0, 3.0), 2.5, leafNode1)); // Entrada 1 con radio cobertor 2.5
        internalNode.addEntry(new Entry(new Point(6.0, 7.0), 2.5, leafNode2)); // Entrada 2 con radio cobertor 2.5

        // Creamos el árbol con la raíz que apunta al nodo interno
        return new MTree(internalNode);
    }
}
