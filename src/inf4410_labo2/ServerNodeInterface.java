package inf4410_labo2;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerNodeInterface extends Remote
{
	public int Process(String[] workLoad, boolean fistJob) throws RemoteException;
	public boolean IsAlive() throws RemoteException;
}
