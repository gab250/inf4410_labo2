package inf4410_labo2;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Map.Entry;

public class Client 
{
	private DispatcherInterface dispatcher_;
	
	
	public static void main(String[] args)
	{
		
		Client client = new Client();
		client.loadDispatcherStub(args[0]);
		
		if(args[1].equals("-p"))
		{
			String fileName = args[2];
			Charset encoding = Charset.defaultCharset();
			String data=null;
			
			byte[] encoded;
			
			try 
			{
				encoded = Files.readAllBytes(Paths.get(args[2]));
				data = encoding.decode(ByteBuffer.wrap(encoded)).toString();
			} 
			catch (IOException e) 
			{
				System.out.println("Error : " + e.getMessage());
			}
			
			if(data!=null)
			{
				
				String result = client.Process(data);
				
				System.out.println("Result : ");
				System.out.println(result);

			}
			
		}
		
	}
	
	public String Process(String workLoad)
	{
		Map<String,Integer> result=null;
		String formatedResult="";
		
		try 
		{
			result = dispatcher_.Process(workLoad);
			
		} 
		catch (RemoteException e) 
		{
			System.err.println("Error in dispatcher : " + e.getMessage());
		}
		
		for(Entry<String, Integer> entry : result.entrySet())
		{
			formatedResult += entry.getKey() + "  " + Integer.toString(entry.getValue()) + System.getProperty("line.separator");;
		}
		
		return formatedResult;
	}
	
	private void loadDispatcherStub(String hostname)
	{
		DispatcherInterface stub = null;
		
		try 
		{
			Registry registry = LocateRegistry.getRegistry(hostname,5001);
			stub = (DispatcherInterface) registry.lookup("dispatcher");
			
		} 
		catch (RemoteException e) 
		{
			
		} 
		catch (NotBoundException e) 
		{
			
		}
		
		dispatcher_= stub;
	}
}
