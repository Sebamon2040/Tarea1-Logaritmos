
import java.util.ArrayList;
import java.util.List;

public class MTreeNode {
    private ArrayList<Entry> entries;
    private boolean isLeaf;
    private double minCap;
    private double maxCap;
    private Point rootPoint;

    // Constructor
    public MTreeNode(boolean isLeaf, double maxCapacity) {
        this.entries = new ArrayList<>();
        this.isLeaf = isLeaf;
        this.minCap = Math.floor(maxCapacity / 2);
        this.maxCap = maxCapacity;
        this.rootPoint = null;
    }

    // Método para agregar una entrada al nodo
    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    public void setRootPoint(Point point) {
        this.rootPoint = point;
    }

    public Point getRootPoint() {
        return this.rootPoint;
    }

    // Método para extraer una entrada que contiene un punto dado
    public Entry extractEntryWithPoint(Point point) {
        for (Entry entry : entries) {
            if (entry.getPoint().equals(point)) {
                entries.remove(entry);
                return entry;
            }
        }
        return null; // Retorna null si no se encuentra ninguna entrada con el punto dado
    }

    // Método para agregar una entrada al inicio del nodo
    public void addEntryAtBeginning(Entry entry) {
        entries.add(0, entry);
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public List<Entry> getEntries() {
        return entries;
    }



    public int getHeight() {
        if (this.isLeaf()) {
            return 0;
        }
        int maxChildHeight = 0;
        for (Entry entry : this.getEntries()) {
            MTreeNode childSubtree = entry.getChildNode();
            int childHeight = childSubtree.getHeight();
            if (childHeight > maxChildHeight) {
                maxChildHeight = childHeight;
            }
        }
        return maxChildHeight + 1;
    }

}
