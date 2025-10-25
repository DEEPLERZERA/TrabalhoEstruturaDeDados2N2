import java.util.LinkedList;
import java.util.Queue;

public class ABB <E extends Comparable<E>> {
    private Node raiz;

    public ABB(){
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
    //método inserir
    public E inserir(E valor){
        Node novo = new Node(valor);
        inserir(novo, raiz);
        return valor;
    }
    public Node inserir(Node novo, Node anterior){
        if(raiz == null){ //ou if(isEmpty()){}
            raiz = novo;
            return raiz;
        }
        if (anterior!=null){
            //processo de comparação para verificar qual 
            //lado será armazenado o Node
            //qdo o nó a ser inserido for menor que o anterior
            if(novo.getValue().compareTo(anterior.getValue())<0){
                anterior.setFilhoEsq(
                    inserir(novo,anterior.getFilhoEsq()));
            }
            else{
                anterior.setFilhoDir(
                    inserir(novo, anterior.getFilhoDir()));
            }
        }
        else{
            anterior = novo;
        }
        return anterior;
    }
    //percorrer
    //em-ordem
    public void emOrdem(){
        emOrdem(raiz);
    }
    public void emOrdem(Node no){
        if(no != null){
            emOrdem(no.getFilhoEsq());
            System.out.print(no.getValue());
            emOrdem(no.getFilhoDir());
        }
    }
    //pré-ordem
    public void preOrdem(){
        preOrdem(raiz);
    }
    public void preOrdem(Node no){
        if(no != null){
            System.out.print(no.getValue());
            preOrdem(no.getFilhoEsq());
            preOrdem(no.getFilhoDir());
        }
    }
    public void posOrdem(){
        posOrdem(raiz);
    }
    public void posOrdem(Node no){
        if (no != null){
            posOrdem(no.getFilhoEsq());
            posOrdem(no.getFilhoDir());
            System.out.print(no.getValue());
        }
    }
    //em nível
    public void emNivel(){
        if(raiz == null) return;
        Queue<Node> fila = new LinkedList<>();
        fila.add(raiz);
        while(!fila.isEmpty()){
            Node atual = fila.poll();
            System.out.print(atual.getValue());
            if(atual.getFilhoEsq() != null)
                fila.add(atual.getFilhoEsq());
            if(atual.getFilhoDir() != null)
                fila.add(atual.getFilhoDir());    
        }
    }
    //Determina o maior elemento a partir de um nó 'raiz' 
    //(e enlaça seu pai para eliminar esse nodo 'raiz' desta posição).
    //Retorna o nodo com maior valor desta subárvore.
    public Node getMax(Node raiz, Node paiRaiz) {
        if (isEmpty()) {
            return null;
        }
        Node aux;
        //Se não tiver mais filho direito
        if (raiz.getFilhoDir() == null) { //encontrou o maior
            aux = raiz;
            //Se tiver um pai, ele assume o filho esquerdo (nunca terá filho direito)
            if (paiRaiz != null) {
                if (paiRaiz.getFilhoEsq() == raiz) { //se 'raiz' era filho esquerdo do pai
                    paiRaiz.setFilhoEsq(raiz.getFilhoEsq());
                } else {  //se 'raiz' era filho direito do pai
                    paiRaiz.setFilhoDir(raiz.getFilhoEsq());
                }
            }
            return aux;
        } else {
            return getMax(raiz.getFilhoDir(), raiz);
        }
    }

    private int compara(Object ob1, Object ob2) {
        return ((Comparable) ob1).compareTo(((Comparable) ob2));
    }
    public boolean eliminar(Object e) {
        return eliminar(raiz, null, e);
    }
    //Rotina para eliminar
    private boolean eliminar(Node node, Node paiRaiz, Object e) {  // remove um elemento da árvore, retorna true ou false
        Node aux;
        if (node == null) {  // não achou o elemento, não existe (chegou em uma folha, ou árvore vazia)
                return false;  // abandonamos o método retornando false, não foi possível eliminar
        } else { // a árvore ou sub-árvore não está vazia:
            if (compara(e, node.getValue()) == 0) {  // se o nó a eliminar, node, foi encontrado (ele guarda o objeto e)
                aux = node;
                if (node.getFilhoEsq() == null && node.getFilhoDir() == null) {  // caso 1: se node não possui filhos, basta eliminar o nó
                            if (paiRaiz == null) {  // se o node a eliminar não tiver pai, ele era a raiz da árvore, então a árvore ficou vazia
                                setRaiz(null);  // convenção para ABB vazia
                            } 
                            else {  // senão, o pai deve "deserdar" o filho (ficar sem esse filho eliminado)
                                // verifica se o nó que será eliminado é o filho esquerdo ou direito  do pai:
                                if (paiRaiz.getFilhoEsq() != null && compara(paiRaiz.getFilhoEsq().getValue(), e) == 0) {
                                    paiRaiz.setFilhoEsq(null);
                                } else if (paiRaiz.getFilhoDir() != null && compara(paiRaiz.getFilhoDir().getValue(), e) == 0) {
                                    paiRaiz.setFilhoDir(null);
                                }
                            }
                } else if (node.getFilhoDir() == null) {   // caso 2a: se node só tiver o filho esquerdo
                            if (paiRaiz != null) {  // se node tiver um pai, o pai (paiRaiz) assume o filho esquerdo de node
                                // verifica se a raiz é filho esquerdo ou direito de paiRaiz, para assumir o neto:
                                if (paiRaiz.getFilhoEsq() != null && compara(paiRaiz.getFilhoEsq().getValue(), e) == 0) {
                                    paiRaiz.setFilhoEsq(node.getFilhoEsq());
                                } else {
                                    paiRaiz.setFilhoDir(node.getFilhoEsq());
                                }
                            } 
                            else { // se node não tiver pai (caso da raiz da árvore, paiRaiz é nulo), adotar seu filho ou mover a raiz:
                                node.setValue(node.getFilhoEsq().getValue());
                                raiz = raiz.getFilhoEsq();  // mover a raiz da árvore
                            }
                } else if (node.getFilhoEsq() == null) {   // caso 2b: se node só tiver o filho direito                    
                        if (paiRaiz != null) {  //se node tiver um pai, o pai (paiRaiz) assume o filho direito de node:
                            // verifica se a raiz paiRaiz tem node como filho esquerdo ou direito, para assumir o neto:
                            if (paiRaiz.getFilhoEsq() != null && compara(paiRaiz.getFilhoEsq().getValue(), e) == 0) {
                                paiRaiz.setFilhoEsq(node.getFilhoDir());
                            } else {
                                paiRaiz.setFilhoDir(node.getFilhoDir());
                            }
                        } 
                        else {  // se node não tiver pai (caso da raiz da árvore, paiRaiz é nulo), adotar seu filho ou mover a raiz:
                            node.setValue(node.getFilhoDir().getValue());
                            raiz = raiz.getFilhoDir();  // mover a raiz da árvore
                        }
                } else {   //caso 3: o nodo node possui os dois filhos:
                            aux = getMax(node.getFilhoEsq(), node); //determina o maior da subárvore esquerda
                            node.setValue(aux.getValue());
                }
                aux = null;
                return true;  // fim dos casos em que o nó a eliminar foi encontrado, retornamos true
            } else { // se node não é o nó a eliminar, continuamos procurando recursivamente:
                    if (compara(e, node.getValue()) < 0) { // se o objeto e for menor que o objeto em node, continuar procurando à esquerda:
                        return eliminar(node.getFilhoEsq(), node, e);  // chamada recursiva
                    } else { // senão, procurar à direita:
                        return eliminar(node.getFilhoDir(), node, e);  // chamada recursiva
                    }
            }
        }


    }
    
}
