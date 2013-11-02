package inf4410_labo2;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher implements DispatcherInterface {
	
	static private Map<Integer,Map<String,Integer>> results_;
	
	private Map<Integer,ServerNodeInterface> activeWorkers_; 
	private int[][] statistics_;
	
	public static void main(String[] args) throws Exception
	{
		Dispatcher dispatcher = new Dispatcher();
		dispatcher.run();
	}	
	
	public Dispatcher()
	{
		activeWorkers_ = new HashMap<Integer,ServerNodeInterface>();
	}
	
	@Override
	public int Register(String hostAdress, String workerName) throws RemoteException 
	{
		ServerNodeInterface newWorker=null;
		int newId=0;
		
		//Import ServerNodeInterface Object
		try 
		{
			newWorker = loadServerNodeStub(hostAdress,workerName);
		} 
		catch (NotBoundException e) 
		{
			System.err.println("Error in register (Dispatcher) : " + e.getMessage()); 

		}
		catch(RemoteException e)
		{
			System.err.println("Error in register (Dispatcher) : " + e.getMessage()); 
		}
		
		//Save newWorker to active Worker map
		if(newWorker != null && !activeWorkers_.containsValue(newWorker))
		{
			
			newId = activeWorkers_.size() + 1;
			activeWorkers_.put(newId, newWorker);
							
			System.out.println("Registered : " + workerName + "@" + hostAdress);
			System.out.println("Number of workers : " + Integer.toString(activeWorkers_.size()));
		
		}
		else
		{
			System.out.println("Couldn't register : " + workerName + "@" + hostAdress);
		}
		
		//Return NewWorker Id 
		if(newId >0)
			return newId;
		else
			return -1;
	}

	@Override
	public void Report(int worker, Map<String, Integer> result) throws RemoteException 
	{
		
	}

	@Override
	public String Process(String[] workLoad) throws RemoteException 
	{
		return null;
	}
	
	private void run() throws Exception
	{
		if(System.getSecurityManager() ==  null)
		{
			System.setSecurityManager(new SecurityManager());
		}

		try
		{
			DispatcherInterface stub = (DispatcherInterface) UnicastRemoteObject.exportObject(this, 5002);
			Registry registry = LocateRegistry.getRegistry(5001);
			registry.rebind("dispatcher", stub);
			
			System.out.println("Dispatcher ready");
		}
		catch(ConnectException e)
		{
			System.err.println("Impossible to connect to registry from Dispatcher");
			System.err.println("Erreur : " + e.getMessage());
		}
		catch(Exception e)
		{
			System.err.println("Erreur: " + e.getMessage());
		}
	}

	private ServerNodeInterface loadServerNodeStub(String hostname, String serverNodeName) throws RemoteException,NotBoundException
	{
		ServerNodeInterface stub = null;

		Registry registry = LocateRegistry.getRegistry(hostname,5001);
		stub = (ServerNodeInterface) registry.lookup(serverNodeName);

		return stub;
	}
	
}
