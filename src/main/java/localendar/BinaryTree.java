package localendar;

import java.util.Comparator;

public class BinaryTree extends GenericTree<Task> {
    public BinaryTree() {
        super(new TaskComparator());
    }

    public void setForComparator(Comparator<Task> newComparator){
        this.comparator = newComparator;
    }

    @Override
    public void insert(Task data) {
        super.insert(data);
    }

}
