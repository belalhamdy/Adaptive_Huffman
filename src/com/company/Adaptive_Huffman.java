package com.company;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.PrintStream;
import java.util.*;
import java.util.List;

public class Adaptive_Huffman {
    final static char NYTCode = '\r';
    final static short StartId = 747; // -> lucky number
    final static short MAX = StartId * 2;
    final static int ASCIISIZE = 7;
    private static class Node {
        public String code; // Code of encoding value of that symbole
        public int freq, id;
        public Node parent, left, right;
        char symbol; // NYT is defined as "\r" (carriage return) -> can be changed later

        Node(char symbol, int id, String code) {
            this.id = id;
            this.code = code;
            this.symbol = symbol;
            freq = 0;
            left = right = parent = null;
        }

        @Override
        public String toString() {
            return (this.symbol == NYTCode ? "NYT" : this.symbol) + " : " + this.freq + " " + this.code + " " + this.id;
        }
    }

    static boolean[] isTaken = new boolean[MAX];
    static Node root;
    static Node NYT;

    public static String Encode(String text) throws Exception {
        init();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            char letter = text.charAt(i);
            Node curr = find(letter);
            if (curr == null) {
                ret.append(NYT.code).append(asciiToBinary(letter));
                curr = insert(NYT, letter);
                update(curr.parent);
            } else {
                ret.append(curr.code);
                update(curr);
            }
            //PrintTree(System.out); // -> For Debugging
        }
        return ret.toString();
    }

    public static String Decode(String text) throws Exception {
        init();
        String prefix;
        try {
            prefix = text.substring(0, ASCIISIZE);
        } catch (Exception ex) {
            throw new Exception("Invalid Input.. Please enter valid data");
        }
        char letter = binaryToAscii(prefix);
        StringBuilder ret = new StringBuilder();
        ret.append(letter);
        prefix = "";
        Node curr ;
        insert(NYT,letter);
        for (int i = ASCIISIZE; i < text.length(); ++i) {
            prefix += text.charAt(i);
            curr = find(prefix);
            int nodeStatus = NodeCase(curr);
            if (nodeStatus == 0) throw new Exception("Invalid Input.. Please enter valid data");
            else if (nodeStatus == 1) {
                if (curr.symbol != NYTCode) // try to exchange it with if (curr != NYT)
                {
                    ret.append(curr.symbol);
                    update(curr);
                } else {
                    try {
                        prefix = text.substring(i+1, i+1+ASCIISIZE);
                        i+=(ASCIISIZE);
                        letter = binaryToAscii(prefix);
                        curr = insert(NYT,letter);
                        ret.append(letter);
                        update(curr.parent);
                    } catch (Exception ex) {
                        throw new Exception("Invalid Input.. Please enter valid data");
                    }
                }
                prefix = "";
            }

        }
        if (!prefix.isEmpty()) {
            throw new Exception("Invalid Input.. Please enter valid data");
        }
        return ret.toString();
    }

    private static String asciiToBinary(char c) {
        return Integer.toBinaryString(c);
    }

    private static char binaryToAscii(String c) {
        return (char) Integer.parseInt(c, 2);
    }

    private static void init() // initialize data
    {
        root = NYT = new Node(NYTCode, StartId, "");
        Arrays.fill(isTaken, false);
        isTaken[StartId] = true;
    }

    private static Node insert(Node curr, char c) {
        Node data = new Node(c, getId(curr.id), curr.code + "1");
        Node nyt = new Node(NYTCode, getId(data.id), curr.code + "0");
        ++data.freq;
        ++curr.freq;
        curr.left = nyt;
        curr.right = data;
        data.parent = nyt.parent = curr;
        NYT = nyt;
        return curr;
    }

    private static Node update(Node curr) {
        while (curr != root && curr != null) {
            curr = SwapIfYouCan(curr);
            curr.freq++;
            curr = curr.parent;
        }
        root.freq = root.right.freq + root.left.freq;
        return curr;
    }

    private static Node SwapIfYouCan(Node req) {
        Queue<Node> q = new LinkedList<>();
        q.add(root.right);
        q.add(root.left);
        while (!q.isEmpty()) {
            Node curr = q.remove();
            if (curr.right != null) q.add(curr.right);
            if (curr.left != null) q.add(curr.left);
            if (canBeSwapped(req, curr)) {
                swap(req, curr);
                updateCodes();
                break;
            }
        }
        return req;
    }

    private static Node swap(Node from, Node to) // returns the from node
    {
        int tempId = from.id;
        from.id = to.id;
        to.id = tempId;

        String tempCode = from.code;
        from.code = to.code;
        to.code = tempCode;

        Node fromParent = from.parent;
        Node toParent = to.parent;
        if (fromParent == toParent) {
            if (fromParent.left == to) {
                fromParent.right = to;
                fromParent.left = from;
            } else {
                fromParent.right = from;
                fromParent.left = to;
            }
        } else {
            from.parent = toParent;
            to.parent = fromParent;
            if (fromParent.left == from) fromParent.left = to;
            else fromParent.right = to;

            if (toParent.left == to) toParent.left = from;
            else toParent.right = from;
        }
        return to;
    }

    private static boolean canBeSwapped(Node from, Node to) {
        return (from.id < to.id && from.freq >= to.freq && from.parent != to && to != root);
    }

    private static Node find(char s, Node curr) {
        Node ret = null;
        if (curr.symbol == s) ret = curr;
        if (curr.left != null && ret == null) ret = find(s, curr.left);
        if (curr.right != null && ret == null) ret = find(s, curr.right);
        return ret;
    }

    private static Node find(Node curr, String s) {
        Node ret = null;
        if (s.isEmpty()) ret = curr;
        else if (s.charAt(0) == '0') ret = find(curr.left,s.substring(1));
        else if (s.charAt(0) == '1') ret = find(curr.right,s.substring(1));
        return ret;
    }

    private static Node find(String prefix) {
        return find(root,prefix);
    }
    private static int NodeCase (Node curr)
    {
        // -1 -> multiple prefixes
        // 0 -> not found
        // 1 -> only one
        if (curr == null) return 0;
        if (curr.left != null || curr.right != null) return -1;
        else return 1;
    }
    private static Node find(char s) {
        return find(s, root);
    }

    private static int getId(int last) {
        while (isTaken[last]) {
            --last;
        }
        isTaken[last] = true;
        return last;
    }

    private static void updateCodes() {
        updateCode(root, "");
    }

    private static void updateCode(Node curr, String code) {
        curr.code = code;
        if (curr.right != null) updateCode(curr.right, code + "1");
        if (curr.left != null) updateCode(curr.left, code + "0");
    }

    //--------------------------------------------------------------------------------------------------------
