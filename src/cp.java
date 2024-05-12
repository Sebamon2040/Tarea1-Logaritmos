import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class cp {

    public static MTreeNode buildCp(List<Point> points, int B, int b) {
        // paso 1
        if (points.size() <= B) {
            MTreeNode leafNode = new MTreeNode(true, B);
            for (Point p : points) {
                leafNode.addEntry(new Entry(p, null, null));
            }
            return leafNode;
        } else {
            log.print("points: " + points.size() + "\n");
            // paso 2
            List<Cluster> F = new ArrayList<>();
            List<Point> samples = new ArrayList<>();
            do {// se calcula el numero de puntos que se van a seleccionar para ser los
                // centroides
                int k = Math.min(B, (int) Math.ceil(((double) points.size()) / B));
                log.print("k: " + k + "\n");

                samples = chooseRandomSamples(points, k); // se seleccionan los centroides
                log.print("samples: " + samples.size() + "\n");

                // paso 3
                F = assingPointsToClosestSamples(points, samples);
                log.print("F: " + F.size() + "\n");
                // paso 4
                F = redistributeSamples(F, b, samples); // se redistribuyen los puntos en los clusters
                log.print("F: " + F.size() + "\n");
                // paso 5

            } while (F.size() == 1);
            // paso 6
            List<MTreeNode> subTrees = new ArrayList<>();
            for (Cluster cluster : F) {
                MTreeNode sTree = buildCp(cluster.getPuntos(), B, b);
                sTree.setRootPoint(cluster.getMedioide());

                // paso 7
                if (sTree.getEntries().size() < b) {
                    samples.remove(sTree.getRootPoint()); // elimino pfj de F(samples)
                    List<MTreeNode> childNodes = new ArrayList<>();
                    // obtengo los subarboles que lo remplazaran
                    for (Entry entry : sTree.getEntries()) {
                        if (entry != null) { // se salta la null
                            childNodes.add(entry.getChildNode());
                        }
                    }
                    // añado los nuevos subarboles a la lista de subarboles y pongo sus pfj en en
                    // F(samples)
                    for (MTreeNode childNode : childNodes) {
                        subTrees.add(childNode); // añado el subarbol
                        childNode.getRootPoint(); // obtengo el punto raiz
                        samples.add(childNode.getRootPoint()); // añado el punto raiz
                    }
                } else {
                    subTrees.add(sTree); // añado el subarbol
                }
            }

            // paso 8
            int h = Integer.MAX_VALUE;
            for (MTreeNode subTree : subTrees) {
                h = Math.min(h, subTree.getHeight());
            }
            List<MTreeNode> Tprime = new ArrayList<>(); // se inicializa T' como una lista vacia
            // paso 9
            for (MTreeNode subTree : subTrees) {
                if (subTree.getHeight() == h) {
                    Tprime.add(subTree);
                } else {
                    // 9.1 se borra el punto pertinenete en F(samples)
                    samples.remove(subTree.getRootPoint());
                    // 9.2 y 9.3, se obtienen los nodos de altura h y se inserta al T' y sus puntos
                    // a F(samples)
                    List<MTreeNode> nodesOfHeightH = getNodesOfHeightH(subTree, h);
                    for (MTreeNode node : nodesOfHeightH) {
                        // añade el nodo a T'
                        Tprime.add(node);
                        // añade el punto raiz de ese subarbol a samples
                        Point rootPoint = node.getRootPoint();
                        samples.add(rootPoint);
                    }
                }
            }
            // Paso 10
            System.out.println("Comenzando la construcción del super-árbol Tsup...");
            MTreeNode Tsup = buildCp(samples, B, b);
            log.print("Tsup is leaf?: " + Tsup.isLeaf() + "\n");
            for (Entry entry : Tsup.getEntries()) {
                log.print("entry: " + entry.getPoint() + "\n");
                log.print("entry child: " + entry.getChildNode() + "\n");
            }
            System.out.println("Construcción de Tsup completada.");

            // Paso 11
            System.out.println("Uniendo los subárboles Tprime a las hojas correspondientes en Tsup...");
            for (MTreeNode Tj : Tprime) {
                // Encuentra la hoja en Tsup correspondiente al punto pfj en F

                Point pfj = Tj.getRootPoint(); // obtengo el punto raiz de Tj

                findLeaf(Tsup, Tj, pfj);
                // encuentra la hoja en Tsup correspondiente al punto pfj en F y
                // une Tj a // esa hoja
            }

            // Paso 12
            System.out.println("Actualizando los radios cobertores para cada entrada en Tsup...");
            // Actualiza los radios cobertores para cada entrada en Tsup
            updateCoveringRadii(Tsup);
            System.out.println("Actualización de radios cobertores completada.");

            // Paso 13
            System.out.println("Retornando Tsup...");
            return Tsup;

        }
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

    private static List<Cluster> assingPointsToClosestSamples(List<Point> points, List<Point> samples) {
        List<Cluster> clusters = new ArrayList<>(samples.size());
        for (int i = 0; i < samples.size(); i++) {
            Cluster cluster = new Cluster(); // creo el conjunto de puntos
            cluster.setMedioide(samples.get(i)); // le asigno el punto a partir del cual se agruparan
            clusters.add(cluster); // añado el conjunto a la lista de conjuntos
        }

        for (Point point : points) {
            int closestSample = findClosestSample(point, clusters);
            clusters.get(closestSample).addPunto(point);
            ;
        }
        return clusters; // retorno el conjunto de clusters generados

    }

    private static int findClosestSample(Point point, List<Cluster> clusters) {
        double minDistance = Double.MAX_VALUE;
        int closestSample = -1;
        for (int i = 0; i < clusters.size(); i++) {
            double distance = point.distance(clusters.get(i).getMedioide());
            if (distance < minDistance) {
                minDistance = distance;
                closestSample = i;
            }
        }
        return closestSample;
    }

    private static List<Cluster> redistributeSamples(List<Cluster> F, int b, List<Point> samples) {
        List<Cluster> Fcopy = new ArrayList<>(F); // Crea una copia de F
        for (Cluster cluster : Fcopy) {
            if (cluster.getSize() < b) {
                samples.remove(cluster.getMedioide()); // saco el punto del sample
                F.remove(cluster); // saco el cluster del conjunto de clusters
                for (Point p : cluster.getPuntos()) {
                    int closestSample = findClosestSample(p, F);
                    F.get(closestSample).addPunto(p);
                }
            }
        }
        return F;
    }

    public static List<MTreeNode> getNodesOfHeightH(MTreeNode node, int h) {
        List<MTreeNode> nodesOfHeightH = new ArrayList<>();
        getNodesOfHeightHHelper(node, h, nodesOfHeightH);
        return nodesOfHeightH;
    }

    private static void getNodesOfHeightHHelper(MTreeNode node, int h,
            List<MTreeNode> nodesOfHeightH) {
        if (node.getHeight() == h) {
            nodesOfHeightH.add(node);
        } else if (node.isLeaf()) {
            return;
        } else {
            for (Entry entry : node.getEntries()) {
                getNodesOfHeightHHelper(entry.getChildNode(), h, nodesOfHeightH);
            }
        }
    }

    public static void findLeaf(MTreeNode Tsup, MTreeNode Tj, Point pfj) {
        for (Entry entry : Tsup.getEntries()) { // recorro las entrys, buscando la que tiene a pfj
            if (entry.getPoint().equals(pfj)) {
                entry.setChildNode(Tj);
                Tsup.setIsLeaf(false); // deja de ser una hoja pues ahora de el cuelga un subarbol
                return;
            }
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