import java.nio.file.*;
import java.io.*;
import java.util.*;

/** Console app com menu para ABB×AVL e análises. */
public class App {
    private static MHLoader.Loaded L=null;
    private static String CSV_PATH = Paths.get("data","mental_health_clean.csv").toString();
    private static final Scanner SC=new Scanner(System.in);
    private static final Set<Integer> removedIds=new HashSet<>();

    public static void main(String[] args) throws Exception {
        for(;;){
            printMenu();
            String op=SC.nextLine().trim();
            try{
                switch(op){
                    case "1": menuLoad(); break;
                    case "2": requireLoaded(); menuSearchBenchmark(); break;
                    case "3": requireLoaded(); menuRemoveExperiment(); break;
                    case "4": requireLoaded(); menuAnalyticsFixed(); break;
                    case "5": requireLoaded(); showStatus(); break;
                    case "6": requireLoaded(); menuInsertRecord(); break;
                    case "7": requireLoaded(); menuSearchInteractive(); break;
                    case "8": requireLoaded(); menuRemoveInteractive(); break;
                    case "9": requireLoaded(); menuAnalyticsAdHoc(); break;
                    case "0": System.out.println("Saindo..."); return;
                    default: System.out.println("Opção inválida.");
                }
            } catch(IllegalStateException ex){}
        }
    }

    private static void printMenu(){
        System.out.println("\n=== MENU ABB × AVL – Mental Health ===");
        System.out.println("1) Carregar/Recarregar CSV");
        System.out.println("2) Benchmark BUSCA (3x, média)");
        System.out.println("3) Experimento REMOÇÃO (+ rotações AVL)");
        System.out.println("4) Gerar análises exploratórias (5+ CSVs)");
        System.out.println("5) Status/contadores");
        System.out.println("6) Inserir DADO manualmente");
        System.out.println("7) Buscar (EXATA/INTERVALO)");
        System.out.println("8) Remover DADO específico");
        System.out.println("9) Análises AD HOC");
        System.out.println("0) Sair");
        System.out.print("Escolha: ");
    }

    private static void requireLoaded(){
        if(L==null){
            System.out.println("Primeiro carregue CSV (1).");
            throw new IllegalStateException("CSV não carregado");
        }
    }

    private static void menuLoad() throws Exception {
        System.out.print("Caminho do CSV ["+CSV_PATH+"]: ");
        String in=SC.nextLine().trim();
        if(!in.isEmpty()) CSV_PATH=in;
        if(!Files.exists(Paths.get(CSV_PATH))){
            System.out.println("Arquivo não encontrado: "+CSV_PATH);
            return;
        }
        L = MHLoader.load(CSV_PATH);
        removedIds.clear();
        System.out.println("Registros carregados: "+L.records.size());
        try(BufferedWriter w=Files.newBufferedWriter(Paths.get("rotations.csv"))){
            w.write("phase,rotations_single,rotations_events\n");
            w.write("load_inserts,"+L.rotSingle_insert+","+L.rotEvents_insert+"\n");
        }
        try(BufferedWriter w=Files.newBufferedWriter(Paths.get("metrics.csv"))){
            w.write("op,compsABB,compsAVL,nsABB,nsAVL\n");
            w.write("insert,"+L.compsABB_insert+","+L.compsAVL_insert+",0,0\n");
        }
    }

    private static void menuSearchBenchmark() throws Exception {
        System.out.print("Qtde de consultas (default ~5000): ");
        String in=SC.nextLine().trim();
        int n=L.records.size();
        int q=in.isEmpty()?Math.max(1,n/5000):Integer.parseInt(in);
        List<MHRecord> queries=sampleEvenly(L.records,q,false);
        int runs=3;
        long sumABB=0,sumAVL=0;
        for(int i=1;i<=runs;i++){
            Experiments.Result r=Experiments.runSearch(L,queries);
            sumABB+=r.nsABB; sumAVL+=r.nsAVL;
            try(BufferedWriter w=Files.newBufferedWriter(Paths.get("metrics.csv"), java.nio.file.StandardOpenOption.APPEND)){
                w.write("search_run"+i+","+r.compsABB+","+r.compsAVL+","+r.nsABB+","+r.nsAVL+"\n");
            }
        }
        long avgABB=sumABB/runs, avgAVL=sumAVL/runs;
        try(BufferedWriter w=Files.newBufferedWriter(Paths.get("metrics.csv"), java.nio.file.StandardOpenOption.APPEND)){
            w.write("search_avg,-,-,"+avgABB+","+avgAVL+"\n");
        }
        System.out.println("Busca concluída (3x). Média gravada em metrics.csv (search_avg).");
    }

