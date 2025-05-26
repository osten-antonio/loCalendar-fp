package localendar;
import javafx.scene.Node;

import java.util.Comparator;

public class CalendarBinaryTree extends GenericTree<Node> {
    int size=0;
    public CalendarBinaryTree(Comparator<Node> comparator) {
        super(comparator);
        size=0;
    }

    @Override
    public void insert(Node data) {
        super.insert(data);
        size+=1;
    }

    @Override
    public void delete(Node data) {
        super.delete(data);
        size-=1;
    }

    public int size(){
        return size;
    }

    public CalendarBinaryTree getLastN(int n){
        CalendarBinaryTree res = new CalendarBinaryTree(comparator);
        getLastNRec(n,new int[]{n},root, res);
        return res;
    }
    public void getLastNRec(int n, int[] m, TreeNode node, CalendarBinaryTree res){
        // need to use array because m-- modifies a copy, so it doesnt persist in each recursive call
        if(node == null){
            return;
        }
        if(node.right == null && node.left != null && n==m[0]){
            res.insert(node.left.data);
            m[0]--;
        }
        if(node.right != null){
            getLastNRec(n,m, node.right, res);
        }
        if(m[0]<=n && m[0]>0){
            res.insert(node.data);
            m[0]--;
        }
    }

}