//    private static String getCode (Node req,Node curr,String ret)
//    {
//        String ans = "";
//        if (curr == req) ans = ret;
//        if (curr.right != null && ans.isEmpty()) ans = getCode(req,curr.right,ret+"1");
//        if (curr.left != null && ans.isEmpty()) ans = getCode(req,curr.left,ret+"0");
//        return ans;
//    }
//    private static String getCode(Node req)
//    {
//        return getCode(req,root,"");
//    }
//    private static void updateCodes()
//    {
//        Queue<Node> q = new LinkedList<>();
//        q.add(root.right);
//        q.add(root.left);
//        while (!q.isEmpty()) {
//            Node curr = q.remove();
//            curr.code = getCode(curr);
//            if (curr.right != null) q.add(curr.right);
//            if (curr.left != null) q.add(curr.left);
//        }
//    }
    public static void PrintTree(@NotNull PrintStream out) {
        out.println();
        List<List<String>> lines = new ArrayList<>();

        List<Node> level = new ArrayList<>();
        List<Node> next = new ArrayList<>();

        level.add(root);
        int nn = 1;

        int widest = 0;

        while (nn != 0) {
            List<String> line = new ArrayList<>();

            nn = 0;

            for (Node n : level) {
                if (n == null) {
                    line.add(null);

                    next.add(null);
                    next.add(null);
                } else {
                    String aa = n.toString();
                    line.add(aa);
                    if (aa.length() > widest) widest = aa.length();

                    next.add(n.left);
                    next.add(n.right);

                    if (n.left != null) nn++;
                    if (n.right != null) nn++;
                }
            }

            if (widest % 2 == 1) widest++;

            lines.add(line);

            List<Node> tmp = level;
            level = next;
            next = tmp;
            next.clear();
        }

        int perpiece = lines.get(lines.size() - 1).size() * (widest + 4);
        for (int i = 0; i < lines.size(); i++) {
            List<String> line = lines.get(i);
            int hpw = (int) Math.floor(perpiece / 2f) - 1;

            if (i > 0) {
                for (int j = 0; j < line.size(); j++) {

                    // split node
                    char c = ' ';
                    if (j % 2 == 1) {
                        if (line.get(j - 1) != null) {
                            c = (line.get(j) != null) ? '┴' : '┘';
                        } else {
                            if (j < line.size() && line.get(j) != null) c = '└';
                        }
                    }
                    out.print(c);

                    // lines and spaces
                    if (line.get(j) == null) {
                        for (int k = 0; k < perpiece - 1; k++) {
                            out.print(" ");
                        }
                    } else {

                        for (int k = 0; k < hpw; k++) {
                            out.print(j % 2 == 0 ? " " : "─");
                        }
                        out.print(j % 2 == 0 ? "┌" : "┐");
                        for (int k = 0; k < hpw; k++) {
                            out.print(j % 2 == 0 ? "─" : " ");
                        }
                    }
                }
                out.println();
            }

            // print line of numbers
            for (int j = 0; j < line.size(); j++) {

                String f = line.get(j);
                if (f == null) f = "";
                int gap1 = (int) Math.ceil(perpiece / 2f - f.length() / 2f);
                int gap2 = (int) Math.floor(perpiece / 2f - f.length() / 2f);

                // a number
                for (int k = 0; k < gap1; k++) {
                    out.print(" ");
                }
                out.print(f);
                for (int k = 0; k < gap2; k++) {
                    out.print(" ");
                }
            }
            out.println();

            perpiece /= 2;
        }
    }
}
