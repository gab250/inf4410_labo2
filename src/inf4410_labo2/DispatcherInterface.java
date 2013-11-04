package inf4410_labo2;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface DispatcherInterface extends Remote
{
	public int Register(String hostAdress, String workerName) throws RemoteException;
	public void Report(int worker,Map<String,Integer> result) throws RemoteException;
	public Map<String,Integer> Process(String workLoad) throws RemoteException;
}
