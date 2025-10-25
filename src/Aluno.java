public class Aluno implements Comparable<Aluno> {
    /*permite que objetos da Classe possam ser 
    comparados entre si*/
    //atributos
    private String nome;
    private String ra;
    private double media;

    Aluno(String n, String r, double m){
        nome = n;
        ra = r;
        media = m;
    }
    public String getNome() {
        return nome;
    }
    public String getRa() {
        return ra;
    }
    public double getMedia() {
        return media;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setRa(String ra) {
        this.ra = ra;
    }
    public void setMedia(double media) {
        this.media = media;
    }
    @Override
    public String toString() {
        return "Nome: " + nome + "- RA: "+ 
        ra + "- Média: " + media + "\n";
    }
    @Override
    public int compareTo(Aluno o) {
        if(this.ra.compareTo(o.getRa()) < 0)
            return -1;
        else
            if(this.ra.compareTo(o.getRa()) == 0)        
                return 0;
            else
                return 1;
    }   
}
