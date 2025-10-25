
public final class AvlMetrics {
    public static long rotationsSingle = 0L; // conta rotações simples; dupla conta como 2
    public static long rotationsEvents = 0L; // conta eventos de rotação (LL, RR, LR, RL)
    public static void reset(){ rotationsSingle = 0L; rotationsEvents = 0L; }
}
