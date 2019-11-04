package com.company;

import java.io.PrintStream;
import java.util.*;

public class Adaptive_Huffman {
    final static char NYTCode = '\r';
    final static short StartId = 747; // -> lucky number
    final static short MAX = StartId*2;
     private static class Node{
        public String code; // Code of encoding value of that symbole
        public int freq,id;
        public Node parent,left,right;
        char symbol; // NYT is defined as "\r" (carriage return) -> can be changed later

        Node(char symbol,int id, String code)
        {
            this.id = id;
            this.code = code;
            this.symbol = symbol;
            freq = 0;
            left = right = parent = null;
        }
    }
    static boolean[] isTaken = new boolean[MAX];
    static Node root;
    static Node NYT;
    public static String Encode(String text)throws Exception {
        init();
        StringBuilder ret = new StringBuilder();
        for (int i = 0 ; i<text.length();++i)
        {
            char letter = text.charAt(i);
            Node curr = find(letter);
            if (curr == null)
            {
                ret.append(NYT.code).append(asciiToBinary(letter));
                curr = insert(NYT,letter);
                update(curr.parent);
            }
            else
            {
                ret.append(curr.code);
                update(curr);
            }
            ++curr.freq;
        }
        return ret.toString();
    }
//    public static String Decode(String text) throws Exception {
//        init();
//        String prefix;
//        try {
//            prefix = text.substring(0,8);
//        }
//        catch (Exception ex)
//        {
//            throw new Exception("Invalid Input.. Please enter valid data");
//        }
//        char letter = binaryToAscii(prefix);
//        StringBuilder ret = new StringBuilder(letter);
//        prefix = "";
//        Node curr;
//        ArrayList<Node> prefixes;
//        for (int i = 8 ; i<text.length() ; ++i)
//        {
//            prefix+=text.charAt(i);
//            prefixes = find(prefix);
//            if (prefixes.size() == 0) throw new Exception("Invalid Input.. Please enter valid data");
//            else if (prefixes.size() == 1)
//            {
//                curr = prefixes.get(0);
//                if (curr.symbol != NYTCode) // try to exchange it with if (curr != NYT)
//                {
//                    //@TODO already inserted insertion
//
//                }
//                else
//                {
//                    //@TODO first insertion
//                }
//            }
//
//        }
//        if (prefix != null)
//        {
//
//        }
//        return ret.toString();
//    }
    private static String asciiToBinary (char c)
    {
        return Integer.toBinaryString(c);
    }
    private static char binaryToAscii (String c)
    {
        return (char)Integer.parseInt(c, 2);
    }
    private static void init() // initialize data
    {
        root = NYT = new Node(NYTCode,StartId,"");
        Arrays.fill(isTaken,false);
    }
    private static Node insert(Node curr,char c)
    {
        Node data = new Node(c,getId(curr.id),curr.code+"1");
        Node nyt = new Node(NYTCode,getId(data.id),curr.code+"0");
        ++data.freq;
        ++curr.freq; // NYT inc freq
        curr.left = nyt;
        curr.right = data;
        data.parent = nyt.parent = curr;
        NYT = nyt;
        return curr.right;
    }
    private static Node update(Node curr)
    {
        while (curr != root)
        {
            curr = SwapIfYouCan(curr);
        }
        return curr;
    }
    private static Node SwapIfYouCan(Node req)
    {
        Queue<Node> q = new LinkedList<>();
        q.add(root.right);
        q.add(root.left);
        while (!q.isEmpty())
        {
            Node curr = q.remove();
            q.add(curr.right);
            q.add(curr.left);
            if (canBeSwapped(req,curr))
            {
                swap(req,curr);
                break;
            }
        }
        return req.parent;
    }
    private static Node swap (Node from,Node to) // returns the from node
    {
        Node temp = new Node(from.symbol,from.id,from.code);
        temp.freq = from.freq;
        temp.right = from.right;
        temp.left = from.left;
        temp.parent = from.parent;

        from.id = to.id;
        from.code = to.code;
        from.freq = to.freq;
        from.right = to.right;
        from.left = to.left;
        from.parent = to.parent;

        to.id = temp.id;
        to.code = temp.code;
        to.freq = temp.freq;
        to.right = temp.right;
        to.left = temp.left;
        to.parent = temp.parent;

        return from;
    }
    private static boolean canBeSwapped(Node from , Node to)
    {
        return (from.id < to.id && from.freq >= to.freq && from.parent != to && to != root);
    }
//    private static ArrayList<Node> find (String s , Node curr)
//    {
//        //@TODO you must make this function returns list of nodes that can be prefix for that
//        if (s.isEmpty()) return curr;
//
//    }
//    private static ArrayList<Node> find (String s)
//    {
//        return find(s,root);
//    }
    private static Node find (char s , Node curr)
    {
        if (curr.symbol == s) return curr;
        if (curr.left != null) return find(s,curr.left);
        if (curr.right != null) return find(s,curr.right);
        return null;
    }
    private static Node find (char s)
    {
        return find(s,root);
    }
    private static int getId(int last)
    {
        while (isTaken[last]){
            --last;
        }
        isTaken[last] = true;
        return last;
    }
    public static void PrintTree(PrintStream out)
    {
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
                    String aa = n.symbol + " : " + n.freq;
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
                System.out.print(f);
                for (int k = 0; k < gap2; k++) {
                    out.print(" ");
                }
            }
            out.println();

            perpiece /= 2;
        }
    }
}
