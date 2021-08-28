//package src;

import java.time.Duration;
import java.time.Instant;


//import src.FibonacciHeap.HeapNode;

public class Part2 {

	public static void main(String[] args) {
		Sequence1(1024);

		Sequence2(1000);
	}
	
	public static void Sequence1(int m) {
		FibonacciHeap heap = new FibonacciHeap();
		FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[m+1];
		
		Instant start = Instant.now();
		long startTime = System.nanoTime();

		
		for(int i = m; i >=0; i--) {
		//	for(int i = 0; i <=m; i++) {  doesn't matter which way (Think so) 
			nodes[i] = heap.insert(i);
		}
		
		heap.deleteMin();
		
		int delta = m - 1;
		double sum = 0;
		int index = 2;
		
		//first 1 for index 2
		heap.decreaseKey(nodes[index], delta);
		
		for(int i = 1; i < (int)(Math.log(m) / Math.log(2)) -1; i++) {
			sum = Math.pow(0.5, i);
			index += (int)(m * sum); 
			heap.decreaseKey(nodes[index], delta);
		}
		
		heap.decreaseKey(nodes[m-1], delta);
		
		Instant end = Instant.now();
		long stopTime = System.nanoTime();

		System.out.println("Sum-Up Squence1: " );
		System.out.println("***************************" );
		System.out.println("Run-Time nanoTime: " + (stopTime - startTime));
		System.out.println("Run-Time Instant: " + Duration.between(start, end));
		System.out.println("Total Links: " + heap.totalLinks());
		System.out.println("Total Cuts: " + heap.totalCuts());
		System.out.println("Potential: " + heap.potential());
		System.out.println("***************************" );

	}

	public static void Sequence2(int m) {
		
		FibonacciHeap heap = new FibonacciHeap();
		
		Instant start = Instant.now();
		long startTime = System.nanoTime();

		for (int i = m; i > 0; i--) {
			heap.insert(m);
		}
		for(int i = 1; i < m/2; i++) {
			heap.deleteMin();
		}
		
		Instant end = Instant.now();
		long stopTime = System.nanoTime();
		
		System.out.println("Sum-Up Squence2: " );
		System.out.println("***************************" );
		System.out.println("Run-Time nanoTime: " + (stopTime - startTime));
		System.out.println("Run-Time Instant: " + Duration.between(start, end));
		System.out.println("Total Links: " + heap.totalLinks());
		System.out.println("Total Cuts: " + heap.totalCuts());
		System.out.println("Potential: " + heap.potential());
		System.out.println("***************************" );

	}
	
}
