package inf4410_labo2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerNode implements ServerNodeInterface {

	private DispatcherInterface dispatcher_;
	private String name_;
	
	public static void main(String[] args)
	{
		ServerNode serverNode = new ServerNode(args[0]);
		
		try 
		{
			serverNode.run();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public ServerNode(String hostName)
	{
		name_ = "Worker1";
				
		dispatcher_ = loadDispatcherStub(hostName);
		
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
		dispatcher_.Register(InetAddress.getLocalHost().getHostName(), name_);
		
	}
		
	@Override
	public int Process(String[] workLoad) throws RemoteException 
	{

		try 
		{
			dispatcher_.Register(InetAddress.getLocalHost().getHostAddress(), name_);
		} 
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return 0;
	}

	@Override
	public boolean IsAlive() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
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

}
