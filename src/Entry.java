public class Entry {
    private final Point point;
    private final Double coveringRadius;
    private final MTreeNode childNode; // Solo se usa si este es un nodo interno
    
    // Constructor
    public Entry(Point point, Double coveringRadius, MTreeNode childNode) {
        this.point = point;
        this.coveringRadius = coveringRadius;
        this.childNode = childNode;
    }

    public Point getPoint() {
        return this.point;
    }

    public double getCoveringRadius() {
        return this.coveringRadius;
    }

    public MTreeNode getChildNode() {
        return this.childNode;
    }
}