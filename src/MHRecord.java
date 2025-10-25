import java.util.Arrays;

public class MHRecord implements Comparable<MHRecord> {
    public static long COMPARE_COUNT = 0L;

    public final String gender, country, occupation, selfEmployed, familyHistory, treatment,
            daysIndoors, habitsChange, mentalHealthHistory, increasingStress, moodSwings,
            socialWeakness, copingStruggles, workInterest, mentalHealthInterview, careOptions;
    public final int rowId;

    public MHRecord(String[] a, int rowId){
        this.gender=a[0]; this.country=a[1]; this.occupation=a[2]; this.selfEmployed=a[3];
        this.familyHistory=a[4]; this.treatment=a[5]; this.daysIndoors=a[6]; this.habitsChange=a[7];
        this.mentalHealthHistory=a[8]; this.increasingStress=a[9]; this.moodSwings=a[10];
        this.socialWeakness=a[11]; this.copingStruggles=a[12]; this.workInterest=a[13];
        this.mentalHealthInterview=a[14]; this.careOptions=a[15]; this.rowId=rowId;
    }

    public int riskScore(){
        int s=0;
        s += "Yes".equalsIgnoreCase(increasingStress)?2:("Maybe".equalsIgnoreCase(increasingStress)?1:0);
        s += "High".equalsIgnoreCase(moodSwings)?2:("Medium".equalsIgnoreCase(moodSwings)?1:0);
        s += "Yes".equalsIgnoreCase(mentalHealthHistory)?2:("Maybe".equalsIgnoreCase(mentalHealthHistory)?1:0);
        s += "Yes".equalsIgnoreCase(socialWeakness)?2:("Maybe".equalsIgnoreCase(socialWeakness)?1:0);
        s += "Yes".equalsIgnoreCase(copingStruggles)?2:0;
        int d=0;
        if("1-14 days".equals(daysIndoors)) d=1;
        else if("15-30 days".equals(daysIndoors)) d=2;
        else if("31-60 days".equals(daysIndoors)) d=3;
        else if("More than 2 months".equals(daysIndoors)) d=4;
        s+=d;
        return s;
    }

    @Override
    public int compareTo(MHRecord o){
        COMPARE_COUNT++;
        int c=this.country.compareTo(o.country);
        if(c!=0) return c;
        return Integer.compare(this.rowId,o.rowId);
    }

    public static void resetCounter(){ COMPARE_COUNT=0L; }

    public static MHRecord makeForInsert(int rowId, String gender, String country, String occupation,
        String selfEmp, String familyHist, String treatment, String daysInd, String habitsChange,
        String mentalHist, String stress, String mood, String socialWeak, String coping, String workInt,
        String interview, String care){
        String[] a=new String[]{gender,country,occupation,selfEmp,familyHist,treatment,daysInd,habitsChange,mentalHist,stress,mood,socialWeak,coping,workInt,interview,care};
        return new MHRecord(a,rowId);
    }

    public static MHRecord makeBound(String country, int rowId){
        String[] a=new String[16];
        Arrays.fill(a,"");
        a[1]=country==null?"":country;
        return new MHRecord(a,rowId);
    }

    public String toString(){ return country+"#"+rowId; }
}
