import java.lang.instrument.Instrumentation;

public class SizeOfExample {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object obj) {
        if (instrumentation == null) {
            throw new IllegalStateException("Instrumentation environment not initialized.");
        }
        return instrumentation.getObjectSize(obj);
    }

    public static void main(String[] args) {
        // Crear una instancia de tu estructura
        Entry entry = new Entry(new Point(1, 2), 40.0, new MTreeNode(true, 50.0));

        // Obtener el tamaño del objeto
        long size = getObjectSize(entry);
        System.out.println("Tamaño de la estructura: " + size + " bytes");
    }
}
