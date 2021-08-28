//package Fibo;

import java.util.ArrayList;
import java.util.Collections;

public class printHeapElad {
    public static void main(String[] args)  {
        FibonacciHeap h1 = new FibonacciHeap();
        int[] arr = {35, 56, 42, 7, 0, 21, 63, 49, 28, 14};
        for (int i: arr){
            h1.insert(i);
        }

        printHeapFib(h1);
//        printHeapFib(h1);
         h1.deleteMin();
        printHeapFib(h1);
//        h1.deleteMin();
//        printHeapFib(h1);
//        h1.deleteMin();
//        printHeapFib(h1);
        h1.decreaseKey(h1.getFirst().getNext().getChild().getChild(), 40);
        printHeapFib(h1);
        h1.decreaseKey(h1.getFirst().getNext().getNext().getChild().getNext().getChild(), 56);
        printHeapFib(h1);

//        h1.delete(h1.getFirst().getNext());
//        printHeapFib(h1);


//        h1.deleteMin();
//        printHeapFib(h1);
//        h1.deleteMin();
//        printHeapFib(h1);
//        h1.deleteMin();
//        printHeapFib(h1);
//        h1.deleteMin();
//        printHeapFib(h1);
//        h1.deleteMin();
//        System.out.println(h1.isEmpty());

    }





    public static  void printHeapFib(FibonacciHeap heap) {
        System.out.println("-----------------------------------------------");
        String[] list = new String[heap.size()*10];
        for (int i = 0; i < heap.size(); i++) {
            list[i] = "";
        }
        Integer level = 0;
        printHeapFib(heap.getFirst(), list, level);
        for (int i = 0; i < heap.size(); i++) {
            if (list[i]!="")
                System.out.println(list[i]);
        }
        System.out.println("-----------------------------------------------");
    }

    public  static void printHeapFib(FibonacciHeap.HeapNode node, String[] list, Integer level) {

        list[level] += "(";
        if (node == null) {
            list[level] += ")";
            return;
        } else {
            FibonacciHeap.HeapNode temp = node;
            do {
                list[level] += temp.getKey();
                FibonacciHeap.HeapNode k = temp.getChild();
                printHeapFib(k, list, level + 1);

                list[level] +=getChain("=");
                temp = temp.getNext();
            } while (temp != node);
            list[level] += ")";
        }


    }
    private static String getChain(String link){
        int count =2;;
        String str="";
        if (count==0){
            return "";
        }
        for (int i=0;i<count/3+1;i++){
            str+=link;
        }
        return str+">";
    }
    public static void insertN(FibonacciHeap h1,int n) {
        insertN( h1,1, n);

    }


    public static void insertN(FibonacciHeap h1,int start, int amount) {
        ArrayList<Integer> mylist = new ArrayList();
        for(int i = start; i <  start+amount; i++) {
            mylist.add(i);
        }
        Collections.shuffle(mylist);
        for (int i = 0; i < amount; i++) {
            h1.insert(mylist.get(i));

        }
    }

}

