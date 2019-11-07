package com.company;

import java.util.Scanner;

public class Main {
    private static Scanner in = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println("Welcome to Adaptive huffman Encoder/Decoder");
        while (true) {
            System.out.println("\n1- Encode\n2- Decode\nAny other character will halt");
            String c = in.nextLine();
            if (c.equals("1")) {
                System.out.println("Enter the text line to encode");
                String text = in.nextLine();
                try {
                    System.out.println(Adaptive_Huffman.Encode(text));
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                    continue;
                }
            }
            else if (c.equals("2")) {
                System.out.println("Enter the encoded bits to decode");
                String text = in.nextLine();
                try {
                    System.out.println(Adaptive_Huffman.Decode(text));
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    continue;
                }
            }
            else break;
            Adaptive_Huffman.PrintTree(System.out);
        }
    }
}
