import java.util.List;

public class Experiments {

    public static final class Result {
        public final String op;
        public final long compsABB;
        public final long compsAVL;
        public final long nsABB;
        public final long nsAVL;
        public Result(String op, long compsABB, long compsAVL, long nsABB, long nsAVL){
            this.op = op; this.compsABB = compsABB; this.compsAVL = compsAVL; this.nsABB = nsABB; this.nsAVL = nsAVL;
        }
        @Override public String toString(){
            return op + "," + compsABB + "," + compsAVL + "," + nsABB + "," + nsAVL;
        }
    }

    // Benchmark de busca em ABB e AVL
    public static Result runSearch(MHLoader.Loaded L, List<MHRecord> queries){
        // ABB
        MHRecord.resetCounter();
        long t1 = System.nanoTime();
        for (MHRecord q : queries) { ABBSearch.search(L.abb, q); }
        long nsABB = System.nanoTime() - t1;
        long compsABB = MHRecord.COMPARE_COUNT;

        // AVL
        MHRecord.resetCounter();
        long t2 = System.nanoTime();
        for (MHRecord q : queries) { L.avl.searchAVL(q); }
        long nsAVL = System.nanoTime() - t2;
        long compsAVL = MHRecord.COMPARE_COUNT;

        return new Result("search", compsABB, compsAVL, nsABB, nsAVL);
    }

    // Benchmark de remoção em ABB e AVL
    public static Result runRemove(MHLoader.Loaded L, List<MHRecord> keysToRemove){
        // ABB
        MHRecord.resetCounter();
        long t1 = System.nanoTime();
        for (MHRecord k : keysToRemove) { L.abb.eliminar(k); }
        long nsABB = System.nanoTime() - t1;
        long compsABB = MHRecord.COMPARE_COUNT;

        // AVL
        MHRecord.resetCounter();
        long t2 = System.nanoTime();
        for (MHRecord k : keysToRemove) { L.avl.removeAVL(k); }
        long nsAVL = System.nanoTime() - t2;
        long compsAVL = MHRecord.COMPARE_COUNT;

        return new Result("remove", compsABB, compsAVL, nsABB, nsAVL);
    }
}
