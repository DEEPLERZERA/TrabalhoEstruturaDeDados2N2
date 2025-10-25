public final class ABBUtil {
    public static void abbInsertIter(ABB<MHRecord> abb, MHRecord r){
        Node novo=new Node(r);
        if(abb.getRaiz()==null){ abb.setRaiz(novo); return; }
        Node cur=abb.getRaiz();
        while(true){
            MHRecord v=(MHRecord)cur.getValue();
            int cmp=r.compareTo(v);
            if(cmp<0){
                if(cur.getFilhoEsq()==null){ cur.setFilhoEsq(novo); return; }
                cur=cur.getFilhoEsq();
            } else if(cmp>0){
                if(cur.getFilhoDir()==null){ cur.setFilhoDir(novo); return; }
                cur=cur.getFilhoDir();
            } else {
                return;
            }
        }
    }
}
