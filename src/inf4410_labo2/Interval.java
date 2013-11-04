package inf4410_labo2;

public class Interval 
{
	private int lowerBound_;
	private int upperBound_;
	
	public Interval(int lowerB, int upperB)
	{
		lowerBound_ = lowerB;
		upperBound_ = upperB;
	}
	
	public int getUpperBound()
	{
		return upperBound_;
	}
	
	public int getLowerBound()
	{
		return lowerBound_;
	}
}
