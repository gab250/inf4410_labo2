package inf4410_labo2;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerNodeInterface extends Remote
{
	public int Process(byte[] workLoad) throws RemoteException;
	public boolean IsAlive() throws RemoteException;
}
