import java.util.LinkedList;
import java.util.List;

/**
 * La classe Combinations serve per trovare tutte le combinazioni di caratteri
 * possibili dato un array di caratteri e una profondità.
 * @author Erik Pelloni
 * 
 * @version v1.0 (18.11.2021)
 */
public class Combinations {

    public static void main(String[] args) {
        allPermutations(new char[]{'a', 'b', 'c', 'd'}, 10)
            .forEach(System.out::println);
    }

    public static List<String> allPermutations(char[] chars, int maxLength) {
        Node root = new Node();
        
        for (int i = 1; i <= maxLength; i++) {
            allPermutations(chars, i, root);
        }

        return root.permutateRecursively();
    }

    private static void allPermutations(char[] chars, int depth, Node node) {
        if (depth == 1) {
            for (char c : chars) {
                node.addNode(new Node(c));
            }
            return;
        }

        for (char c : chars) {
            Node subnode = new Node(c);
            allPermutations(chars, depth - 1, subnode);
            node.addNode(subnode);
        }
    }

}

/**
 * La classe Node rappresenta un nodo dell' "albero" di caratteri. 
 * @author Erik Pelloni
 * @version v1.0 (18.11.2021)
 */
class Node {

    private List<Node> subnodes = new LinkedList<>();
    private char value;
    private boolean root;

    public Node() {
        this.root = true;
    }

    public Node(char value) {
        this.value = value;
    }

    public void addNode(Node node) {
        subnodes.add(node);
    }

    public List<String> permutateRecursively() {
        return permutateRecursively(new StringBuilder());
    }

    private List<String> permutateRecursively(StringBuilder superstring) {
        List<String> result = new LinkedList<>();
        
        if (!root) {
            superstring.append(value);
        }
        
        if (subnodes.isEmpty()) {
            result.add(superstring.toString());
            return result;
        }
        
        for (Node node : subnodes) {
            List<String> permutations = node.permutateRecursively(new StringBuilder(superstring.toString()));

            permutations.forEach(result::add);
        }

        return result;
    }

}