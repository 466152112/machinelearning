package bean;


public class AvlTree<T extends Comparable> {
	public static class AvlNode<T> {// avl树节�?

		AvlNode(T theElement) {
			this(theElement, null, null);
		}

		AvlNode(T theElement, AvlNode<T> lt, AvlNode<T> rt) {
			element = theElement;
			left = lt;
			right = rt;
			height = 0;
		}

		public T element; // 节点中的数据
		public AvlNode<T> left; // 左儿�?
		public AvlNode<T> right; // 右儿�?
		public int height; // 节点的高�?
		/**
		 * @return the element
		 */
		public T getElement() {
			return element;
		}

		/**
		 * @param element the element to set
		 */
		public void setElement(T element) {
			this.element = element;
		}

		/**
		 * @return the left
		 */
		public AvlNode<T> getLeft() {
			return left;
		}

		/**
		 * @param left the left to set
		 */
		public void setLeft(AvlNode<T> left) {
			this.left = left;
		}

		/**
		 * @return the right
		 */
		public AvlNode<T> getRight() {
			return right;
		}

		/**
		 * @param right the right to set
		 */
		public void setRight(AvlNode<T> right) {
			this.right = right;
		}

		/**
		 * @return the height
		 */
		public int getHeight() {
			return height;
		}

		/**
		 * @param height the height to set
		 */
		public void setHeight(int height) {
			this.height = height;
		}
		
	}

	public AvlNode<T> root;// avl树根

	public AvlTree() {
		root = null;
	}

	// 在avl树中插入数据，重复数据复�?
	public void insert(T x) {
		root = insert(x,root);
	}
	
//	//中序遍历avl�?
//    public void printTree( AvlNode< T> t )
//    {
//        if( t != null )
//        {
//            printTree( t.left );
//            System.out.println( t.element+","+t.extras);
//            printTree( t.right );
//        }
//    }
//    
    
	// 搜索
	public boolean contains(T x) {
		return contains(x, root);
	}

	public void makeEmpty() {
		root = null;
	}

	public boolean isEmpty() {
		return root == null;
	}

	private AvlNode<T> insert(T x, AvlNode<T> t) {
		if (t == null)
			return new AvlNode<T>(x, null, null);

		int compareResult = x.compareTo(t.element);

		if (compareResult < 0) {
			t.left = insert(x,t.left);// 将x插入左子树中
			if (height(t.left) - height(t.right) == 2)// 打破平衡
				if (x.compareTo(t.left.element) < 0)// LL型（左左型）
					t = rotateWithLeftChild(t);
				else
					// LR型（左右型）
					t = doubleWithLeftChild(t);
		} else if (compareResult > 0) {
			t.right = insert(x,t.right);// 将x插入右子树中
			if (height(t.right) - height(t.left) == 2)// 打破平衡
				if (x.compareTo(t.right.element) > 0)// RR型（右右型）
					t = rotateWithRightChild(t);
				else
					// RL�?
					t = doubleWithRightChild(t);
		} else
			; // 重复数据，什么也不做
		t.height = Math.max(height(t.left), height(t.right)) + 1;// 更新高度
		return t;
	}

	// 搜索（查找）
	private boolean contains(T x, AvlNode t) {
		while (t != null) {
			int compareResult = x.compareTo((T) t.element);

			if (compareResult < 0)
				t = t.left;
			else if (compareResult > 0)
				t = t.right;
			else
				return true; // Match
		}
		return false; // No match
	}

	// 求高�?
	private int height(AvlNode<T> t) {
		return t == null ? -1 : t.height;
	}
	
	public T getElement(T x, AvlNode t){
		while (t != null) {
			int compareResult = x.compareTo( t.element);

			if (compareResult < 0)
				t = t.left;
			else if (compareResult > 0)
				t = t.right;
			else
				return (T) t.element; // Match
		}
		return null; // No match
	}
	public T getElement(T x){
		return getElement(x,this.root);
	}
	// 带左子树旋转,适用于LL�?
	private AvlNode<T> rotateWithLeftChild(AvlNode<T> k2) {
		AvlNode<T> k1 = k2.left;
		k2.left = k1.right;
		k1.right = k2;
		k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
		k1.height = Math.max(height(k1.left), k2.height) + 1;
		return k1;
	}

	// 带右子树旋转，�?用于RR�?
	private AvlNode<T> rotateWithRightChild(AvlNode<T> k1) {
		AvlNode<T> k2 = k1.right;
		k1.right = k2.left;
		k2.left = k1;
		k1.height = Math.max(height(k1.left), height(k1.right)) + 1;
		k2.height = Math.max(height(k2.right), k1.height) + 1;
		return k2;
	}

	// 双旋转，适用于LR�?
	private AvlNode<T> doubleWithLeftChild(AvlNode<T> k3) {
		k3.left = rotateWithRightChild(k3.left);
		return rotateWithLeftChild(k3);
	}

	// 双旋�?适用于RL�?
	private AvlNode<T> doubleWithRightChild(AvlNode<T> k1) {
		k1.right = rotateWithLeftChild(k1.right);
		return rotateWithRightChild(k1);
	}
	
}
