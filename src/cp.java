import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author elcar
 */
public class cp {

    public static MTree buildCp(List<Point> points, int B, int b) {
        // paso 1
        if (points.size() <= B) { // si el numero de puntos es menor o igual a la capacidad minima del nodo, se
                                  // crea un arbol y se insertan todos los puntos
            MTreeNode leafNode = new MTreeNode(true, B);
            for (Point p : points) {
                leafNode.addEntry(new Entry(p, null, null));
            }
            return new MTree(leafNode);
        }
        // paso 2
        int k = Math.min(B, points.size() / B); // se calcula el numero de puntos que se van a seleccionar para ser los
                                                // centroides

        List<Point> samples = chooseRandomSamples(points, k); // se seleccionan los centroides
        // paso 3
        List<List<Point>> F = assingPointsToClosestSamples(points, samples);
        // paso 4
        List<List<Point>> Fr = redistributeSamples(F, b, samples); // se redistribuyen los puntos en los clusters
        // paso 5
        if (Fr.size() == 1) { // si solo hay un cluster, se rehace el proceso
            return buildCp(Fr.get(0), B, b);
        }
        // paso 6
        List<MTree> subTrees = new ArrayList<>();
        for (List<Point> cluster : Fr) {
            MTree subTree = buildCp(cluster, B, b);
            subTrees.add(subTree);
        }

        // paso 7
        for (MTree subTree : subTrees) {
            if (subTree.getRoot().getEntries().size() < b) {
                int idx = subTrees.indexOf(subTree);
                samples.remove(idx);

                List<MTreeNode> childNodes = new ArrayList<>();
                for (Entry entry : subTree.getRoot().getEntries()) {
                    childNodes.add(entry.getChildNode());
                }

                for (MTreeNode childNode : childNodes) {
                    for (Entry entry : childNode.getEntries()) {
                        samples.add(entry.getPoint());
                    }
                }
            }
        }

        // paso 8
        int h = Integer.MAX_VALUE;
        for (MTree subTree : subTrees) {
            h = Math.min(h, subTree.getHeight());
        }
        List<MTree> Tprime = new ArrayList<>(); // se inicializa T' como una lista vacia

        // paso 9
        for (MTree subTree : subTrees) {
            if (subTree.getHeight() == h) {
                Tprime.add(subTree);
            } else {
                // 9.1
                int idx = subTrees.indexOf(subTree);
                samples.remove(idx);
                // 9.2 y 9.3
                List<MTreeNode> nodesOfHeightH = getNodesOfHeightH(subTree.getRoot(), h);
                for (MTreeNode node : nodesOfHeightH) {
                    MTree newSubTree = new MTree(node);
                    Tprime.add(newSubTree);
                    for (Entry entry : node.getEntries()) {
                        samples.add(entry.getPoint());
                    }
                }
            }
        }
        // paso 10
        MTree Tsup = buildCp(samples, B, b);
        // paso 11
        for (MTree Tj : Tprime) {
            // Encuentra la hoja en Tsup correspondiente al punto pfj en F
            Point pfj = Tj.getRoot().getEntries().get(0).getPoint(); // Asume que pfj es el primer punto en la raíz de
                                                                     // Tj
            MTreeNode leaf = findLeaf(Tsup.getRoot(), pfj);

            // Une Tj a la hoja en Tsup
            leaf.addEntry(new Entry(pfj, null, Tj.getRoot()));
        }

        // Paso 12
        // Actualiza los radios cobertores para cada entrada en Tsup
        updateCoveringRadii(Tsup.getRoot());

        // Paso 13
        return Tsup;

    }

    private static List<Point> chooseRandomSamples(List<Point> points, int k) {
        Random random = new Random();
        List<Point> samples = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            int index = random.nextInt(points.size());
            samples.add(points.get(index));
        }
        return samples;
    }

    private static List<List<Point>> assingPointsToClosestSamples(List<Point> points, List<Point> samples) {
        List<List<Point>> clusters = new ArrayList<>(samples.size());
        for (int i = 0; i < samples.size(); i++) {
            clusters.add(new ArrayList<>());
        }

        for (Point point : points) {
            int closestSample = findClosestSample(point, samples);
            clusters.get(closestSample).add(point);
        }
        return clusters;

    }

    private static int findClosestSample(Point point, List<Point> samples) {
        double minDistance = Double.MAX_VALUE;
        int closestSample = -1;
        for (int i = 0; i < samples.size(); i++) {
            double distance = point.distance(samples.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                closestSample = i;
            }
        }
        return closestSample;
    }

    private static List<List<Point>> redistributeSamples(List<List<Point>> F, int b, List<Point> samples) {
        List<List<Point>> Fcopy = new ArrayList<>(F); // Crea una copia de F
        for (List<Point> cluster : Fcopy) {
            if (cluster.size() < b) {
                int idx = F.indexOf(cluster); // veo el indice que sacare
                samples.remove(idx); // saco el sample
                F.remove(cluster); // saco el cluster
                for (Point p : cluster) {
                    int closestSample = findClosestSample(p, samples);
                    F.get(closestSample).add(p);
                }
            }
        }
        return F;
    }

    public static List<MTreeNode> getNodesOfHeightH(MTreeNode node, int h) {
        List<MTreeNode> nodesOfHeightH = new ArrayList<>();
        getNodesOfHeightHHelper(node, h, 0, nodesOfHeightH);
        return nodesOfHeightH;
    }

    private static void getNodesOfHeightHHelper(MTreeNode node, int h, int currentHeight,
            List<MTreeNode> nodesOfHeightH) {
        if (currentHeight == h) {
            nodesOfHeightH.add(node);
        } else if (node.isLeaf()) {
            return;
        } else {
            for (Entry entry : node.getEntries()) {
                getNodesOfHeightHHelper(entry.getChildNode(), h, currentHeight + 1, nodesOfHeightH);
            }
        }
    }

    public static MTreeNode findLeaf(MTreeNode node, Point p) {
        if (node.isLeaf()) {
            for (Entry entry : node.getEntries()) {
                if (entry.getPoint().equals(p)) {
                    return node;
                }
            }
            return null;
        } else {
            for (Entry entry : node.getEntries()) {
                MTreeNode foundNode = findLeaf(entry.getChildNode(), p);
                if (foundNode != null) {
                    return foundNode;
                }
            }
            return null;
        }
    }

    public static void updateCoveringRadii(MTreeNode node) {
        if (node.isLeaf()) {
            return;
        } else {
            for (Entry entry : node.getEntries()) {
                MTreeNode childNode = entry.getChildNode();
                updateCoveringRadii(childNode);

                double maxDistance = 0;
                Point entryPoint = entry.getPoint();
                for (Entry childEntry : childNode.getEntries()) {
                    Point childPoint = childEntry.getPoint();
                    double distance = entryPoint.distance(childPoint);
                    if (distance > maxDistance) {
                        maxDistance = distance;
                    }
                }
                entry.setCoveringRadius(maxDistance);
            }
        }
    }

}