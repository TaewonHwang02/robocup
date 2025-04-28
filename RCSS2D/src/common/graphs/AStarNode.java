package common.graphs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class AStarNode<T> implements Comparable<AStarNode<T>>{

    protected T object; // whatever is being graphed

    private AStarNode<T> parent = null;
    private ArrayList<AStarEdge<T>> children;

    // some values needed for A*:
    //      - g : move function, cost of the path up until this point
    //      - h : heuristic function, approximation of distance from this node to the goal
    //      - f : cost function, g + h

    private double f = Double.MAX_VALUE;
    private double g = Double.MAX_VALUE;
    private double h;

    public AStarNode(T object, double h, ArrayList<AStarEdge<T>> children){
        this.object = object;
        this.h = h;
        this.children = children;
    }

    public double getHeuristic() {
        return h;
    }

    private ArrayList<AStarEdge<T>> getChildren() {
        return this.children;
    }

    public static class AStarEdge<T> {

        protected double weight;
        protected AStarNode<T> child;

        public AStarEdge(double weight, AStarNode<T> child){
            this.weight = weight;
            this.child = child;
        }
    }

    public static <T> AStarNode<T> AStar(AStarNode<T> start, AStarNode<T> goal){

        PriorityQueue<AStarNode<T>> closed = new PriorityQueue<>();
        PriorityQueue<AStarNode<T>> open = new PriorityQueue<>();

        if (start.f == Double.MAX_VALUE || start.g == Double.MAX_VALUE){
            start.f = Double.MAX_VALUE;
        }
        else{
            start.f = start.g + goal.getHeuristic();
        }

        open.add(start);

        while (!open.isEmpty()){

            AStarNode<T> head = open.remove();
            if (head == goal){
                // the goal has been found. Its parent information should have been updated in a way that allows us
                //      to retrace our steps back to the start
                return head;
            }

            for (AStarNode.AStarEdge<T> edge : head.getChildren()){

                AStarNode<T> child = edge.child;
                double current_g = head.g + edge.weight;

                if (!open.contains(child) && !closed.contains(child)){
                    // child turns out to be an unexplored node
                    child.parent = head;
                    child.g = current_g;
                    child.f = child.g + child.getHeuristic();
                    open.add(child);
                }
                else if (current_g < child.g){
                    // we found a cheaper path from start to child
                    child.parent = head;
                    child.g = current_g;
                    child.f = child.g + child.getHeuristic();

                    if (closed.contains(child)){
                        closed.remove(child);
                        open.add(child);
                    }
                }

            }

            closed.add(head);
        }

        // if we left the while loop, that means we never found the goal
        return null;
    }



    public static <T> LinkedList<T> getPath(AStarNode<T> goal){

        if (goal == null){
            return null;
        }

        AStarNode<T> iter = goal;
        LinkedList<T> path = new LinkedList<>();

        while (iter.parent != null){
            path.addFirst(iter.object);
            iter = iter.parent;
        }
        path.addFirst(iter.object);

        return path;
    }



    @Override
    public int compareTo(AStarNode<T> node) {
        return Double.compare(this.f, node.f);
    }
}
