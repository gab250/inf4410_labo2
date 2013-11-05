package inf4410_labo2;

import java.util.HashMap;
import java.util.Map;

public class WorkUnit implements Runnable 
{
	private ServerNode parent_;
	private String[] workLoad_;

	public WorkUnit(ServerNode parent, String[] workLoad)
	{
		parent_ = parent;
		workLoad_ = workLoad;
	}
		
	@Override
	public void run() 
	{
		/*String stringWorkLoad = new String(workLoad_);
		stringWorkLoad = stringWorkLoad.replace('\n', ' ');
		stringWorkLoad = stringWorkLoad.replace('\r', ' ');
		String[] words = stringWorkLoad.split(" ");*/
		
		Map<String,Integer> result = new HashMap<String,Integer>();
		
		for(int i=0; i<workLoad_.length; ++i)
		{
			workLoad_[i] = workLoad_[i].trim();
		
			if(workLoad_[i].length()> 0 && workLoad_[i].charAt(workLoad_[i].length()-1) == '"')
			{
				workLoad_[i] = workLoad_[i].substring(0,(workLoad_[i].length()-1));
			}
		
			if(workLoad_[i].length()> 0 && workLoad_[i].charAt(0) == '"')
			{
				workLoad_[i] = workLoad_[i].substring(1,workLoad_[i].length());
			}
			
			if(workLoad_[i].length()> 0 &&  
			   (workLoad_[i].charAt(workLoad_[i].length()-1) == '!' || 
			    workLoad_[i].charAt(workLoad_[i].length()-1) == '?' || 
			    workLoad_[i].charAt(workLoad_[i].length()-1) == '.'	||
			    workLoad_[i].charAt(workLoad_[i].length()-1) == ',' || 
			    workLoad_[i].charAt(workLoad_[i].length()-1) == ';'))
			{
				workLoad_[i] = workLoad_[i].substring(0,(workLoad_[i].length()-1));
			}		
			
			if(result.containsKey(workLoad_[i]))
			{
				int oldValue = result.get(workLoad_[i]);
				int newValue = ++oldValue;
				
				result.put(workLoad_[i], newValue);
			}
			else
			{
				result.put(workLoad_[i], 1);
			}
		}
		
		System.out.println("Work Unit : Reporting work");
		
		parent_.Report(result);
	}		
	
}
