package localendar;

import java.util.function.Consumer;

class Node{
    Task task;
    Node left,right;
    Node(Task task){
        this.task = task;
        left=right=null;
    }
}

public class BinaryTree{
    private Node root;
    private TaskComparator comparator;
    public BinaryTree(){
        comparator = new TaskComparator();
        root=null;
    }
    public void insert(Task task){
        root = insertRec(root,task);
    }
    private Node insertRec(Node node, Task task){
        if(node==null){
            return new Node(task);
        }
        if(comparator.compare(task,node.task) < 0){
            node.left = insertRec(node.left,task);
        }else{
            node.right = insertRec(node.right,task);
        }
        return node;
    }
    public void inOrder(Consumer<Task> action) { // https://docs.oracle.com/javase/8/docs/api/java/util/function/Consumer.html
        inOrderRecursive(root, action);
    }
    private void inOrderRecursive(Node node, Consumer<Task> action){
        if(node!=null){
            inOrderRecursive(node.left, action);
            System.out.println("Printed");
            action.accept(node.task);
            inOrderRecursive(node.right, action);
        }
    }
    public void delete(Task task){
        root = deleteRecursive(root,task);
    }
    private Node deleteRecursive(Node node, Task task){
        if(node==null){
            return node;
        }
        if(comparator.compare(node.task,task)==1){
            node.left = deleteRecursive(node.left,task);
        }else if(comparator.compare(node.task,task)==-1){
            node.right = deleteRecursive(node.right,task);
        }else{
            if(node.left == null){
                return node.right;
            }
            if(node.right == null){
                return node.left;
            }
            Node successor = getSuccessor(node);
            node.task = successor.task;
            node.right = deleteRecursive(node.right, successor.task);
        }
        return node;
    }
    private Node getSuccessor(Node curr){
        curr = curr.right;
        while(curr!=null && curr.left!=null){
            curr = curr.left;
        }
        return curr;
    }
}
