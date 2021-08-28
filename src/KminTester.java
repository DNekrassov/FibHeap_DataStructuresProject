import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KminTester {
    public static void main(String[] args){
        FibonacciHeap H = new FibonacciHeap();
        int j = 20; // Should be between 2 to 30
        double n = Math.pow(2, j)+1;

        List<Integer> numbers = new ArrayList<Integer>();

        for (int i = 0 ; i < n; i++){
            numbers.add(i);
        }

        Collections.shuffle(numbers);

        for (int i = 0 ; i < n; i++){
            H.insert(numbers.get(i));
        }
        H.deleteMin();

        int k = (int)Math.pow(2, j);
        System.out.println(Arrays.toString(FibonacciHeap.kMin(H, k)));
    }
}
