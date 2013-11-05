package inf4410_labo2;

import java.util.Arrays;

public class FileDataContainer 
{
	//private byte[] data_;
	private String[] data_;
	private int currentPosition_;
	private float pourcentageConfirmed_;
	
	public FileDataContainer(String[] data)
	{
		data_ = data;
		currentPosition_ = 0;
		pourcentageConfirmed_= 0;
	}

	public String[] GetDataPortion(float percentage)
	{
		String[] dataCopy; 
		
		
		if((percentage + pourcentageConfirmed_) < 1)
		{
		  dataCopy = Arrays.copyOfRange(data_,currentPosition_, currentPosition_ + (int)(data_.length*percentage));
		}
		else
		{
			dataCopy = Arrays.copyOfRange(data_,currentPosition_, data_.length);
		}
		
		return dataCopy;
    }
	
	public Interval Confirm(float percentage)
	{
		Interval interval;
		
		pourcentageConfirmed_ += percentage;
		
		if(pourcentageConfirmed_ >= 1)
		{
			currentPosition_ = data_.length;
			interval = new Interval(currentPosition_, currentPosition_ + (int)(data_.length*percentage));
		}
		else
		{
			currentPosition_ += (int)(data_.length*percentage);
			interval = new Interval(currentPosition_, data_.length);
		}
		
		return interval;
	}
	
	public int DataLeft()
	{
		return (data_.length - currentPosition_);
	}
	
	public float getPourcentageLeft()
	{
		return (1-pourcentageConfirmed_);
	}
	
	public String[] getData(Interval interval)
	{
		return Arrays.copyOfRange(data_,interval.getLowerBound(), interval.getUpperBound());
	}
}
