package inf4410_labo2;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Test {

	public static void main(String[] args) 
	{
		String fileName = "text1.txt";
		Charset encoding = Charset.defaultCharset();
		
		byte[] encoded=null;
		
		try 
		{
			encoded = Files.readAllBytes(Paths.get(fileName));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
				
		String stringWorkLoad = new String(encoded);
		stringWorkLoad = stringWorkLoad.replace('\n', ' ');
		stringWorkLoad = stringWorkLoad.replace('\r', ' ');
		String[] words = stringWorkLoad.split(" ");

		Map<String,Integer> result = new HashMap<String,Integer>();
		
		for(int i=0; i<words.length; ++i)
		{
			
			words[i] = words[i].trim();
			
			if(words[i].length()> 0)
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
		}
		
		System.out.println("Hey nIg");
	}

}
