import java.nio.file.*;
import java.io.*;
import java.util.*;

public class App {

    private static MHLoader.Loaded L = null;
    private static String CSV_PATH = Paths.get("data","mental_health_clean.csv").toString();
    private static final Scanner SC = new Scanner(System.in);
    private static final Set<Integer> removedIds = new HashSet<>(); // track rowIds already removidos

    public static void main(String[] args) throws Exception {
        while (true) {
            printMenu();
            String op = SC.nextLine().trim();
            if (op.equals("1")) {
                menuLoad();
            } else if (op.equals("2")) {
                requireLoaded();
                menuSearch();
            } else if (op.equals("3")) {
                requireLoaded();
                menuRemove();
            } else if (op.equals("4")) {
                requireLoaded();
                menuAnalytics();
            } else if (op.equals("5")) {
                requireLoaded();
                showStatus();
            } else if (op.equals("6")) {
                System.out.println("Saindo...");
                break;
            } else {
                System.out.println("Opção inválida.");
            }
        }
    }

    private static void printMenu(){
        System.out.println("\n=== MENU ABB × AVL – Mental Health ===");
        System.out.println("1) Carregar/Recarregar CSV (reseta estado e contadores)");
        System.out.println("2) Rodar benchmark de BUSCA (ABB × AVL)");
        System.out.println("3) Rodar experimento de REMOÇÃO (ABB × AVL) + rotações (AVL)");
        System.out.println("4) Gerar análises exploratórias (5+ CSVs)");
        System.out.println("5) Mostrar status/contadores atuais");
        System.out.println("6) Sair");
        System.out.print("Escolha: ");
    }

    private static void menuLoad() throws Exception {
        System.out.print("Caminho do CSV [" + CSV_PATH + "]: ");
        String in = SC.nextLine().trim();
        if (!in.isEmpty()) CSV_PATH = in;
        if (!Files.exists(Paths.get(CSV_PATH))) {
            System.out.println("Arquivo não encontrado: " + CSV_PATH);
            return;
        }
        // Reset rotações
        AvlMetrics.reset();
        // Carregar
        L = MHLoader.load(CSV_PATH);
        removedIds.clear();
        System.out.println("Registros carregados: " + L.records.size());

        // Criar rotations.csv com fase de insert
        Path rfile = Paths.get("rotations.csv");
        try (BufferedWriter w = Files.newBufferedWriter(rfile)) {
            w.write("phase,rotations_single,rotations_events\n");
            w.write("load_inserts," + AvlMetrics.rotationsSingle + "," + AvlMetrics.rotationsEvents + "\n");
        }
        System.out.println("Rotações (AVL) salvas em: " + rfile.toAbsolutePath());

        // Criar metrics.csv (cabeçalho vazio por enquanto)
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get("metrics.csv"))) {
            w.write("op,compsABB,compsAVL,nsABB,nsAVL\n");
        }
    }

    private static void menuSearch() throws Exception {
        System.out.print("Qtde de consultas (default ~5000): ");
        String in = SC.nextLine().trim();
        int n = L.records.size();
        int q = in.isEmpty() ? Math.max(1, n/5000) : Integer.parseInt(in);
        List<MHRecord> queries = sampleEvenly(L.records, q, false);
        Experiments.Result r = Experiments.runSearch(L, queries);
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get("metrics.csv"), java.nio.file.StandardOpenOption.APPEND)) {
            w.write(r.toString()); w.write("\n");
        }
        System.out.println("Busca concluída → metrics.csv (linha 'search')");
        System.out.println("search,"+r.compsABB+","+r.compsAVL+","+r.nsABB+","+r.nsAVL);
    }

    private static void menuRemove() throws Exception {
        System.out.print("Percentual a remover (ex 10 para 10%): ");
        String in = SC.nextLine().trim();
        int n = L.records.size();
        int pct = in.isEmpty() ? 10 : Integer.parseInt(in);
        int cut = Math.max(1, (int) Math.round(n * (pct / 100.0)));
        List<MHRecord> toRemove = sampleEvenly(L.records, cut, true);

        long preSingle = AvlMetrics.rotationsSingle;
        long preEvents = AvlMetrics.rotationsEvents;

        Experiments.Result r = Experiments.runRemove(L, toRemove);
        long deltaSingle = AvlMetrics.rotationsSingle - preSingle;
        long deltaEvents = AvlMetrics.rotationsEvents - preEvents;

        try (BufferedWriter w = Files.newBufferedWriter(Paths.get("rotations.csv"), java.nio.file.StandardOpenOption.APPEND)) {
            w.write("removals," + deltaSingle + "," + deltaEvents + "\n");
        }
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get("metrics.csv"), java.nio.file.StandardOpenOption.APPEND)) {
            w.write(r.toString()); w.write("\n");
        }
        System.out.println("Remoção concluída → metrics.csv ('remove') e rotations.csv ('removals')");
        System.out.println("Rotations delta: single="+deltaSingle+" events="+deltaEvents);
    }

    private static void menuAnalytics() throws Exception {
        Analytics.treatmentByCountryGender(L.records);
        Analytics.daysIndoorsByCountry(L.records);
        Analytics.riskByCountry(L.records);
        Analytics.familyHistoryEffectOnTreatment(L.records);
        Analytics.stressMoodMatrix(L.records);
        System.out.println("Arquivos gerados: out_*.csv");
    }

    private static void showStatus(){
        int n = L==null?0:L.records.size();
        System.out.println("Carregado? " + (L!=null));
        System.out.println("Registros (originais): " + n);
        System.out.println("Removidos até agora: " + removedIds.size());
        System.out.println("AVL rotations → single=" + AvlMetrics.rotationsSingle + " events=" + AvlMetrics.rotationsEvents);
    }

    private static void requireLoaded(){
        if (L == null) {
            System.out.println("Primeiro carregue o CSV (opção 1).");
            throw new IllegalStateException("CSV não carregado");
        }
    }

    // Amostra distribuída ao longo dos dados; se skipRemoved=true ignora já removidos
    private static List<MHRecord> sampleEvenly(List<MHRecord> list, int howMany, boolean forRemoval){
        List<MHRecord> out = new ArrayList<>();
        int n = list.size();
        if (howMany <= 0) return out;
        int step = Math.max(1, n / howMany);
        for (int i = 0, added=0; i < n && added < howMany; i += step) {
            MHRecord r = list.get(i);
            if (forRemoval) {
                if (removedIds.contains(r.rowId)) continue;
                removedIds.add(r.rowId);
            }
            out.add(r);
            added++;
        }
        return out;
    }
}
