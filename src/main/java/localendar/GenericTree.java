package localendar;
import java.util.Comparator;
import java.util.function.Consumer;

public class GenericTree<T> {
    protected class TreeNode {
        T data;
        TreeNode left, right;
        int height;

        TreeNode(T data) {
            this.data = data;
            height = 1;
        }
    }

    protected TreeNode root;
    protected Comparator<T> comparator;

    public GenericTree(Comparator<T> comparator) {
        this.comparator = comparator;
        root = null;
    }

    public void insert(T data) {
        root = insertRec(root, data);
    }

    private TreeNode insertRec(TreeNode node, T data) {
        if (node == null) return new TreeNode(data);

        if (comparator.compare(data, node.data) < 0)
            node.left = insertRec(node.left, data);
        else
            node.right = insertRec(node.right, data);

        node.height = 1 + Math.max(height(node.left), height(node.right));
        return balance(node, data);
    }

    private TreeNode balance(TreeNode node, T data) {
        int balance = getBalance(node);

        // LL
        if (balance > 1 && comparator.compare(data, node.left.data) < 0)
            return rightRotate(node);

        // RR
        if (balance < -1 && comparator.compare(data, node.right.data) > 0)
            return leftRotate(node);

        // LR
        if (balance > 1 && comparator.compare(data, node.left.data) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // RL
        if (balance < -1 && comparator.compare(data, node.right.data) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public void delete(T data) {
        root = deleteRec(root, data);
    }

    private TreeNode deleteRec(TreeNode node, T data) {
        if (node == null) return null;

        int cmp = comparator.compare(data, node.data);
        if (cmp < 0) {
            node.left = deleteRec(node.left, data);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, data);
        } else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            TreeNode successor = getSuccessor(node.right);
            node.data = successor.data;
            node.right = deleteRec(node.right, successor.data);
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));
        return balance(node, data);
    }

    private TreeNode getSuccessor(TreeNode node) {
        while (node.left != null)
            node = node.left;
        return node;
    }

    public void inOrder(Consumer<T> action) {
        inOrderRec(root, action);
    }

    private void inOrderRec(TreeNode node, Consumer<T> action) {
        if (node != null) {
            inOrderRec(node.left, action);
            action.accept(node.data);
            inOrderRec(node.right, action);
        }
    }

    private int height(TreeNode node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(TreeNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private TreeNode rightRotate(TreeNode y) {
        TreeNode x = y.left;
        TreeNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private TreeNode leftRotate(TreeNode x) {
        TreeNode y = x.right;
        TreeNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }
}
