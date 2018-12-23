import java.util.*;

//Class to be used for the Open and Closed sets of the A* algorithm
public class AStarSet
{
    //Assisting structure for efficient search
    private Map<Node, AStarNode> map;
    //The priority queue where nodes are sorted by F
    private SortedSet<AStarNode> set;

    public AStarSet()
    {
        map = new HashMap<>();
        set = new TreeSet<>();
    }

    //Update info about AStarNode n (insert if it doesn't exist) and return the updated node
    public AStarNode update(AStarNode n)
    {
        //If n is not already in the set, add it
        if(!map.containsKey(n))
        {
            map.put(n, n);
            set.add(n);
            return n; //return newly added node
        }

        //If it is, get the previous instance
        AStarNode previousInstance = map.get(n);

        //If the length of the path arriving at n in this instance is the same as before
        if (previousInstance.getPathLength() == n.getPathLength())
        {
            //The alternative paths leading to n are added to the list
            previousInstance.addPrevious(n.getPreviousNodes());
            return previousInstance; //return reference to the updated instance of the node
        }
        //if the currently found path is shorter
        else if (previousInstance.getPathLength() > n.getPathLength())
        {
            //replace the previous instance with the current one
            map.replace(previousInstance, n);
            set.remove(previousInstance);
            set.add(n);
            return n; //return reference to new instance
        }

        return previousInstance; //if path length of previous instance for some reason in shorter, do nothing and return reference to previous instance
    }

    public void clear()
    {
        map.clear();
        set.clear();
    }

    public boolean contains(final AStarNode n)
    {
        return  map.containsKey(n);
    }
    
    public AStarNode removeBest()
    {
        AStarNode best = set.first();
        set.remove(best);
        map.remove(best);
        return best;
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }
}
