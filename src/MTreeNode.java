
import java.util.ArrayList;
import java.util.List;


public class MTreeNode {
    private ArrayList<Entry> entries;
    private boolean isLeaf;
    
    // Constructor
    public MTreeNode(boolean isLeaf) {
        this.entries = new ArrayList<>();
        this.isLeaf = isLeaf;
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
