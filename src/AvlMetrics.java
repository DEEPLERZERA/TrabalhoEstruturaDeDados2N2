/** Contadores globais de rotações na AVL. */
public final class AvlMetrics {
    public static long rotationsSingle = 0L;  // rotações simples (ou parte das duplas)
    public static long rotationsEvents = 0L;  // eventos de rotação (cada rotação conta 1)
    public static void reset(){ rotationsSingle = 0L; rotationsEvents = 0L; }
}