    private static void menuRemoveExperiment() throws Exception {
        System.out.print("Percentual a remover (ex 10 para 10%): ");
        String in=SC.nextLine().trim();
        int n=L.records.size();
        int pct=in.isEmpty()?10:Integer.parseInt(in);
        int cut=Math.max(1,(int)Math.round(n*(pct/100.0)));
        java.util.List<MHRecord> toRemove=sampleEvenly(L.records,cut,true);
        long preS=AvlMetrics.rotationsSingle, preE=AvlMetrics.rotationsEvents;
        Experiments.Result r=Experiments.runRemove(L,toRemove);
        long dS=AvlMetrics.rotationsSingle-preS, dE=AvlMetrics.rotationsEvents-preE;
        try(BufferedWriter w=Files.newBufferedWriter(Paths.get("rotations.csv"), java.nio.file.StandardOpenOption.APPEND)){
            w.write("removals,"+dS+","+dE+"\n");
        }
        try(BufferedWriter w=Files.newBufferedWriter(Paths.get("metrics.csv"), java.nio.file.StandardOpenOption.APPEND)){
            w.write(r.toString()+"\n");
        }
        System.out.println("Remoção concluída. Δ rotações AVL = single="+dS+" events="+dE);
    }

    private static void menuAnalyticsFixed() throws Exception {
        Analytics.treatmentByCountryGender(L.records);
        Analytics.daysIndoorsByCountry(L.records);
        Analytics.riskByCountry(L.records);
        Analytics.familyHistoryEffectOnTreatment(L.records);
        Analytics.stressMoodMatrix(L.records);
        System.out.println("Gerados: out_*.csv");
    }

    private static void showStatus(){
        int n=L==null?0:L.records.size();
        System.out.println("Carregado? "+(L!=null));
        System.out.println("Registros: "+n);
        System.out.println("Removidos (sessão): "+removedIds.size());
        System.out.println("AVL rotations → single="+AvlMetrics.rotationsSingle+" events="+AvlMetrics.rotationsEvents);
    }

    private static void menuInsertRecord(){
        int rowId=L.records.size();
        String country=InputUtil.ask("Country","USA");
        String gender=InputUtil.oneOf("Gender","Male","Male","Female");
        String occupation=InputUtil.ask("Occupation","Others");
        String selfEmp=InputUtil.oneOf("SelfEmployed","No","Yes","No","Unknown");
        String familyHist=InputUtil.oneOf("FamilyHistory","No","Yes","No");
        String treatment=InputUtil.oneOf("Treatment","No","Yes","No");
        String daysInd=InputUtil.oneOf("DaysIndoors","1-14 days","Go out Every day","1-14 days","15-30 days","31-60 days","More than 2 months");
        String habits=InputUtil.oneOf("HabitsChange","No","Yes","No","Maybe");
        String mentalHist=InputUtil.oneOf("MentalHealthHistory","No","Yes","No","Maybe");
        String stress=InputUtil.oneOf("IncreasingStress","No","Yes","No","Maybe");
        String mood=InputUtil.oneOf("MoodSwings","Low","Low","Medium","High");
        String socialWeak=InputUtil.oneOf("SocialWeakness","No","Yes","No","Maybe");
        String coping=InputUtil.oneOf("CopingStruggles","No","Yes","No");
        String work=InputUtil.oneOf("WorkInterest","No","Yes","No","Maybe");
        String interview=InputUtil.oneOf("MentalHealthInterview","No","Yes","No","Maybe");
        String care=InputUtil.oneOf("CareOptions","No","Yes","No","Maybe");
        MHRecord rec=MHRecord.makeForInsert(rowId,gender,country,occupation,selfEmp,familyHist,treatment,daysInd,habits,mentalHist,stress,mood,socialWeak,coping,work,interview,care);
        ABBUtil.abbInsertIter(L.abb,rec);
        L.avl.insereAVL(rec);
        L.records.add(rec);
        System.out.println("Inserido: "+rec);
    }

