//Node with a rating field
public class TaxiNode extends Node implements Comparable<TaxiNode>
{
    private double rating;
    private String type;

    public TaxiNode(double x_v, double y_v, long id_v, double rating_v, String type_v)
    {
        super(x_v,y_v,id_v);
        rating = rating_v;
        type = type_v;
    }

    @Override
    public int compareTo(final TaxiNode n)
    {
        int cmp = -Double.compare(score(), n.score());
        if (cmp != 0) return cmp;

        return Long.compare(getId(), n.getId());
    }

    public double getRating() { return rating; }

    //Score = rating + typeScore
    public double score()
    {
        double typeScore = 0.0;
        if(type.equals("subcompact"))
        {
            typeScore = 0.0;
        }
        else if(type.equals("compact"))
        {
            typeScore = 0.5;
        }
        else if(type.equals("large"))
        {
            typeScore = 1.0;
        }
        return rating + typeScore;
    }
}
