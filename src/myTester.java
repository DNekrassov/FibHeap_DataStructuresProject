//package src;

import java.util.ArrayList;
import java.util.Collections;

public class myTester {

	public static void main(String[] args) {
		FibonacciHeap fibonacciHeap = new FibonacciHeap();
	
	     String test = "test0";
	        fibonacciHeap = new FibonacciHeap();

	        ArrayList<Integer> numbers = new ArrayList<>();

	        for (int i = 0; i < 5; i++) {
	            numbers.add(i);
	        }

	        Collections.shuffle(numbers);

	        for (int i = 0; i < 5; i++) {
	            fibonacciHeap.insert(numbers.get(i));
	        }

	        for (int i = 0; i < 5; i++) {
	        
	            if (fibonacciHeap.findMin().getKey() != i) {
	            	System.out.println("mistake in " + i + " loop");
	                return;
	            }
	            fibonacciHeap.deleteMin();
	        }	
        	System.out.println("success");

	}
		
		
		/*heap.insert(1);
		System.out.println(heap.findMin().getKey());
		heap.insert(0);
		System.out.println(heap.findMin().getKey());
		
		System.out.println("*****************");
		heap.deleteMin();
		
		heap.insert(2);
		heap.insert(4);
		System.out.println("*****************");
		System.out.println(heap.findMin().key);

		heap.insert(3);
		heap.insert(0);
		System.out.println("*****************");
		System.out.println(heap.findMin().key);
		heap.deleteMin();

	}
	*/
}
