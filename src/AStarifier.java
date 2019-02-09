import java.util.HashSet;
import java.util.List;

//Class implementing the structures and process for the A* algorithm
public class AStarifier
{
    //Sets for the A* algorithm
    private AStarSet openSet, closedSet;

    public AStarifier()
    {
        openSet = new AStarSet();
        closedSet = new AStarSet();
    }

    //Find all optimal paths for the k best taxis on the graph starting from all startNodes ending on target
    //Returns an AStarNode from which the paths can be traversed in reverse
    public AStarNode AStarify(final Graph graph, final Node startNode, final Node tg, String time)
    {
        //Because the actual target may not be a graph node, find nearest graph node and make that the target
        Node target = graph.findNearest(tg);

        initialize(graph, startNode, target);

        boolean found = false; //Set if the target node is found
        AStarNode ret = null;

        //While there are still nodes in the open set, continue
        while(!openSet.isEmpty())
        {
            AStarNode best = openSet.removeBest();  //remove node with the lowest F
            best = closedSet.update(best); //update the closed set

            //If the node removed is the target
            if (best.equals(target))
            {
                closedSet.clear();
                found = true; //set the flag
                ret = best; //and the current node is set to be returned
            }
            else
            {
                for (Node n: graph.getNeighbors(best))
                {
                    //create AStarNode for each node adjacent to the current one
                    AStarNode an = new AStarNode(n, best.getPathLength() + graph.distance(best, n, time), graph.heuristic(n, target), best);

                    //if the neighbor is in the closed set, update the closed set
                    if(closedSet.contains(an))
                    {
                        closedSet.update(an);
                    }
                    else //else update the open set
                    {
                        openSet.update(an);
                    }
                }
            }

            //Stop searching if the target is found and we've run out of nodes providing the optimal path length
            if (found)
            {
                if (ret.getPathLength() < best.getPathLength()) break;
            }
        }

        if(ret == null)
        {
            System.out.println("Could not find path to target.");
            return new AStarNode(tg, 0.0, 0.0, new HashSet<>());
        }

        //add the initial target node to the path
        ret = new AStarNode(tg, ret.getPathLength() + graph.heuristic(tg, ret), 0.0, ret);

        return ret;
    }


    private void initialize(final Graph graph, final Node startNode, final Node target)
    {
        openSet.clear();
        closedSet.clear();

        //Because the graph doesn't necessarily include the taxis as nodes, find nearest node to each taxi and start from there

        //create AStarNode from the current taxi node
        AStarNode AN = new AStarNode(startNode, 0.0, graph.heuristic(startNode, target), new HashSet<>());
        //find nearest graph node
        Node nearest = graph.findNearest(AN);
        //create AStar node for the nearest graph node, set current taxi AStarNode as the only predecessor
        AStarNode aNearest = new AStarNode(nearest, graph.heuristic(AN, nearest), graph.heuristic(nearest, target), AN);
        //add the the second AStarNode to the open set
        openSet.update(aNearest);
    }
}
