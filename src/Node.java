import org.jetbrains.annotations.NotNull;

import java.lang.Math;


//Base class for storing input information
public class Node
{
    //Coordinates and stuff
    private double x;
    private double y;
    private long id;
    private String name;

    //For printing purposes
    @Override
    public String toString() {
        return x + "," + y + "," + "0";
    }

    public Node(double x_v, double y_v, long id_v)
    {
        x = x_v;
        y = y_v;
        id = id_v;
        name = new String();
    }

    public Node(double x_v, double y_v, long id_v, String name_v)
    {
        x = x_v;
        y = y_v;
        id = id_v;
        name = name_v;
    }

    public Node(@NotNull final Node n)
    {
        x = n.x;
        y = n.y;
        id = n.id;
        name = n.name;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public long getId()
    {
        return id;
    }

    public String getName() { return name; }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
        {
            return false;
        }

        if (!Node.class.isAssignableFrom(obj.getClass()))
        {
            return false;
        }

        final Node n = (Node) obj;
        return id == n.id;
    }

    //Euclidian distance
    public double distanceFrom(final Node n)
    {
        double dx = x - n.x;
        double dy = y - n.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public int hashCode()
    {
        return (int) id;
    }
}
