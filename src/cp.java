import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class cp {

    public static MTreeNode buildCp(List<Point> points, int B, int b) {
        // paso 1
        if (points.size() <= B) {
            MTreeNode leafNode = new MTreeNode(true, B);
            for (Point p : points) {
                leafNode.addEntry(new Entry(p, null, null));
            }
            return leafNode;
        }
        log.print("points: " + points.size() + "\n");
        // paso 2
        List<List<Point>> F = new ArrayList<>();
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
        for (List<Point> cluster : F) {
            MTreeNode sTree = buildCp(cluster, B, b);
            // añade el punto raiz de ese subarbol al inicio de su lista de entrys
            Point rootPoint = samples.get(F.indexOf(cluster)); // punto raiz del subarbol
            Entry rootEntry = sTree.extractEntryWithPoint(rootPoint);
            if (rootEntry != null) {
                sTree.addEntryAtBeginning(rootEntry); // añade el punto raiz al inicio
            }
            subTrees.add(sTree);
        }

        // Paso 7: Crear una copia de la lista subTrees para evitar
        // ConcurrentModificationException
        List<MTreeNode> subTreesCopy = new ArrayList<>(subTrees);
        for (MTreeNode subTree : subTreesCopy) {
            if (subTree.getEntries().size() < b) {
                int idx = subTrees.indexOf(subTree);
                samples.remove(idx); // elimino pfj de F(samples)
                subTrees.remove(subTree); // elimino el subarbol

                List<MTreeNode> childNodes = new ArrayList<>();
                // obtengo los subarboles que lo remplazaran
                for (Entry entry : subTree.getEntries()) {
                    if (entry != null) { // se salta la null
                        childNodes.add(entry.getChildNode());
                    }
                }
                // añado los nuevos subarboles a la lista de subarboles y pongo sus pfj en
                // F(samples)
                for (MTreeNode childNode : childNodes) {
                    subTrees.add(childNode); // añado el subarbol
                    Point rootPoint = childNode.getEntries().get(0).getPoint(); // obtengo el punto raiz
                    samples.add(rootPoint); // añado el punto raiz
                }
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
                // 9.1
                int idx = subTrees.indexOf(subTree);
                samples.remove(idx);
                // 9.2 y 9.3
                List<MTreeNode> nodesOfHeightH = getNodesOfHeightH(subTree, h);
                for (MTreeNode node : nodesOfHeightH) {
                    Tprime.add(node);
                    // añade el punto raiz de ese subarbol a samples
                    Point rootPoint = node.getEntries().get(0).getPoint();
                    samples.add(rootPoint);
                }
            }
        }
        // Paso 10
        System.out.println("Comenzando la construcción del super-árbol Tsup...");
        MTreeNode Tsup = buildCp(samples, B, b);
        System.out.println("Construcción de Tsup completada.");

        // Paso 11
        System.out.println("Uniendo los subárboles Tprime a las hojas correspondientes en Tsup...");
        for (MTreeNode Tj : Tprime) {
            // Encuentra la hoja en Tsup correspondiente al punto pfj en F
            // toma el indice de Tj en Tprime
            int idx = Tprime.indexOf(Tj);
            Point pfj = Tsup.getEntries().get(idx).getPoint(); // Asume que el punto pfj en F es el punto de
                                                               // la
                                                               // entrada en la raíz de Tsup en la posición
                                                               // idx
            System.out.println("Punto pfj: " + pfj);
            System.out.println("Buscando la hoja correspondiente en Tsup...");

            findLeaf(Tsup, Tj, pfj);
            log.print("is leaf?:" + Tsup.isLeaf()); // encuentra la hoja en Tsup correspondiente al punto pfj en F y
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

    public static void findLeaf(MTreeNode node, MTreeNode Tj, Point pfj) {
        MTreeNode Nodej = Tj;
        for (Entry entry : node.getEntries()) { // recorro las entrys, buscando la que tiene a pfj
            if (entry.getPoint().equals(pfj)) {
                entry.setChildNode(Nodej);
                node.setIsLeaf(false); // deja de ser una hoja pues ahora de el cuelga un subarbol
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