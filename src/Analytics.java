import java.util.*;
import java.nio.file.*;
import java.io.*;

public class Analytics {
    private static void writeCsv(Path p, java.util.List<String[]> rows) throws Exception {
        try(BufferedWriter w=Files.newBufferedWriter(p)){
            for(String[] r:rows){
                for(int i=0;i<r.length;i++){
                    if(i>0) w.write(",");
                    w.write(r[i]==null?"":r[i]);
                }
                w.write("\n");
            }
        }
    }

    public static void treatmentByCountryGender(java.util.List<MHRecord> recs) throws Exception {
        Map<String,Map<String,int[]>> m=new TreeMap<>();
        for(MHRecord r:recs){
            m.computeIfAbsent(r.country,k->new TreeMap<>());
            Map<String,int[]> g=m.get(r.country);
            g.computeIfAbsent(r.gender,k->new int[2]);
            int[] a=g.get(r.gender);
            a[0]++;
            if("Yes".equalsIgnoreCase(r.treatment)) a[1]++;
        }
        java.util.List<String[]> out=new ArrayList<>();
        out.add(new String[]{"Country","Gender","N","Treated","Rate"});
        for(var e:m.entrySet()) for(var ge:e.getValue().entrySet()){
            int N=ge.getValue()[0], T=ge.getValue()[1];
            String rate=String.format(java.util.Locale.US,"%.4f", N==0?0.0:(T*1.0/N));
            out.add(new String[]{e.getKey(),ge.getKey(),String.valueOf(N),String.valueOf(T),rate});
        }
        writeCsv(Paths.get("out_treatment_country_gender.csv"), out);
    }

    public static void daysIndoorsByCountry(java.util.List<MHRecord> recs) throws Exception {
        String[] b={"Go out Every day","1-14 days","15-30 days","31-60 days","More than 2 months"};
        Map<String,int[]> m=new TreeMap<>();
        for(MHRecord r:recs){
            int[] a=m.computeIfAbsent(r.country,k->new int[b.length]);
            int idx=java.util.Arrays.asList(b).indexOf(r.daysIndoors);
            if(idx>=0) a[idx]++;
        }
        java.util.List<String[]> out=new ArrayList<>();
        out.add(new String[]{"Country","GoOutEveryDay","1_14","15_30","31_60","MoreThan2Months"});
        for(var e:m.entrySet()){
            int[] a=e.getValue();
            out.add(new String[]{e.getKey(),String.valueOf(a[0]),String.valueOf(a[1]),String.valueOf(a[2]),String.valueOf(a[3]),String.valueOf(a[4])});
        }
        writeCsv(Paths.get("out_days_country.csv"), out);
    }

    public static void riskByCountry(java.util.List<MHRecord> recs) throws Exception {
        Map<String,int[]> m=new TreeMap<>();
        for(MHRecord r:recs){
            int[] a=m.computeIfAbsent(r.country,k->new int[2]);
            a[0]+=r.riskScore(); a[1]++;
        }
        java.util.List<String[]> out=new ArrayList<>();
        out.add(new String[]{"Country","N","AvgRisk"});
        for(var e:m.entrySet()){
            int N=e.getValue()[1];
            double avg=N==0?0.0:(e.getValue()[0]*1.0/N);
            out.add(new String[]{e.getKey(),String.valueOf(N),String.format(java.util.Locale.US,"%.4f",avg)});
        }
        writeCsv(Paths.get("out_risk_country.csv"), out);
    }

    public static void familyHistoryEffectOnTreatment(java.util.List<MHRecord> recs) throws Exception {
        int[][] m=new int[2][2];
        for(MHRecord r:recs){
            int fh="Yes".equalsIgnoreCase(r.familyHistory)?1:0;
            int tr="Yes".equalsIgnoreCase(r.treatment)?1:0;
            m[fh][tr]++;
        }
        java.util.List<String[]> out=new ArrayList<>();
        out.add(new String[]{"FamilyHistory","TreatmentNo","TreatmentYes","RateYes"});
        String[] rows={"No","Yes"};
        for(int i=0;i<2;i++){
            int N=m[i][0]+m[i][1];
            double rate=N==0?0.0:(m[i][1]*1.0/N);
            out.add(new String[]{rows[i],String.valueOf(m[i][0]),String.valueOf(m[i][1]),String.format(java.util.Locale.US,"%.4f",rate)});
        }
        writeCsv(Paths.get("out_family_treatment.csv"), out);
    }

    public static void stressMoodMatrix(java.util.List<MHRecord> recs) throws Exception {
        String[] stress={"No","Maybe","Yes"};
        String[] mood={"Low","Medium","High"};
        int[][] mtx=new int[stress.length][mood.length];
        for(MHRecord r:recs){
            int si=java.util.Arrays.asList(stress).indexOf(r.increasingStress);
            int mi=java.util.Arrays.asList(mood).indexOf(r.moodSwings);
            if(si>=0 && mi>=0){ mtx[si][mi]++; }
        }
        java.util.List<String[]> out=new ArrayList<>();
        out.add(new String[]{"Stress\\Mood","Low","Medium","High"});
        for(int i=0;i<stress.length;i++){
            double row=mtx[i][0]+mtx[i][1]+mtx[i][2];
            out.add(new String[]{stress[i],
                String.format(java.util.Locale.US,"%.4f", row==0?0.0:(mtx[i][0]/row)),
                String.format(java.util.Locale.US,"%.4f", row==0?0.0:(mtx[i][1]/row)),
                String.format(java.util.Locale.US,"%.4f", row==0?0.0:(mtx[i][2]/row))});
        }
        writeCsv(Paths.get("out_stress_mood.csv"), out);
    }
}
