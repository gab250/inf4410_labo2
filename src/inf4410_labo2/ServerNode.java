package inf4410_labo2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Random;

public class ServerNode implements ServerNodeInterface {

	private DispatcherInterface dispatcher_;
	private String name_;
	private int id_;
	private int capacity_;
	private float calculationFailureRate_;
	
	public static void main(String[] args)
	{
		ServerNode serverNode = new ServerNode(args[0],args[1],Integer.valueOf(args[2]),Float.valueOf(args[3]));
		
		try 
		{
			serverNode.run();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public ServerNode(String hostName, String name, int capacity,float failureRate)
	{
		name_ = name;
				
		dispatcher_ = loadDispatcherStub(hostName);
		capacity_ = capacity;
		calculationFailureRate_ = failureRate;
    }
	
	public void Report(Map<String,Integer> result)
	{
		try 
		{
			dispatcher_.Report(id_, result);
		} 
		catch (RemoteException e) {
			
			System.err.println("Error : " + e.getMessage());
		}
	}
	
	private void run() throws Exception
	{
		if(System.getSecurityManager() ==  null)
		{
			System.setSecurityManager(new SecurityManager());
		}
		
		//Register current Node to RMI registry
		try
		{
			ServerNodeInterface stub = (ServerNodeInterface) UnicastRemoteObject.exportObject(this, 5002);
			Registry registry = LocateRegistry.getRegistry(5001);
			registry.rebind(name_, stub);
		}
		catch(ConnectException e)
		{
			System.err.println("Impossible to connect to registry from ServerNovde");
			System.err.println("Erreur : " + e.getMessage());
		}
		catch(Exception e)
		{
			System.err.println("Erreur: " + e.getMessage());
		}
		
		//Register current Node to dispatcher
		id_ = dispatcher_.Register(InetAddress.getLocalHost().getHostName(), name_);
		
		//Register failed
		if(id_ <= 0)
		{
			System.err.println("Couldn't register, closing node...");
			System.exit(-1);
		}
		
	}
		
	@Override
	public int Process(byte[] workLoad) throws RemoteException 
	{
		int result;
		
		if(!IsFailing(workLoad.length))
		{
	    	WorkUnit workUnit = new WorkUnit(this,workLoad);
			Thread workingThread = new Thread(workUnit);
			workingThread.start();
			
			System.out.println("Server Node : Work Started ( " + Integer.toString(workLoad.length) + " B)");
			
			result=0;
		}
		else
		{
			System.out.println("Work failed");
			
			result=-1;
		}
				
		return result;
	}

	@Override
	public boolean IsAlive() throws RemoteException 
	{
		return true;
	}
	
	private DispatcherInterface loadDispatcherStub(String hostname)
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
		
		return stub;
	}
	
	private boolean IsFailing(int size)
	{
		Random rand = new Random();
		int random = rand.nextInt();
		boolean result;
		
		if(size > capacity_)
		{
			int percentage = ((size - capacity_)/(9*capacity_))*100;
			
			if(random%100<=percentage)
			{
				result = true;
			}
			else
			{
				result = false;
			}
		}
		else
		{
			result = false;
		}
		
		return result;

	}

}
