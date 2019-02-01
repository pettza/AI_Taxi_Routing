import java.util.List;
import java.util.LinkedList;

//Extension of Node class for use in the A* algorithm

public class AStarNode extends Node implements Comparable<AStarNode>
{
    //List of predecessors in different paths of equal optimal length
    private List<AStarNode> previousNodes;
    private double pathLength, heuristic;


    public AStarNode(Node n, double pl_v, double h_v, AStarNode previous)
    {
        super(n);
        pathLength = pl_v;
        heuristic = h_v;
        previousNodes = new LinkedList<>();
        previousNodes.add(previous);
    }

    @Override
    public int compareTo(final AStarNode n)
    {
        int cmp = Double.compare(F(), n.F());
        if (cmp != 0) return cmp;

        return Long.compare(getId(), n.getId());
    }

    public AStarNode(Node n, double pl_v, double h_v, List<AStarNode> previous)
    {
        super(n);
        pathLength = pl_v;
        heuristic = h_v;
        previousNodes = new LinkedList<>(previous);
    }

    public double F()
    {
        return pathLength + heuristic;
    }

    public double getPathLength()
    {
        return pathLength;
    }

    public void addPrevious(List<AStarNode> n)
    {
        previousNodes.addAll(n);
    }

    public final List<AStarNode> getPreviousNodes()
    {
        return previousNodes;
    }

}