    private static void menuSearchInteractive(){
        System.out.println("1) Busca EXATA (Country,rowId)\n2) Busca por INTERVALO");
        String op=SC.nextLine().trim();
        if("1".equals(op)){
            String country=InputUtil.ask("Country","USA");
            int rowId=InputUtil.askInt("rowId",0);
            MHRecord key=MHRecord.makeBound(country,rowId);
            MHRecord r1=ABBSearch.search(L.abb,key);
            NoAVL n=L.avl.searchAVL(key);
            MHRecord r2=n==null?null:(MHRecord)n.getDado();
            System.out.println("ABB → "+(r1==null?"não encontrado":r1));
            System.out.println("AVL → "+(r2==null?"não encontrado":r2));
        } else {
            String c1=InputUtil.ask("Country INICIAL (* para menor)","*");
            String c2=InputUtil.ask("Country FINAL (* para maior)","*");
            int r1=InputUtil.askInt("rowId INICIAL",0);
            int r2=InputUtil.askInt("rowId FINAL",999999999);
            String loC="*".equals(c1)?"":c1;
            String hiC="*".equals(c2)?"zzzzzzzz":c2;
            MHRecord lo=MHRecord.makeBound(loC,r1);
            MHRecord hi=MHRecord.makeBound(hiC,r2);
            int limit=InputUtil.askInt("Limite para mostrar",20);
            java.util.List<MHRecord> a=RangeQueries.abbRange(L.abb,lo,hi,limit,null);
            java.util.List<MHRecord> v=RangeQueries.avlRange(L.avl,lo,hi,limit,null);
            System.out.println("ABB count="+a.size()+" | AVL count="+v.size());
            for(int i=0;i<Math.min(limit,a.size());i++) System.out.println("  "+a.get(i));
        }
    }

    private static void menuRemoveInteractive(){
        String country=InputUtil.ask("Country","USA");
        int rowId=InputUtil.askInt("rowId",0);
        MHRecord key=MHRecord.makeBound(country,rowId);
        long preS=AvlMetrics.rotationsSingle, preE=AvlMetrics.rotationsEvents;
        boolean ok1=L.abb.eliminar(key);
        boolean ok2=L.avl.removeAVL(key);
        long dS=AvlMetrics.rotationsSingle-preS, dE=AvlMetrics.rotationsEvents-preE;
        if(ok1||ok2) removedIds.add(rowId);
        System.out.println("ABB remove="+ok1+" | AVL remove="+ok2+" | rotationsΔ single="+dS+" events="+dE);
    }

    private static void menuAnalyticsAdHoc(){
        String country=InputUtil.ask("Country (*=todos)","*");
        String gender=InputUtil.oneOf("Gender (*,Male,Female)","*","*","Male","Female");
        String treatment=InputUtil.oneOf("Treatment (*,Yes,No)","*","*","Yes","No");
        String metric=InputUtil.oneOf("Métrica (count,treated,avgrisk)","count","count","treated","avgrisk");
        int n=0, treatedN=0; double sumRisk=0;
        for(MHRecord r:L.records){
            if(!"*".equals(country) && !r.country.equalsIgnoreCase(country)) continue;
            if(!"*".equals(gender) && !r.gender.equalsIgnoreCase(gender)) continue;
            if(!"*".equals(treatment) && !r.treatment.equalsIgnoreCase(treatment)) continue;
            n++; if("Yes".equalsIgnoreCase(r.treatment)) treatedN++; sumRisk+=r.riskScore();
        }
        if("count".equalsIgnoreCase(metric)) System.out.println("COUNT = "+n);
        else if("treated".equalsIgnoreCase(metric)) System.out.println("TREATED = "+treatedN);
        else System.out.println("AVGRISK = "+(n==0?0.0:(sumRisk/n)));
    }

    private static java.util.List<MHRecord> sampleEvenly(java.util.List<MHRecord> list, int howMany, boolean forRemoval){
        java.util.List<MHRecord> out=new java.util.ArrayList<>();
        int n=list.size(); if(howMany<=0) return out;
        int step=Math.max(1,n/howMany);
        for(int i=0,added=0;i<n && added<howMany;i+=step){
            MHRecord r=list.get(i);
            if(forRemoval){
                if(removedIds.contains(r.rowId)) continue;
                removedIds.add(r.rowId);
            }
            out.add(r); added++;
        }
        return out;
    }
}
