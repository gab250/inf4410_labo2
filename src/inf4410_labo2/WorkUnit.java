package inf4410_labo2;

import java.util.HashMap;
import java.util.Map;

public class WorkUnit implements Runnable 
{
	private ServerNode parent_;
	private byte[] workLoad_;

	public WorkUnit(ServerNode parent, byte[] workLoad)
	{
		parent_ = parent;
		workLoad_ = workLoad;
	}
		
	@Override
	public void run() 
	{
		String stringWorkLoad = new String(workLoad_);
		stringWorkLoad = stringWorkLoad.replace('\n', ' ');
		stringWorkLoad = stringWorkLoad.replace('\r', ' ');
		String[] words = stringWorkLoad.split(" ");
		
		Map<String,Integer> result = new HashMap<String,Integer>();
		
		for(int i=0; i<words.length; ++i)
		{
			if(words[i].charAt(words[i].length()-1) == '!' || words[i].charAt(words[i].length()-1) == '?' || words[i].charAt(words[i].length()-1) == '.')
			{
				words[i] = words[i].substring(0,(words[i].length()-1));
			}		
			
			if(result.containsKey(words[i]))
			{
				int oldValue = result.get(words[i]);
				int newValue = ++oldValue;
				
				result.put(words[i], newValue);
			}
			else
			{
				result.put(words[i], 1);
			}
		}
		
		parent_.Report(result);
	}
}
