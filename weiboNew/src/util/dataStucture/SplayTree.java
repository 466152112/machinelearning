/**
 * 
 */
package util.dataStucture;

/**   
 *    
 * @progject_name��weiboNew   
 * @class_name��dfs   
 * @class_describe��   
 * @creator��zhouge   
 * @create_time��2014��11��4�� ����12:44:06   
 * @modifier��zhouge   
 * @modified_time��2014��11��4�� ����12:44:06   
 * @modified_note��   
 * @version    
 *    
 */
/**
 * Java ����: ��չ��
 *
 * @author skywang
 * @date 2014/02/03
 */

public class SplayTree<T extends Comparable<T>> {

    private SplayTreeNode<T> mRoot;    // �����

    public class SplayTreeNode<T extends Comparable<T>> {
        T key;                // �ؼ���(��ֵ)
        SplayTreeNode<T> left;    // ����
        SplayTreeNode<T> right;    // �Һ���

        public SplayTreeNode() {
            this.left = null;
            this.right = null;
        }

        public SplayTreeNode(T key, SplayTreeNode<T> left, SplayTreeNode<T> right) {
            this.key = key;
            this.left = left;
            this.right = right;
        }
    }

    public SplayTree() {
        mRoot=null;
    }

    /*
     * ǰ�����"��չ��"
     */
    private void preOrder(SplayTreeNode<T> tree) {
        if(tree != null) {
            System.out.print(tree.key+" ");
            preOrder(tree.left);
            preOrder(tree.right);
        }
    }

    public void preOrder() {
        preOrder(mRoot);
    }

    /*
     * �������"��չ��"
     */
    private void inOrder(SplayTreeNode<T> tree) {
        if(tree != null) {
            inOrder(tree.left);
            System.out.print(tree.key+" ");
            inOrder(tree.right);
        }
    }

    public void inOrder() {
        inOrder(mRoot);
    }


    /*
     * �������"��չ��"
     */
    private void postOrder(SplayTreeNode<T> tree) {
        if(tree != null)
        {
            postOrder(tree.left);
            postOrder(tree.right);
            System.out.print(tree.key+" ");
        }
    }

    public void postOrder() {
        postOrder(mRoot);
    }


    /*
     * (�ݹ�ʵ��)����"��չ��x"�м�ֵΪkey�Ľڵ�
     */
    private SplayTreeNode<T> search(SplayTreeNode<T> x, T key) {
        if (x==null)
            return x;

        int cmp = key.compareTo(x.key);
        if (cmp < 0)
            return search(x.left, key);
        else if (cmp > 0)
            return search(x.right, key);
        else
            return x;
    }

    public SplayTreeNode<T> search(T key) {
        return search(mRoot, key);
    }

    /*
     * (�ǵݹ�ʵ��)����"��չ��x"�м�ֵΪkey�Ľڵ�
     */
    private SplayTreeNode<T> iterativeSearch(SplayTreeNode<T> x, T key) {
        while (x!=null) {
            int cmp = key.compareTo(x.key);

            if (cmp < 0) 
                x = x.left;
            else if (cmp > 0) 
                x = x.right;
            else
                return x;
        }

        return x;
    }

    public SplayTreeNode<T> iterativeSearch(T key) {
        return iterativeSearch(mRoot, key);
    }

    /* 
     * ������С��㣺����treeΪ��������չ������С��㡣
     */
    private SplayTreeNode<T> minimum(SplayTreeNode<T> tree) {
        if (tree == null)
            return null;

        while(tree.left != null)
            tree = tree.left;
        return tree;
    }

    public T minimum() {
        SplayTreeNode<T> p = minimum(mRoot);
        if (p != null)
            return p.key;

        return null;
    }
     
    /* 
     * ��������㣺����treeΪ��������չ��������㡣
     */
    private SplayTreeNode<T> maximum(SplayTreeNode<T> tree) {
        if (tree == null)
            return null;

        while(tree.right != null)
            tree = tree.right;
        return tree;
    }

    public T maximum() {
        SplayTreeNode<T> p = maximum(mRoot);
        if (p != null)
            return p.key;

        return null;
    }

