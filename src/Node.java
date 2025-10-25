public class Node<E extends Comparable<E>> {
    /*Classe Node é genérica, aceita um tipo E, desde que
     * esse tipo implemente a interface Comparable
     */
    //atributos
    private E value;
    private Node filhoEsq;
    private Node filhoDir;
    //construtor
    Node (E value){
        this.value = value;
        this.filhoDir = null;
        this.filhoEsq = null;
    }
    public E getValue() {
        return value;
    }
    public void setValue(E value) {
        this.value = value;
    }
    public Node getFilhoDir() {
        return filhoDir;
    }
    public Node getFilhoEsq() {
        return filhoEsq;
    }
    public void setFilhoDir(Node filhoDir) {
        this.filhoDir = filhoDir;
    }
    public void setFilhoEsq(Node filhoEsq) {
        this.filhoEsq = filhoEsq;
    }
    
}
