/** Busca iterativa na ABB (1 compare por nó). */
public class ABBSearch {
    /** Retorna o registro encontrado ou null se não existir. */
    public static MHRecord search(ABB<MHRecord> abb, MHRecord key){
        Node cur = abb.getRaiz();
        while(cur!=null){
            MHRecord v=(MHRecord)cur.getValue();
            int c=key.compareTo(v);
            if(c==0) return v;
            cur = (c<0)?cur.getFilhoEsq():cur.getFilhoDir();
        }
        return null;
    }
}
