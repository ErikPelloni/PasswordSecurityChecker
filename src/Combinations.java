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

    public static Node createTree(char[] chars, int maxLength) {
        Node root = new Node();

        for (int i = 1; i <= maxLength; i++) {
            allPermutations(chars, i, root);
        }

        return root;
    }

    public static List<String> getBranches(Node node) {
        return node.permutateRecursively();
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

    public static void bruteForce(char[] characters, PasswordSecurityChecker psc) {
        int base = characters.length + 1;
        StringBuilder guess = new StringBuilder();
        int tests = 1;
        int c = 0;
        int m = 0;
    
        while (true) {
            int y = tests;
            while (true) {
                c = y % base;
                m = (int) Math.floor((y - c) / (double) base);
                y = m;
                int index = (c - 1);
                if (index < 0) {
                    index += characters.length - 1;
                }
                guess.insert(0, characters[index]);
                // guess = characters[index] + guess;
                if (m == 0) {
                    break;
                }
            }
    
            System.out.println(guess);
            psc.tryPassword(guess.toString(), true);
            // else
            ++tests;
            guess.setLength(0);
        }
    }

    private List<String> permutateRecursively(StringBuilder superstring) {
        List<String> result = new LinkedList<>();
        
        if (!root) {
            superstring.append(value);
        }
        
        if (subnodes.isEmpty()) {
            result.add(superstring.toString());
            //
            return result;
        }
        
        for (Node node : subnodes) {
            List<String> permutations = node.permutateRecursively(new StringBuilder(superstring.toString()));

            permutations.forEach(result::add);
        }

        return result;
    }

    public void permutateRecursively(PasswordSecurityChecker psc) {
        permutateRecursively(new StringBuilder(), psc);
    }

    void permutateRecursively(StringBuilder superstring, PasswordSecurityChecker psc) {
        if (!root) {
            superstring.append(value);
        }
        
        if (subnodes.isEmpty()) {
            String pass = superstring.toString();
            System.out.println(pass);
            psc.tryPassword(pass, true);
            return;
        }
        
        for (Node node : subnodes) {
            node.permutateRecursively(new StringBuilder(superstring.toString()), psc);
        }
    }

}