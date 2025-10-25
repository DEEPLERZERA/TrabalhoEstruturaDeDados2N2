
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MHLoader {

    public static class Loaded {
        public final ABB<MHRecord> abb;
        public final AVL avl;
        public final List<MHRecord> records; // to allow search experiments
        public Loaded(ABB<MHRecord> abb, AVL avl, List<MHRecord> records){
            this.abb = abb; this.avl = avl; this.records = records;
        }
    }

    public static Loaded load(String csvPath) throws Exception {
        AvlMetrics.reset();
        ABB<MHRecord> abb = new ABB<>();
        AVL avl = null;

        List<MHRecord> records = new ArrayList<>(200_000);

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String header = br.readLine(); // skip header
            String line;
            int row = 0;
            MHRecord first = null;
            while ((line = br.readLine()) != null) {
                String[] t = splitCSV(line);
                if (t.length < 16) continue; // guard
                MHRecord r = new MHRecord(t, row++);
                records.add(r);

                // ABB insert via Node
                Node<MHRecord> node = new Node<>(r);
                abb.inserir(node, abb.getRaiz());

                // AVL: init once then insert the rest
                if (first == null) {
                    first = r;
                    avl = new AVL(first);
                } else {
                    avl.insereAVL(r);
                }
            }
        }
        return new Loaded(abb, avl, records);
    }

    // Minimal CSV split (no quoted commas in this dataset)
    private static String[] splitCSV(String line) {
        return line.split(",", -1);
    }
}
