import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("LanguageDetectionInspection")
public class cp {

    public static MTreeNode buildCp(List<Point> points, int B, int b) {
        log.print("paso 1");
        if (points.size() <= B) {
            log.print("tamaño de los puntos es menor o igual a B\n");
            MTreeNode leafNode = new MTreeNode(true, B);
            for (Point p : points) {
                leafNode.addEntry(new Entry(p, null, null));
            }
            return leafNode;
        }
        log.print("points: " + points.size() + "\n");
        log.print("paso 2");
        List<Cluster> F = new ArrayList<>();

        do {// se calcula el numero de puntos que se van a seleccionar para ser los
            // medioides
            int k = Math.min(B, (int) Math.ceil(((double) points.size()) / B));
            log.print("k: " + k + "\n");

            F = chooseRandomSamples(points, k); // se seleccionan los centroides
            //con esto tenemos k clusters de un solo punto.
            log.print("Cantidad de clusters: " + F.size() + "\n");

            log.print("paso 3");
            F = assingPointsToClosestSamples(points, F); // se asignan los puntos a los clusters
            log.print("F: " + F.size() + "\n");
            log.print("paso 4");
            F = redistributeSamples(F, b); // se redistribuyen los puntos en los clusters
            log.print("F: " + F.size() + "\n");
            log.print("paso 5");

        } while (F.size() == 1);
        log.print("paso 6");
        List<MTreeNode> subTrees = new ArrayList<>();
        for (Cluster F_j : F) {
            List<Point> pointsF_j = F_j.getPuntos(); //obtenemos los puntos de cada cluster
            MTreeNode sTree = buildCp(pointsF_j, B, b); //creamos árboles para cada cluster de manera recursiva
            subTrees.add(sTree); //añadimos el subarbol a la lista de subarboles
            //tendremos un subarbol por cada cluster, cada subarbol tendrá todos los puntos que fueron asignados al sample(medioide)
            log.print("paso 7");
            //si es que el subarbol tiene menos de b puntos, se extraen todos los puntos del subarbol y se añaden a la lista de puntos
            if (sTree.getEntries().size() < b) {
                //extraemos todos los puntos (entradas) del subarbol
                List<Point> puntos = new ArrayList<>();
                for (Entry entry : sTree.getEntries()) {
                    puntos.add(entry.getPoint());
                }
                //obtenemos el medioide de cluster.
                Point medioide = F_j.getMedioide();
                //eliminamos el cluster
                F.remove(F_j);
                //creamos nuevos clusters con cada uno de los puntos del subarbol, exceptuando el medioide
                for (Point p : puntos) {

                    Cluster cluster = new Cluster();
                    cluster.addPunto(p);
                    cluster.setMedioide(p);
                    F.add(cluster);

                }

            }
            log.print("paso 8");
            int h = Integer.MAX_VALUE;
            /*
            Obtenemos el menor de los altos de los subarboles
             */
            for (MTreeNode subTree : subTrees) {
                h = Math.min(h, subTree.getHeight());
            }
            List<MTreeNode> Tprime = new ArrayList<>(); // se inicializa T' como una lista vacia
            // paso 9
            log.print("paso 9");
            for (MTreeNode subTree : subTrees){
                if (subTree.getHeight() == h ){
                    Tprime.add(subTree);
                }
                else {
                    //obtenemos el indice del subarbol en la lista
                    int idx = subTrees.indexOf(subTree);
                    //obtenemos el cluster que originó al subarbol
                    Cluster cluster = F.get(idx);
                    //eliminamos el cluster
                    F.remove(cluster);


                }
            }
            // paso 10
            log.print("paso 10");
            MTreeNode Tsup = buildCp(points, B, b);
            // paso 11
            log.print("paso 11");




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
    //retorna una lista de clusters con un solo punto, el cual es el medioide (el sample).
    private static List<Cluster> chooseRandomSamples(List<Point> points, int k) {
        Random random = new Random();
        List<Cluster> F = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            Cluster clusterF_j = new Cluster();
            Point sample = points.get(random.nextInt(points.size()));
            points.remove(sample); // sacamos al sample de la lista de puntos
            clusterF_j.addPunto(sample); // añadimos el punto
            clusterF_j.setMedioide(sample); //seteamos ese punto como medioide
            F.add(clusterF_j); //añadimos el cluster a la lista de clusters.
        }
        return F;
    }
    /*
    Encuentra el cluster mas cercano entre la lista de clusters de cada punto .
     */
    private static List<Cluster> assingPointsToClosestSamples(List<Point> points, List<Cluster> clusters) {
        for (Point p : points) {
            double minDistance = Double.MAX_VALUE;
            Cluster closestCluster = null;
            for (Cluster cluster : clusters) {
                double distance = p.distance(cluster.getMedioide());
                if (distance < minDistance) {
                    minDistance = distance;
                    closestCluster = cluster;
                }
            }
            closestCluster.addPunto(p);
        }
        return clusters;
    }

    private static List<Cluster> redistributeSamples(List<Cluster> F, int b) {
        List<Cluster> Fcopy = new ArrayList<>(F); // Crea una copia de F

        for (Cluster F_j : Fcopy) {
            if (F_j.getSize() < b){
                List<Point> puntos =  F_j.getPuntos(); //obtenemos todos los puntos del cluster que vamos a borrar
                Fcopy.remove(F_j); //eliminamos el cluster
                for (Point p: puntos){
                    assingPointsToClosestSamples(puntos, Fcopy);  //agregamos los puntos solitarios a su nuevo sample (medioide) mas cercano
                }
            }

        }
       return Fcopy;
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