    /* 
     * ��תkey��Ӧ�Ľڵ�Ϊ���ڵ㣬�����ظ��ڵ㡣
     *
     * ע�⣺
     *   (a)����չ���д���"��ֵΪkey�Ľڵ�"��
     *          ��"��ֵΪkey�Ľڵ�"��תΪ���ڵ㡣
     *   (b)����չ���в�����"��ֵΪkey�Ľڵ�"������key < tree.key��
     *      b-1 "��ֵΪkey�Ľڵ�"��ǰ���ڵ���ڵĻ�����"��ֵΪkey�Ľڵ�"��ǰ���ڵ���תΪ���ڵ㡣
     *      b-2 "��ֵΪkey�Ľڵ�"��ǰ���ڵ���ڵĻ�������ζ�ţ�key�������κμ�ֵ��С����ô��ʱ������С�ڵ���תΪ���ڵ㡣
     *   (c)����չ���в�����"��ֵΪkey�Ľڵ�"������key > tree.key��
     *      c-1 "��ֵΪkey�Ľڵ�"�ĺ�̽ڵ���ڵĻ�����"��ֵΪkey�Ľڵ�"�ĺ�̽ڵ���תΪ���ڵ㡣
     *      c-2 "��ֵΪkey�Ľڵ�"�ĺ�̽ڵ㲻���ڵĻ�������ζ�ţ�key�������κμ�ֵ������ô��ʱ�������ڵ���תΪ���ڵ㡣
     */
    private SplayTreeNode<T> splay(SplayTreeNode<T> tree, T key) {
        if (tree == null) 
            return tree;

        SplayTreeNode<T> N = new SplayTreeNode<T>();
        SplayTreeNode<T> l = N;
        SplayTreeNode<T> r = N;
        SplayTreeNode<T> c;

        for (;;) {

            int cmp = key.compareTo(tree.key);
            if (cmp < 0) {

                if (tree.left == null)
                    break;

                if (key.compareTo(tree.left.key) < 0) {
                    c = tree.left;                           /* rotate right */
                    tree.left = c.right;
                    c.right = tree;
                    tree = c;
                    if (tree.left == null) 
                        break;
                }
                r.left = tree;                               /* link right */
                r = tree;
                tree = tree.left;
            } else if (cmp > 0) {

                if (tree.right == null) 
                    break;

                if (key.compareTo(tree.right.key) > 0) {
                    c = tree.right;                          /* rotate left */
                    tree.right = c.left;
                    c.left = tree;
                    tree = c;
                    if (tree.right == null) 
                        break;
                }

                l.right = tree;                              /* link left */
                l = tree;
                tree = tree.right;
            } else {
                break;
            }
        }

        l.right = tree.left;                                /* assemble */
        r.left = tree.right;
        tree.left = N.right;
        tree.right = N.left;

        return tree;
    }

    public void splay(T key) {
        mRoot = splay(mRoot, key);
    }

    /* 
     * �������뵽��չ���У������ظ��ڵ�
     *
     * ����˵����
     *     tree ��չ����
     *     z ����Ľ��
     */
    private SplayTreeNode<T> insert(SplayTreeNode<T> tree, SplayTreeNode<T> z) {
        int cmp;
        SplayTreeNode<T> y = null;
        SplayTreeNode<T> x = tree;

        // ����z�Ĳ���λ��
        while (x != null) {
            y = x;
            cmp = z.key.compareTo(x.key);
            if (cmp < 0)
                x = x.left;
            else if (cmp > 0)
                x = x.right;
            else {
                System.out.printf("�����������ͬ�ڵ�(%d)!\n", z.key);
                z=null;
                return tree;
            }
        }

        if (y==null)
            tree = z;
        else {
            cmp = z.key.compareTo(y.key);
            if (cmp < 0)
                y.left = z;
            else
                y.right = z;
        }

        return tree;
    }

    public void insert(T key) {
        SplayTreeNode<T> z=new SplayTreeNode<T>(key,null,null);

        // ����½����ʧ�ܣ��򷵻ء�
        if ((z=new SplayTreeNode<T>(key,null,null)) == null)
            return ;

        // ����ڵ�
        mRoot = insert(mRoot, z);
        // ���ڵ�(key)��תΪ���ڵ�
        mRoot = splay(mRoot, key);
    }

    /* 
     * ɾ�����(z)�������ر�ɾ���Ľ��
     *
     * ����˵����
     *     bst ��չ��
     *     z ɾ���Ľ��
     */
    private SplayTreeNode<T> remove(SplayTreeNode<T> tree, T key) {
        SplayTreeNode<T> x;

        if (tree == null) 
            return null;

        // ���Ҽ�ֵΪkey�Ľڵ㣬�Ҳ����Ļ�ֱ�ӷ��ء�
        if (search(tree, key) == null)
            return tree;

        // ��key��Ӧ�Ľڵ���תΪ���ڵ㡣
        tree = splay(tree, key);

        if (tree.left != null) {
            // ��"tree��ǰ���ڵ�"��תΪ���ڵ�
            x = splay(tree.left, key);
            // �Ƴ�tree�ڵ�
            x.right = tree.right;
        }
        else
            x = tree.right;

        tree = null;

        return x;
    }

    public void remove(T key) {
        mRoot = remove(mRoot, key);
    }

    /*
     * ������չ��
     */
    private void destroy(SplayTreeNode<T> tree) {
        if (tree==null)
            return ;

        if (tree.left != null)
            destroy(tree.left);
        if (tree.right != null)
            destroy(tree.right);

        tree=null;
    }

    public void clear() {
        destroy(mRoot);
        mRoot = null;
    }

    /*
     * ��ӡ"��չ��"
     *
     * key        -- �ڵ�ļ�ֵ 
     * direction  --  0����ʾ�ýڵ��Ǹ��ڵ�;
     *               -1����ʾ�ýڵ������ĸ���������;
     *                1����ʾ�ýڵ������ĸ������Һ��ӡ�
     */
    private void print(SplayTreeNode<T> tree, T key, int direction) {

        if(tree != null) {

            if(direction==0)    // tree�Ǹ��ڵ�
                System.out.printf("%2d is root\n", tree.key);
            else                // tree�Ƿ�֧�ڵ�
                System.out.printf("%2d is %2d's %6s child\n", tree.key, key, direction==1?"right" : "left");

            print(tree.left, tree.key, -1);
            print(tree.right,tree.key,  1);
        }
    }

    public void print() {
        if (mRoot != null)
            print(mRoot, mRoot.key, 0);
    }
}
