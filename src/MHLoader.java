import java.io.*;
import java.util.*;

public class MHLoader {
    public static class Loaded {
        public final ABB<MHRecord> abb;
        public final AVL avl;
        public final java.util.List<MHRecord> records;
        public final long compsABB_insert, compsAVL_insert, rotSingle_insert, rotEvents_insert;
        public Loaded(ABB<MHRecord> a, AVL v, java.util.List<MHRecord> r, long cA,long cV,long s,long e){
            abb=a; avl=v; records=r; compsABB_insert=cA; compsAVL_insert=cV; rotSingle_insert=s; rotEvents_insert=e;
        }
    }

    public static Loaded load(String csvPath) throws Exception {
        java.util.List<MHRecord> records = readAll(csvPath);
        java.util.Collections.shuffle(records,new java.util.Random(42));

        ABB<MHRecord> abb = new ABB<>();
        MHRecord.resetCounter();
        for(MHRecord r:records) ABBUtil.abbInsertIter(abb,r);
        long cAbb = MHRecord.COMPARE_COUNT;

        AvlMetrics.reset();
        MHRecord.resetCounter();
        AVL avl=null;
        for(MHRecord r:records){
            if(avl==null) avl=new AVL(r); else avl.insereAVL(r);
        }
        long cAvl=MHRecord.COMPARE_COUNT;
        long rotS=AvlMetrics.rotationsSingle, rotE=AvlMetrics.rotationsEvents;

        return new Loaded(abb,avl,records,cAbb,cAvl,rotS,rotE);
    }

    private static java.util.List<MHRecord> readAll(String csvPath) throws Exception {
        java.util.List<MHRecord> list=new java.util.ArrayList<>(200000);
        try(BufferedReader br=new BufferedReader(new FileReader(csvPath))){
            String header=br.readLine();
            String line; int row=0;
            while((line=br.readLine())!=null){
                String[] t = line.split(",",-1);
                if(t.length<16) continue;
                list.add(new MHRecord(t,row++));
            }
        }
        return list;
    }
}
