import java.util.*;

/** Range queries (ABB/AVL) com limite de retorno e filtro opcional. */
public final class RangeQueries {
    /** Predicado opcional para filtrar registros durante a travessia. */
    public interface Checker{ boolean accept(MHRecord r); }

    /** Intervalo na ABB: in-order limitado por [lo,hi] + filtro + limite. */
    public static java.util.List<MHRecord> abbRange(ABB<MHRecord> abb, MHRecord lo, MHRecord hi, int limit, Checker check){
        java.util.List<MHRecord> out=new java.util.ArrayList<>();
        traverseABB(abb.getRaiz(),lo,hi,limit,check,out);
        return out;
    }
    /** Travessia recursiva da ABB respeitando [lo,hi] e limite. */
    private static void traverseABB(Node n, MHRecord lo, MHRecord hi, int limit, Checker check, java.util.List<MHRecord> out){
        if(n==null || (limit>0 && out.size()>=limit)) return;
        MHRecord v=(MHRecord)n.getValue();
        if(v.compareTo(lo)>=0) traverseABB(n.getFilhoEsq(),lo,hi,limit,check,out);
        if(v.compareTo(lo)>=0 && v.compareTo(hi)<=0 && (check==null || check.accept(v))) out.add(v);
        if(v.compareTo(hi)<=0) traverseABB(n.getFilhoDir(),lo,hi,limit,check,out);
    }

    /** Intervalo na AVL: in-order limitado por [lo,hi] + filtro + limite. */
    public static java.util.List<MHRecord> avlRange(AVL avl, MHRecord lo, MHRecord hi, int limit, Checker check){
        java.util.List<MHRecord> out=new java.util.ArrayList<>();
        traverseAVL(avl.getRaiz(),lo,hi,limit,check,out);
        return out;
    }
    /** Travessia recursiva da AVL respeitando [lo,hi] e limite. */
    private static void traverseAVL(NoAVL n, MHRecord lo, MHRecord hi, int limit, Checker check, java.util.List<MHRecord> out){
        if(n==null || (limit>0 && out.size()>=limit)) return;
        MHRecord v=(MHRecord)n.getDado();
        if(v.compareTo(lo)>=0) traverseAVL(n.getEsq(),lo,hi,limit,check,out);
        if(v.compareTo(lo)>=0 && v.compareTo(hi)<=0 && (check==null || check.accept(v))) out.add(v);
        if(v.compareTo(hi)<=0) traverseAVL(n.getDir(),lo,hi,limit,check,out);
    }
}
