package inf4410_labo2;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Dispatcher implements DispatcherInterface {
	
	static private Map<Integer,Map<String,Integer>> results_;
	static private Lock lock_;
	
	private Map<Integer,ServerNodeInterface> Workers_; 
	private int[][] statistics_;
		
	public static void main(String[] args) throws Exception
	{
		Dispatcher dispatcher = new Dispatcher();
		dispatcher.run();
	}	
	
	public Dispatcher()
	{
		Workers_ = new HashMap<Integer,ServerNodeInterface>();
		results_ = new HashMap<Integer,Map<String,Integer>>();
		lock_ = new ReentrantLock();
		
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
		if(newWorker != null && !Workers_.containsValue(newWorker))
		{
			ClearDeadWorkers();
			
			//Check for dead spots
			if(Workers_.containsValue(null))
			{
				for(Entry<Integer,ServerNodeInterface> entry : Workers_.entrySet())
				{
					if(entry.getValue() == null)
					{
						newId = entry.getKey();
												
						break;
					}
				}
			}
			else
			{
				newId = Workers_.size() + 1;
			}	
					
			Workers_.put(newId, newWorker);
			
			System.out.println("Registered : " + workerName + "@" + hostAdress);
			System.out.println("Number of workers : " + Integer.toString(Workers_.size()));
		
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
		PutResult(worker,result);
	}

	@Override
	public Map<String,Integer> Process(String workLoad) throws RemoteException 
	{
		byte[] workLoadInByte = workLoad.getBytes();
		String result=null;
						
		//Clear results
		results_ = new HashMap<Integer,Map<String,Integer>>();
		
		//Dispatch to workers
		
		//Check livelyness of workers
		Vector<Integer> aliveWorkers = new Vector<Integer>();
		
		ClearDeadWorkers();
		aliveWorkers = GetWorkerAlive();
				
		//Dispatch work
		if(aliveWorkers.size() > 0)
		{
			int sizeOfWorkload = workLoadInByte.length/aliveWorkers.size();
						
			for(int i=0; i<aliveWorkers.size(); ++i)
			{
				if(i != aliveWorkers.size() -1 )
				{
					Workers_.get(aliveWorkers.get(i)).Process(Arrays.copyOfRange(workLoadInByte, i*sizeOfWorkload, i*sizeOfWorkload + (sizeOfWorkload-1)));
				}
				else
				{	
					Workers_.get(aliveWorkers.get(i)).Process(Arrays.copyOfRange(workLoadInByte, i*sizeOfWorkload, workLoadInByte.length-1));
				}
			}
					
		}
		else
		{
			result = "Impossible to process, no workers";
		}
		
		int nbOfWorkers = aliveWorkers.size();
		
		//Wait for results
		while(GetResultSize()<nbOfWorkers)
		{
			try 
			{
				Thread.sleep(1);
				
			} catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		
		//Merge results
		Map<String,Integer> combinedResults = new HashMap<String,Integer>();
		
		for(int i=1; i<=results_.size(); ++i)
		{
			for(Entry<String, Integer> entry : results_.get(i).entrySet())
			{
				if(combinedResults.containsKey(entry.getKey()))
				{
					int oldValue = combinedResults.get(entry.getKey());
					int newValue = oldValue + results_.get(i).get(entry.getKey());
				
					combinedResults.put(entry.getKey(), newValue);
				}
				else
				{
					combinedResults.put(entry.getKey(), entry.getValue());
				}
			}
		}
				
		return combinedResults;
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
	
	private int GetResultSize()
	{
		int size=0;
		lock_.lock();
		
		try
		{
			size = results_.size();			
		}
		finally
		{
			lock_.unlock();
		}
		
		return size;
	}

	private void PutResult(int workerID, Map<String,Integer> result)
	{
		lock_.lock();
		
		try
		{
			results_.put(workerID, result);
		}
		finally
		{
			lock_.unlock();
		}
		
	}

	private Map<String,Integer> GetResult(int workerID)
	{
		Map<String,Integer> result;
		lock_.lock();
		
		try
		{
			result = results_.get(workerID);			
		}
		finally
		{
			lock_.unlock();
		}
		
		return result;
	}

	private void ClearDeadWorkers()
	{
		for(int i=1; i<=Workers_.size(); ++i)
		{
			try
			{
				if(Workers_.get(i) != null)
				{
					Workers_.get(i).IsAlive();
				}				
			}
			catch(RemoteException e)
			{
				Workers_.put(i, null);
				continue;
			}
		}
	}

	private Vector<Integer> GetWorkerAlive()
	{
		Vector<Integer> result = new Vector<Integer>();

		for(int i=1; i<=Workers_.size(); ++i)
		{
				if(Workers_.get(i) != null)
				{
					result.add(i);
				}				
		}
		
		return result;
	}
}
