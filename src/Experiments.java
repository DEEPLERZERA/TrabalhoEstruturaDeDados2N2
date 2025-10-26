import java.util.List;

/** Mede busca e remoção em ABB×AVL e retorna métricas agregadas. */
public class Experiments {
    /** Estrutura de resultado registrada em CSV (toString já formata). */
    public static final class Result {
        public final String op;                 // "search" ou "remove"
        public final long compsABB, compsAVL;   // comparações (via compareTo)
        public final long nsABB, nsAVL;         // tempo em nanos
        public Result(String op,long cA,long cV,long nA,long nV){
            this.op=op;this.compsABB=cA;this.compsAVL=cV;this.nsABB=nA;this.nsAVL=nV;
        }
        public String toString(){return op+","+compsABB+","+compsAVL+","+nsABB+","+nsAVL;}
    }

    /** Executa buscas para um conjunto de consultas e mede (ABB×AVL). */
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

    /** Remove um conjunto de chaves e mede tempos/comparações (ABB×AVL). */
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
