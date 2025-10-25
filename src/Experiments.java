import java.util.List;

public class Experiments {
    public static final class Result {
        public final String op;
        public final long compsABB, compsAVL, nsABB, nsAVL;
        public Result(String op,long cA,long cV,long nA,long nV){
            this.op=op;this.compsABB=cA;this.compsAVL=cV;this.nsABB=nA;this.nsAVL=nV;
        }
        public String toString(){return op+","+compsABB+","+compsAVL+","+nsABB+","+nsAVL;}
    }

    public static Result runSearch(MHLoader.Loaded L, List<MHRecord> queries){
        MHRecord.resetCounter();
        long t1=System.nanoTime();
        for(MHRecord q:queries) ABBSearch.search(L.abb,q);
        long nsABB=System.nanoTime()-t1;
        long cA=MHRecord.COMPARE_COUNT;

        MHRecord.resetCounter();
        long t2=System.nanoTime();
        for(MHRecord q:queries) L.avl.searchAVL(q);
        long nsAVL=System.nanoTime()-t2;
        long cV=MHRecord.COMPARE_COUNT;
        return new Result("search",cA,cV,nsABB,nsAVL);
    }

    public static Result runRemove(MHLoader.Loaded L, List<MHRecord> keys){
        MHRecord.resetCounter();
        long t1=System.nanoTime();
        for(MHRecord k:keys) L.abb.eliminar(k);
        long nsABB=System.nanoTime()-t1;
        long cA=MHRecord.COMPARE_COUNT;

        MHRecord.resetCounter();
        long t2=System.nanoTime();
        for(MHRecord k:keys) L.avl.removeAVL(k);
        long nsAVL=System.nanoTime()-t2;
        long cV=MHRecord.COMPARE_COUNT;
        return new Result("remove",cA,cV,nsABB,nsAVL);
    }
}
