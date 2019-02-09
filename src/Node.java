import java.lang.Math;


//Base class for storing input information
public class Node
{
    //Coordinates and stuff
    private double x;
    private double y;
    private long id;

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

    }

    public Node(final Node n)
    {
        x = n.x;
        y = n.y;
        id = n.id;
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

    @Override
    public int hashCode()
    {
        return (int) id;
    }
}
