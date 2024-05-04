
import java.util.ArrayList;
import java.util.List;


public class MTreeNode {
    private ArrayList<Entry> entries;
    private boolean isLeaf;
    private double minCap;
    private double maxCap;
    
    // Constructor
    public MTreeNode(boolean isLeaf, double maxCapacity) {
        this.entries = new ArrayList<>();
        this.isLeaf = isLeaf;
        this.minCap = Math.floor(maxCapacity/2);
        this.maxCap = maxCapacity;
    }
    
    // Método para agregar una entrada al nodo
    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public List<Entry> getEntries() {
        return entries;
    }
    
    
    
}
