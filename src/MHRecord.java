import java.util.Arrays;

public class MHRecord implements Comparable<MHRecord> {
    public static long COMPARE_COUNT = 0L;

    public final String gender;
    public final String country;
    public final String occupation;
    public final String selfEmployed; // "Yes"/"No"/"Unknown"
    public final String familyHistory; // "Yes"/"No"
    public final String treatment; // "Yes"/"No"
    public final String daysIndoors; // canonical 5 bins
    public final String habitsChange; // "Yes"/"No"/"Maybe"
    public final String mentalHealthHistory; // "Yes"/"No"/"Maybe"
    public final String increasingStress; // "Yes"/"No"/"Maybe"
    public final String moodSwings; // "Low"/"Medium"/"High"
    public final String socialWeakness; // "Yes"/"No"/"Maybe"
    public final String copingStruggles; // "Yes"/"No"
    public final String workInterest; // "Yes"/"No"/"Maybe"
    public final String mentalHealthInterview; // "Yes"/"No"/"Maybe"
    public final String careOptions; // "Yes"/"No"/"Maybe"

    public final int rowId; // incremental loader row id

    public MHRecord(String[] a, int rowId) {
        this.gender = a[0];
        this.country = a[1];
        this.occupation = a[2];
        this.selfEmployed = a[3];
        this.familyHistory = a[4];
        this.treatment = a[5];
        this.daysIndoors = a[6];
        this.habitsChange = a[7];
        this.mentalHealthHistory = a[8];
        this.increasingStress = a[9];
        this.moodSwings = a[10];
        this.socialWeakness = a[11];
        this.copingStruggles = a[12];
        this.workInterest = a[13];
        this.mentalHealthInterview = a[14];
        this.careOptions = a[15];
        this.rowId = rowId;
    }


    public int riskScore(){
        int score = 0;
        // increasingStress
        score += "Yes".equalsIgnoreCase(increasingStress) ? 2 : ("Maybe".equalsIgnoreCase(increasingStress) ? 1 : 0);
        // mood
        score += "High".equalsIgnoreCase(moodSwings) ? 2 : ("Medium".equalsIgnoreCase(moodSwings) ? 1 : 0);
        // mental history
        score += "Yes".equalsIgnoreCase(mentalHealthHistory) ? 2 : ("Maybe".equalsIgnoreCase(mentalHealthHistory) ? 1 : 0);
        // socialWeakness
        score += "Yes".equalsIgnoreCase(socialWeakness) ? 2 : ("Maybe".equalsIgnoreCase(socialWeakness) ? 1 : 0);
        // coping struggles
        score += "Yes".equalsIgnoreCase(copingStruggles) ? 2 : 0;
        // daysIndoors
        int d = 0;
        if ("1-14 days".equals(daysIndoors)) d = 1;
        else if ("15-30 days".equals(daysIndoors)) d = 2;
        else if ("31-60 days".equals(daysIndoors)) d = 3;
        else if ("More than 2 months".equals(daysIndoors)) d = 4;
        score += d;
        return score;
    }

    @Override
    public int compareTo(MHRecord o) {
        // Count every comparison the trees perform via Comparable
        COMPARE_COUNT++;
        int c = this.country.compareTo(o.country);
        if (c != 0) return c;
        return Integer.compare(this.rowId, o.rowId);
    }

    public static void resetCounter() { COMPARE_COUNT = 0L; }

    @Override
    public String toString() {
        return country + "#" + rowId;
    }
}
