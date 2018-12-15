import java.util.LinkedList;
import java.util.List;

public class AStarifier
{
    private AStarSet openSet, closedSet;

    public AStarifier()
    {
        openSet = new AStarSet();
        closedSet = new AStarSet();
    }

    public AStarNode AStarify(final Graph graph, final List<Node> startNodes, final Node tg)
    {
        Node target = graph.findNearest(tg);
        initialize(graph, startNodes, target);
        boolean bestFound = false;
        AStarNode ret = null;
        while(!openSet.isEmpty())
        {
            AStarNode best = openSet.removeBest();
            closedSet.update(best);
            if (best.equals(target))
            {
                bestFound = true;
                ret = closedSet.update(best);
            }
            else
            {
                if (best == null) System.out.println("POULO!!!");
                for (Node n: graph.getNeighbors(best))
                {
                    AStarNode an = new AStarNode(n, best.getPathLength() + best.distance_from(n), n.distance_from(target), best);
                    if(closedSet.contains(an))
                    {
                        closedSet.update(an);
                    }
                    else
                    {
                        openSet.update(an);
                    }
                }
            }

            if (bestFound == true)
            {
                if (ret.getPathLength() < best.getPathLength()) break;
            }
        }

        return ret;
    }

    private void initialize(final Graph graph, final List<Node> startNodes, final Node target)
    {
        openSet.clear();
        closedSet.clear();

        for (Node n : startNodes)
        {
            AStarNode an = new AStarNode(n, 0.0, n.distance_from(target), new LinkedList<>());
            Node nearest = graph.findNearest(an);
            AStarNode aNearest = new AStarNode(nearest, an.distance_from(nearest), nearest.distance_from(target), an);
            openSet.update(aNearest);
        }
    }
}
