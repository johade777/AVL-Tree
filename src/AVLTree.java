import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * @author David Patterson
 */

/**
 * @author loki
 *
 * @param <T>
 */
public class AVLTree<T extends Comparable<T>> {
	public static int rotationCount;
	private int size;
	private BinaryNode root;
	public boolean altered = false;

	
	/**
	 * Creates a new AVL tree with a null root and rotation count set to zero
	 */
	public AVLTree() {
		this.root = null;
		rotationCount = 0;
	}

	/**
	 * Takes in an object and inserts it into the tree by calling insert within the BinaryNode class. 
	 * Runs in 2*log(n) time due to using a contain method check before inserting
	 * 
	 * @param obj
	 * @return boolean
	 */
	public boolean insert(T obj) {
		if (obj == null) {
			throw new IllegalArgumentException();
		}
		if (this.root == null) {
			this.root = new BinaryNode(obj);
			this.size++;
			return true;
		}
		if (this.root.contains(obj)) {
			return false;
		}
		this.size++;
		this.root = this.root.insert(obj);
		return true;
	}

	/**
	 * Takes in an object and removes it from the tree by calling remove within the BinaryNode class. 
	 * Runs in 2*log(n) time due to using a contain method check before inserting
	 * 
	 * @param obj
	 * @return boolean
	 */
	public boolean remove(T obj) {
		if (obj == null) {
			throw new IllegalArgumentException();
		}

		if (this.root == null) {
			return false;
		}

		if (!this.root.contains(obj)) {
			return false;
		}

		this.root = this.root.remove(obj);
		this.size--;
		return true;
	}

	/**
	 * Gets total times tree performed a rotation
	 * @return int
	 */
	public int getRotationCount() {
		return AVLTree.rotationCount;
	}

	/**
	 * Creates a new tree iterator
	 * @return iterator
	 */
	public Iterator<T> iterator() {
		return new iterator();
	}

	/**
	 * @author David Patterson
	 * 
	 * Inner class of AVLTree, BinaryNode
	 */
	class BinaryNode {
		private T data;
		private BinaryNode left;
		private BinaryNode right;
		private int height;

		/**
		 * Constructor for a null node
		 */
		public BinaryNode() {
			this.data = null;
			this.left = null;
			this.right = null;
			this.height = 0;
		}
 
		/**
		 * Constructs a new node with given element
		 * @param obj
		 */
		public BinaryNode(T obj) {
			this.data = obj;
			this.left = null;
			this.right = null;
			this.height = 0;
		}

		/**
		 * Recursively calls insert until it reaches a spot to insert new node. 
		 * Checks if current node needs re-balancing and returns. 
		 * 
		 * @param obj
		 * @return BinaryNode
		 */
		public BinaryNode insert(T obj) {
			if (this.data.compareTo(obj) > 0) {
				if (this.left == null) {
					this.left = new BinaryNode(obj);
					this.height = this.getHeight();
					return this;
				}
				this.left = this.left.insert(obj);
				BinaryNode temp = this.checkBalance();
				temp.height = temp.getHeight();
				return temp;
			}

			if (this.data.compareTo(obj) < 0) {
				if (this.right == null) {
					this.right = new BinaryNode(obj);
					this.height = this.getHeight();
					return this;
				}
				this.right = this.right.insert(obj);
				BinaryNode temp = this.checkBalance();
				temp.height = temp.getHeight();
				return temp;
			}

			return this; // shouldn't get here
		}

		/**
		 * Recursively calls remove until it finds node to be removed.  
		 * Checks if current node needs re-balancing and returns.
		 * 
		 * @param obj
		 * @return BinaryNode
		 */
		public BinaryNode remove(T obj) {
			if (obj.compareTo(this.data) < 0) {
				this.left = this.left.remove(obj);
			}
			if (obj.compareTo(this.data) > 0) {
				this.right = this.right.remove(obj);
			}
			if (obj.compareTo(this.data) == 0) {
				if (this.left != null && this.right != null) {
					this.data = this.left.getMax().data;
					this.left = this.left.remove(this.data);
				} else {
					return (this.left != null) ? this.left : this.right;
				}
			}
			this.height = this.getHeight();
			BinaryNode temp = this.checkBalance();
			temp.getHeight();
			return temp;
		}

