
public class ABBSearch {
    // Iterative search to count comparisons explicitly via MHRecord.compareTo
    public static MHRecord search(ABB<MHRecord> abb, MHRecord key) {
        Node cur = abb.getRaiz();
        while (cur != null) {
            MHRecord curVal = (MHRecord) cur.getValue();
            int c = key.compareTo(curVal);
            if (c == 0) return curVal;
            cur = (c < 0) ? cur.getFilhoEsq() : cur.getFilhoDir();
        }
        return null;
    }
}
