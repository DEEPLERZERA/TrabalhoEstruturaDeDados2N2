import java.util.LinkedList;
import java.util.Queue;

public class Tree <E extends Comparable<E>> { //árvore
    private Node raiz;

    Tree(){
        raiz = null; //árvore vazia
    }
    public Node getRaiz() {
        return raiz;
    }
    public void setRaiz(Node raiz) {
        this.raiz = raiz;
    }
    public boolean isEmpty(){
        return raiz == null;
    }
    public E inserir(E valor){
        Node novo = new Node(valor);
        if(raiz == null){ //pode usar o isEmpty
            raiz = novo;
            return valor;
        }
        Queue<Node> fila = new LinkedList<>();
        fila.add(raiz);
        while(!fila.isEmpty()){
            Node atual = fila.poll();
            if(atual.getFilhoEsq() == null){
                atual.setFilhoEsq(novo);
                return valor;
            } else if (atual.getFilhoDir() == null){
                atual.setFilhoDir(novo);
                return valor;
            }else{
                fila.add(atual.getFilhoEsq());
                fila.add(atual.getFilhoDir());
            }
        }
        return valor;
    }
    
}