		/**
		 * Tests to see if the current node needs re-balanced based of height of left and right child
		 * 
		 * @return BinaryNode
		 */
		private BinaryNode checkBalance() {
			int leftHeight = (this.left != null) ? this.left.height : -1;
			int rightHeight = (this.right != null) ? this.right.height : -1;

			if (rightHeight - leftHeight >= 2) {
				BinaryNode temp = this.right;

				int tempLeftHeight = (temp.left != null) ? temp.left.height: -1;
				int tempRightHeight = (temp.right != null) ? temp.right.height: -1;
				if (tempLeftHeight > tempRightHeight) {
					this.right = temp.SingleRightRotation();
				}
				return this.SingleLeftRotation();
			}
			
			if (leftHeight - rightHeight >= 2) {
				BinaryNode temp = this.left;

				int tempLeftHeight = (temp.left != null) ? temp.left.height: -1;
				int tempRightHeight = (temp.right != null) ? temp.right.height: -1;
				if (tempRightHeight > tempLeftHeight) {
					this.left = temp.SingleLeftRotation();
				}
				return this.SingleRightRotation();
			}

			return this; // shouldn't make it here
		}

		/**
		 * Returns the node with the greatest element data within tree
		 * 
		 * @return BinaryNode
		 */
		private BinaryNode getMax() {
			BinaryNode temp = this;
			while (temp.right != null) {
				temp = temp.right;
			}
			return temp;
		}

		/**
		 * Performs a single right rotation to balance tree
		 * 
		 * @return BinaryNode
		 */
		private BinaryNode SingleRightRotation() {
			BinaryNode temp = this.left;
			if (temp != null) {
				this.left = temp.right;
			}
			temp.right = this;
			this.height = this.height - 1;
			temp.height = temp.height + 1;
			rotationCount++;
			return temp;
		}

		/**
		 * Performs a single left rotation to balance tree
		 * 
		 * @return BinaryNode
		 */
		private BinaryNode SingleLeftRotation() {
			BinaryNode temp = this.right;
			if (temp != null) {
				this.right = temp.left;
			}
			temp.left = this;
			this.height = this.height - 1;
			temp.height = temp.height + 1;
			rotationCount++;
			return temp;
		}

		/**
		 * Calculates the height of the current node, runs in constant time O(1) by finding the height
		 * of the left and right node and using the maximum height of the two nodes + 1
		 * 
		 * @return int
		 */
		private int getHeight() {
			int leftHeight = (this.left != null) ? this.left.height : -1;
			int rightHeight = (this.right != null) ? this.right.height : -1;

			return Math.max(leftHeight, rightHeight) + 1;
		}

		/**
		 * Recursively checks tree until it either finds a node with the
		 * same data as obj or reaches a null node
		 * 
		 * @param obj
		 * @return boolean
		 */
		public boolean contains(T obj) {
			if (this.data.compareTo(obj) == 0) {
				return true;
			}
			if (this.data.compareTo(obj) > 0) {
				if (this.left == null) {
					return false;
				}
				return this.left.contains(obj);
			}
			if (this.data.compareTo(obj) < 0) {
				if (this.right == null) {
					return false;
				}
				return this.right.contains(obj);
			}
			return false;
		}
	}

	public class iterator implements Iterator<T> {
		private Stack<BinaryNode> storage = new Stack<BinaryNode>();
		private BinaryNode temp = new BinaryNode();
		private boolean nextCalled = false;
		private boolean removeCalled = false;

		/**
		 * Constructs an iterator and pushes the root on the stack 
		 */
		public iterator() {
			if (root != null) {
				storage.push(root);
			}
		}

		/* 
		 * Checks to see if storage stack is empty
		 */
		
		@Override
		public boolean hasNext() {
			return (!this.storage.isEmpty());
		}

		/* 
		 * Calls the next element in a pre-order traversal
		 */
		
		@Override
		public T next() {
			if (AVLTree.this.altered) {
				throw new ConcurrentModificationException();
			}
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			this.temp = this.storage.pop();
			if (this.temp.right != null) {
				this.storage.push(this.temp.right);
			}
			if (this.temp.left != null) {
				this.storage.push(this.temp.left);
			}

			this.nextCalled = true;
			this.removeCalled = false;
			return this.temp.data;
		}
	}

	public int height() {
		if(root == null){
			return -1;
		}
		return root.height;
	}

	public int size() {
		return this.size;
	}
}