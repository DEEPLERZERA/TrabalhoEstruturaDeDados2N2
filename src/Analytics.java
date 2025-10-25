
import java.util.*;
import java.nio.file.*;
import java.io.*;

public class Analytics {

    private static void writeCsv(Path path, List<String[]> rows) throws Exception {
        try (BufferedWriter w = Files.newBufferedWriter(path)) {
            for (String[] r : rows) {
                for (int i=0; i<r.length; i++) {
                    if (i>0) w.write(",");
                    // naive CSV since our values have no commas
                    w.write(r[i] == null ? "" : r[i]);
                }
                w.write("\n");
            }
        }
    }

    public static void treatmentByCountryGender(List<MHRecord> recs) throws Exception {
        Map<String, Map<String, int[]>> map = new TreeMap<>();
        for (MHRecord r : recs) {
            map.computeIfAbsent(r.country, k-> new TreeMap<>());
            Map<String,int[]> g = map.get(r.country);
            g.computeIfAbsent(r.gender, k-> new int[2]);
            int[] arr = g.get(r.gender);
            arr[0]++; // total
            if ("Yes".equalsIgnoreCase(r.treatment)) arr[1]++; // treated
        }
        List<String[]> out = new ArrayList<>();
        out.add(new String[]{"Country","Gender","N","Treated","Rate"});
        for (var e : map.entrySet()) {
            for (var ge : e.getValue().entrySet()) {
                int N = ge.getValue()[0], T = ge.getValue()[1];
                String rate = String.format(java.util.Locale.US, "%.4f", N==0?0.0: (T*1.0/N));
                out.add(new String[]{e.getKey(), ge.getKey(), String.valueOf(N), String.valueOf(T), rate});
            }
        }
        writeCsv(Paths.get("out_treatment_country_gender.csv"), out);
    }

    public static void daysIndoorsByCountry(List<MHRecord> recs) throws Exception {
        String[] buckets = {"Go out Every day","1-14 days","15-30 days","31-60 days","More than 2 months"};
        Map<String, int[]> map = new TreeMap<>();
        for (MHRecord r : recs) {
            int[] arr = map.computeIfAbsent(r.country, k-> new int[buckets.length]);
            int idx = java.util.Arrays.asList(buckets).indexOf(r.daysIndoors);
            if (idx>=0) arr[idx]++;
        }
        List<String[]> out = new ArrayList<>();
        out.add(new String[]{"Country","GoOutEveryDay","1_14","15_30","31_60","MoreThan2Months"});
        for (var e : map.entrySet()) {
            int[] a = e.getValue();
            out.add(new String[]{e.getKey(), String.valueOf(a[0]), String.valueOf(a[1]), String.valueOf(a[2]), String.valueOf(a[3]), String.valueOf(a[4])});
        }
        writeCsv(Paths.get("out_days_country.csv"), out);
    }

    public static void riskByCountry(List<MHRecord> recs) throws Exception {
        Map<String, int[]> map = new TreeMap<>();
        for (MHRecord r : recs) {
            int[] agg = map.computeIfAbsent(r.country, k-> new int[2]);
            agg[0] += r.riskScore(); agg[1]++;
        }
        List<String[]> out = new ArrayList<>();
        out.add(new String[]{"Country","N","AvgRisk"});
        for (var e : map.entrySet()) {
            int N = e.getValue()[1];
            double avg = N==0?0.0:(e.getValue()[0]*1.0/N);
            out.add(new String[]{e.getKey(), String.valueOf(N), String.format(java.util.Locale.US, "%.4f", avg)});
        }
        writeCsv(Paths.get("out_risk_country.csv"), out);
    }

    public static void familyHistoryEffectOnTreatment(List<MHRecord> recs) throws Exception {
        // 2x2: FamilyHistory (Yes/No) x Treatment (Yes/No)
        int[][] m = new int[2][2];
        for (MHRecord r : recs) {
            int fh = "Yes".equalsIgnoreCase(r.familyHistory) ? 1 : 0;
            int tr = "Yes".equalsIgnoreCase(r.treatment) ? 1 : 0;
            m[fh][tr]++;
        }
        List<String[]> out = new ArrayList<>();
        out.add(new String[]{"FamilyHistory","TreatmentNo","TreatmentYes","RateYes"});
        String[] rows = {"No","Yes"};
        for (int i=0;i<2;i++){
            int N = m[i][0] + m[i][1];
            double rate = N==0?0.0: (m[i][1]*1.0/N);
            out.add(new String[]{rows[i], String.valueOf(m[i][0]), String.valueOf(m[i][1]), String.format(java.util.Locale.US, "%.4f", rate)});
        }
        writeCsv(Paths.get("out_family_treatment.csv"), out);
    }

    public static void stressMoodMatrix(List<MHRecord> recs) throws Exception {
        String[] stress = {"No","Maybe","Yes"};
        String[] mood = {"Low","Medium","High"};
        int[][] mtx = new int[stress.length][mood.length];
        int total=0;
        for (MHRecord r : recs) {
            int si = java.util.Arrays.asList(stress).indexOf(r.increasingStress);
            int mi = java.util.Arrays.asList(mood).indexOf(r.moodSwings);
            if (si>=0 && mi>=0) { mtx[si][mi]++; total++; }
        }
        List<String[]> out = new ArrayList<>();
        out.add(new String[]{"Stress\\Mood","Low","Medium","High"});
        for (int i=0;i<stress.length;i++){
            double row = mtx[i][0]+mtx[i][1]+mtx[i][2];
            out.add(new String[]{stress[i],
                String.format(java.util.Locale.US,"%.4f", row==0?0.0: (mtx[i][0]/row)),
                String.format(java.util.Locale.US,"%.4f", row==0?0.0: (mtx[i][1]/row)),
                String.format(java.util.Locale.US,"%.4f", row==0?0.0: (mtx[i][2]/row))
            });
        }
        writeCsv(Paths.get("out_stress_mood.csv"), out);
    }
}
