/** Leitura de inputs com defaults/validação no console. */
public final class InputUtil {
    private static final java.util.Scanner SC = new java.util.Scanner(System.in);

    /** Pergunta string com default (Enter aceita o default). */
    public static String ask(String label, String def){
        System.out.print(label + (def==null?": ": " ["+def+"]: "));
        String s=SC.nextLine().trim();
        return s.isEmpty()?def:s;
    }

    /** Pergunta inteiro com validação e default. */
    public static int askInt(String label, int def){
        while(true){
            try{
                return Integer.parseInt(ask(label,String.valueOf(def)));
            }catch(Exception e){
                System.out.println("Valor inválido.");
            }
        }
    }

    /** Força escolher uma opção entre as oferecidas (case-insensitive). */
    public static String oneOf(String label, String def, String... opts){
        while(true){
            String s=ask(label+" "+java.util.Arrays.toString(opts),def);
            for(String o:opts) if(o.equalsIgnoreCase(s)) return o;
            System.out.println("Opção inválida.");
        }
    }
}
