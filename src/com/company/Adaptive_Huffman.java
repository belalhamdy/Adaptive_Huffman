package com.company;

import java.io.PrintStream;

public class Adaptive_Huffman {
    final static char NYT = '\r';
    final static short StartId = 747; // -> lucky number
     private static class Node{
        public String code; // Code of encoding value of that symbole
        public int freq,id;
        char symbol; // NYT is defined as "\r" (carriage return) -> can be changed later
        Node(char s,int id)
        {
            symbol = s;
            freq = 1;
        }
    }
    static Node root;
    public static String Encode(String text)
    {
        init();

        return "";
    }
    public static String Decode(String text)
    {
        init();

        return "";
    }
    private static void init() // initialize data
    {
        root = new Node(NYT,StartId);
    }
    public static void PrintTree(PrintStream out)
    {

    }
    private static void insert(Node curr)
    {

    }
    private static void update(Node curr)
    {

    }
    private static Node search (char s)
    {
        return null;
    }
    private static Node search (Node s)
    {
        return search(s.symbol);
    }

}
