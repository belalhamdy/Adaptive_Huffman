package com.company;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Adaptive_Huffman {
    final static char NYT = '\r';
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
    public static String Encode(String text)
    {
        init();
        for (int i = 0 ; i<text.length();++i)
        {

        }
        return "";
    }
    public static String Decode(String text)
    {
        init();

        return "";
    }
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
        root = new Node(NYT,StartId,"");
        Arrays.fill(isTaken,false);
    }
    private static Node insert(Node curr,char c)
    {
        Node data = new Node(c,getId(curr.id),curr.code+"1");
        Node nyt = new Node(NYT,getId(data.id),curr.code+"0");
        ++data.freq;
        curr.left = nyt;
        curr.right = data;
        data.parent = nyt.parent = curr;
        return curr;
    }
    private static Node update(Node curr)
    {

    }
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
