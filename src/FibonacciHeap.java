/**
 *
 * @authors
 * Daniel Nekrassov - nekrassov - 314918012
 * Lior Grinberg - liorgrinberg - 308322866
 *
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap {

	private HeapNode first; //references the first heap-node in the heap
	private HeapNode min; //references the minimal heap-node in the heap
	private int size; // counts amount of nodes in the heap
	private int treeNum; //counts # of trees in the heap
	private int markedNum; //counts # of marked nodes in the heap

	public static int cuts; //counts all cut operations in a given execution
	public static int links; //counts all link operations in a given execution

	/**
	 * public boolean isEmpty()
	 *
	 * precondition: none
	 * 
	 * The method returns true if and only if the heap is empty.
	 *
	 * Complexity: O(1)
	 */
	public boolean isEmpty() {
		return this.first == null;
	}

	/**
	 * public HeapNode insert(int key)
	 *
	 * Creates a node (of type HeapNode) which contains the given key, and inserts
	 * it into the heap.
	 * 
	 * Returns the new node created.
	 *
	 * Complexity : O(1)
	 */
	public HeapNode insert(int key) {
		HeapNode node = new HeapNode(key);
		this.first = mergeNodeLists(node, this.first); //sets node as first node
		this.size++;
		this.treeNum++;
		setMin(node);
		return node;
	}

	/**
	 * public void deleteMin()
	 *
	 * Delete the node containing the minimum key.
	 *
	 * Complexity: O(treeNum) = O(log n) amortized - depends on successiveLinking()
	 */
	public void deleteMin() {
		// function should work only in case of non-empty tree
		
		if (this.isEmpty()){
			return;
		}
		this.setSize(this.getSize() - 1); //decreases size by 1
		HeapNode minNode = this.findMin();
	
		//case 1 - there is only a single tree in heap

		if (minNode.getNext() == minNode){
			this.min = null;
			this.first = null;
		}
		//case 2 - if there's more than one tree - cut away the min-node tree
		else{
			if(this.min == this.first) {
				this.first = this.min.next;
			}
			this.min.getNext().setPrev(this.min.getPrev());
			this.min.getPrev().setNext(this.min.getNext());
			this.min = this.min.getNext(); //temporary - so that min won't be null
			minNode.setNext(null);
			minNode.setPrev(null);
		}

		//make minNodes children into roots and merge with tree - O(minNode.rank)
		if (minNode.getChild() != null){
			HeapNode currentChild = minNode.getChild();
			//set each child's parent as null - t
			do{
				currentChild.setParent(null);
				currentChild = currentChild.getNext();
				if(currentChild.getMark()){
					currentChild.setMark(false);
					markedNum--;
				}
			} while (currentChild != minNode.getChild());
		}
		
		HeapNode x = mergeNodeLists(this.min, minNode.getChild());

		//if case 1 has occurred - we need to set up a first node and a min node
		if (this.min == null){
			this.min = x; //temporary - so that won't be null
			this.first = x;
		}

		//if heap is now empty (this.first is still null) - our job is done
		if (this.first == null){
			return;
		}

		//execute Successive Linking - first, min nodes are updated there
		successiveLinking();
	}

	/**
	 * public HeapNode findMin()
	 *
	 * Return the node of the heap whose key is minimal.
	 * If tree is empty - returns null
	 *
     * Time complexity - O(1)
	 */
	public HeapNode findMin() {
		return this.min;
	}

	/**
	 * public void meld (FibonacciHeap heap2)
	 *
	 * Meld this heap with heap2
	 *
	 * Complexity: O(1)
	 */
	public void meld(FibonacciHeap heap2) {
		this.setTreeNum(this.getTreeNum() + heap2.getTreeNum());
		this.setMarkedNum(this.getMarkedNum() + heap2.getMarkedNum());
		this.setFirst(mergeNodeLists(this.getFirst(), heap2.getFirst()));
		this.setMin(heap2.findMin());
		this.setSize(this.getSize() + heap2.getSize());
	}

	/**
	 * public int size()
	 *
	 * Return the number of elements in the heap
	 * Time complexity - O(1)
	 */
	public int size() {
		return this.size;
	}

	/**
	 * public int[] countersRep()
	 *
	 * Return a counters array, where the value of the i-th entry is the number of
	 * trees of order i in the heap.
	 *
	 * Complexity: O(treeNum) = O(log(n)) amortized - since it passes through all trees in the heap
	 */
	public int[] countersRep() {
		//if tree is empty - returns an empty array
		if (this.isEmpty()){
			return new int[]{};
		}

		HeapNode node = this.first;
		int[] counterArr = new int[(int) (Math.log(this.size)/Math.log(2.0)) + 1];

		//goes over each tree in the heap, and updates the array according to the tree's order
		do{
			counterArr[node.getRank()]++;
			node = node.getNext();
		} while (node != this.first);

		return counterArr;
	}

	/**
	 * public void delete(HeapNode x)
	 *
	 * Deletes the node x from the heap.
	 *
	 * Executes by lowering the node's key below the current minimum, and then deleting the (new) minimal node
	 *
	 * Complexity: O(log n) amortized - deleteMin is O(log n) amortized and decreaseKey is O(1) amortized
	 */
	public void delete(HeapNode x) {
	
		decreaseKey(x, x.key - this.findMin().key + 1);
		deleteMin();
	}

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 *
	 * The function decreases the key of the node x by delta. If needed, activates cascadingCuts and/or updates the minimal node.
	 * Complexity: O(1) amortized - depends on the amount of times cascadingCuts is called upon
	 */
	public void decreaseKey(HeapNode x, int delta) {
		x.key = x.getKey() - delta;
		//in the case that the node is not a root or it's key is lower than it's parents - we need to cut
		if (x.getParent() != null && x.key < x.getParent().key){
			cascadingCuts(x);
		}
		//check if the node with the new key is minimal or not
		if (x.key < this.findMin().key){
			this.min = x;
		}
	}

	/**
	 * public int potential()
	 *
	 * This function returns the current potential of the heap, which is: Potential
	 * = #trees + 2*#marked The potential equals to the number of trees in the heap
	 * plus twice the number of marked nodes in the heap.
	 *
	 * Time complexity - O(1)
	 */
	public int potential() {
		return this.treeNum + 2*this.markedNum;
	}

	/**
	 * public static int totalLinks()
	 *
	 * This static function returns the total number of link operations made during
	 * the run-time of the program. A link operation is the operation which gets as
	 * input two trees of the same rank, and generates a tree of rank bigger by one,
	 * by hanging the tree which has larger value in its root on the tree which has
	 * smaller value in its root.
	 *
	 * Time complexity - O(1)
	 */
	public static int totalLinks() {
		return links;
	}

	/**
	 * public static int totalCuts()
	 *
	 * This static function returns the total number of cut operations made during
	 * the run-time of the program. A cut operation is the operation which
	 * disconnects a subtree from its parent (during decreaseKey/delete methods).
	 */
	public static int totalCuts() {
		return cuts;
	}

	/**
	 * public static int[] kMin(FibonacciHeap H, int k)
	 *
	 * This static function returns the k minimal elements in a binomial tree H. The
	 * function should run in O(k*deg(H)). You are not allowed to change H.
	 *
	 * Explanation for complexity - each loop we insert O(deg(H)) elements to kMinHeap - total O(k*deg(H)) insertions.
	 * deletion - takes O(log(n)) amortized - therefore at most O(log(k*deg(H))) per loop - total O(log(k*deg(H))) which
	 * is also O(k*deg(H)) - further explanation in PDF
	 */
	public static int[] kMin(FibonacciHeap H, int k) {
		//if H is empty/null or k is not positive - returns an empty array
		if (H == null || k <= 0 || H.isEmpty()){
			return new int[]{};
		}
		int[] arr = new int[k]; //create a new array to keep all k minimal keys
		FibonacciHeap kMinHeap = new FibonacciHeap(); //temporary fibonacci heap to be used in the process

		//processes to root of the binary heap
		HeapNode currentMin = H.findMin();
		arr[0] = currentMin.getKey();
		if (k == 1){
			return arr;
		}

		//processes the rest of the k-1 nodes
		for (int i = 1; i < k ; i++){

			//adds the children of the "deleted" node to the temporary heap
			HeapNode currentChild = currentMin.getChild();
			if (currentChild != null){
				do{
					HeapNode x = kMinHeap.insert(currentChild.getKey());
					x.setkMinOriginalNode(currentChild);

					currentChild = currentChild.getNext();
				} while (currentChild != currentMin.getChild());
			}

			//finds the minimal node in the original tree, so it's children will be added to the temporary heap
			currentMin = kMinHeap.findMin().getkMinOriginalNode();
			arr[i] = currentMin.getKey(); //adds the minimal node's key to the array
			kMinHeap.deleteMin(); //deletes the minimal node from the temporary heap
		}
		return arr;
	}

	/**
	 * Getters and Setters
	 * Time complexity - O(1)
	 */
	public HeapNode getFirst(){return this.first;}
	private void setFirst(HeapNode node){this.first = node;}
	public int getSize(){return this.size;}
	private void setSize(int size){this.size = size;}
	public int getTreeNum(){ return treeNum;}
	private void setTreeNum(int treeNum){ this.treeNum = treeNum;}
	public int getMarkedNum(){ return markedNum;}
	private void setMarkedNum(int markedNum){this.markedNum = markedNum;}

	/**
	 * Checks if node has a lower key than the current min node (if we have any), if yes - sets him to be the minimal node
	 * Time complexity - O(1)
	 */
	private void setMin(HeapNode node) {
		if (this.min == null){
			this.min = node;
		}
		else if (node.getKey() < this.min.getKey()) {
			this.min = node;
		}
	}

	/**
	 * Takes 2 lists and merges them into a single list such that first.getPrev = last node in second list and
	 * second.gerPrev = last node in first list.
	 * Returns first node of first list (or first node of second list if first is null)
	 */
	private static HeapNode mergeNodeLists(HeapNode first, HeapNode second){
		if ( first == null && second == null){
			return null;
		}
		if (first == null){
			return second;
		}
		if (second == null){
			return first;
		}
		//links the lists to form a circular DLL
		
		
		HeapNode tmp = first.getPrev();
		first.setPrev(second.getPrev());
		second.getPrev().setNext(first);
		second.setPrev(tmp);
		second.getPrev().setNext(second);
		return first;
	}

	/**
	 * Executes a Successive Linking process, in which similarly ranked nodes are linked, from lowest to highest -
	 * so that in the end we have log2(size()) amount of trees in the heap.
	 * Updates the first and min fields of the heap, and the amount of trees in the heap
	 * @pre - isEmpty() == false
	 *
	 * uses updateHeapByArray(), linkRoots() methods
	 * Time complexity - O(n) w.c, O(log n) amortized - since it passes all trees in the heap
	 */
	private void successiveLinking() {
		int arraySize = Math.max(2 ,((int)(Math.log(this.size) / Math.log(2))) + 1);
		HeapNode[] arr = new HeapNode[arraySize]; // sets array to keep the roots. Size of the maximal possible tree's rank
		HeapNode root = this.first;
		HeapNode tmp;
		arr[root.getRank()] = root; //insert the first root to the array
		
		root = root.next;

		while(root != this.first) { // iterate on all the roots of the heap
			tmp = root.next; // keep pointer on root.next before changing it
			if (arr[root.getRank()] == null) {
				arr[root.getRank()] = root;
			}
			else { // there is already root of the same rank in the array- link them
				while(arr[root.getRank()] != null) { // keep linking
					links++;
					root = linkRoots(root, arr[root.getRank()]); // root is updated to be the root of the linked tree
					arr[root.getRank() - 1] = null; // delete the previous non-linked tree of the array
				}
				arr[root.getRank()] = root;
			}
			root = tmp;
		}
		// Update the heap using the array
		updateHeapByArray(arr);
	}

	/**
	 * The method gets an array of order O(log n), and iterate over it one time
	 * with local changes in (fields & pointers) in each iteration.
	 * Time complexity - O(log n)
	 */
	private void updateHeapByArray(HeapNode[] arr) {
		// initialize the Heap before updating it
		this.first = null;
		this.min = null;
		HeapNode last = null;

		setTreeNum(0);
		
		//iterate the array, update field of the heap, and connect the trees
		for(HeapNode node : arr) {
			if (node != null) { // add node to the heap
				if (first == null) {
					this.first = node;
					this.min = first;
					last = first;
				}
				else { // heap is not empty
					last.next = node;
					if(node.getKey() < this.min.getKey()) { // update min
						this.min = node;
					}
					node.prev = last;
					last = node;
				}
				setTreeNum(getTreeNum() + 1);
			}
		}
		//connect the first and last trees
		last.next = first;
		first.prev = last;
	}

	/**
	 * link two given trees with the same rank and return the new tree.
	 * We hang one on the other
	 * Time complexity - O(1)
	 */
	private HeapNode linkRoots(HeapNode root, HeapNode other) {
		HeapNode tmp;

		if(root.getKey() > other.getKey()) { //replace between them, so you'll hang 'other' on 'root'
			tmp = root;
			root = other;
			other = tmp;
		}
		other.setNext(other);
		other.setPrev(other);

		other = mergeNodeLists(other, root.getChild()); //set other's brothers

		// hang 'other' on root
		other.setParent(root);
		root.setChild(other);

		root.next = root;
		root.prev = root;

		root.rank += 1; // increase the rank, so it will match the new tree
		return root;
	}

	/**
	 * Executes a cascading cut process, cuts a node from its parent
	 * If parent was already marked - recursively cuts that node from its parent too
	 * @param node - node that is to be cut from it's parent
	 *
	 * Time Complexity: O(1) amortized - depending on the amount of cuts to be made
	 */
	private void cascadingCuts(HeapNode node){

		//in case we reached a root in the cascading cuts
		if (node.getParent() == null){
			return;
		}

		cuts++;

		//cutting the node from its siblings side, if he has any
		if(node.getNext() != node){
			node.getNext().setPrev(node.getPrev());
			node.getPrev().setNext(node.getNext());
		}

		HeapNode parent = node.getParent();

		//if node is a child to a parent, need to move it to another sibling, if he has any
		if(parent.getChild() == node){
			if(node.getNext() != node){
				parent.setChild(node.getNext());
			}
			else{
				parent.setChild(null);
			}
		}

		parent.setRank(1); //decrease parent's degree

		//cutting away node from siblings and parent, and making sure it is not marked before we put it in root
		node.setParent(null);
		node.setPrev(node);
		node.setNext(node);
		if (node.getMark()){
			node.setMark(false);
			this.markedNum--;
		}

		//inserts node into root list from the left
		this.first =  mergeNodeLists(node, this.first);
		this.treeNum++;

		if (parent.getMark()){
			cascadingCuts(parent);
		}
		else if (parent.getParent() != null){
			parent.setMark(true);
			this.markedNum++;
		}
	}

	/**
	 * public class HeapNode
	 * 
	 * If you wish to implement classes other than FibonacciHeap (for example
	 * HeapNode), do it in this file, not in another file
	 * 
	 * All the methods in the class have Time-Complexity of O(1), due to dealing with pointers and fields alone.
	 */
	public class HeapNode {

		public int key;

		private int rank;		 //number of children
		private boolean mark; 	 //Stores if the node is marked (if mark == True, it is considered marked for decreaseKey purposes)
		private HeapNode child;  //Store the "leftmost" child
		private HeapNode next; 	 //Store the "right" sibling
		private HeapNode prev; 	 //Store the "left" sibling
		private HeapNode parent; //Store the node’s parent
		private HeapNode kMinOriginalNode; //Used in kMin, stores a pointer to the original node from the original heap from which this node’s key was taken.

		 /**
	     * Constructor of an HeapNode
	     */
		public HeapNode(int key) {
			this.key = key;
			this.next = this;
			this.prev = this;
		}

		/**
		 * Getters and Setters
		 */
		public int getKey() {
			return this.key;
		}

		public int getRank() {
			return this.rank;
		}

		public boolean getMark() {
			return this.mark;
		}

		public HeapNode getChild() {
			return this.child;
		}

		public HeapNode getNext() {
			return this.next;
		}

		public HeapNode getPrev() {
			return this.prev;
		}

		public HeapNode getParent() {
			return this.parent;
		}

		/**
		 * Sets rank of node as (this.rank - difference)
		 */
		public void setRank(int difference) {
			this.rank -= difference;
		}

		public void setMark(boolean mark) {
			this.mark = mark;
		}

		public void setChild(HeapNode node) {
			this.child = node;
		}

		public void setNext(HeapNode node) {
			this.next = node;
		}

		public void setPrev(HeapNode node) {
			this.prev = node;
		}

		public void setParent(HeapNode node) {
			this.parent = node;
		}

		public HeapNode getkMinOriginalNode(){
			return this.kMinOriginalNode;
		}

		public void setkMinOriginalNode(HeapNode node){
			this.kMinOriginalNode = node;
		}

	}
}
