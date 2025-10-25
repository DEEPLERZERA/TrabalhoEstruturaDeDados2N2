public final class InputUtil {
    private static final java.util.Scanner SC = new java.util.Scanner(System.in);

    public static String ask(String label, String def){
        System.out.print(label + (def==null?": ":" ["+def+"]: "));
        String s=SC.nextLine().trim();
        return s.isEmpty()?def:s;
    }

    public static int askInt(String label, int def){
        while(true){
            try{
                return Integer.parseInt(ask(label,String.valueOf(def)));
            }catch(Exception e){
                System.out.println("Valor inválido.");
            }
        }
    }

    public static String oneOf(String label, String def, String... opts){
        while(true){
            String s=ask(label+" "+java.util.Arrays.toString(opts),def);
            for(String o:opts) if(o.equalsIgnoreCase(s)) return o;
            System.out.println("Opção inválida.");
        }
    }
}
