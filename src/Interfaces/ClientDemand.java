package Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientDemand extends Remote {
    void echo(String frase) throws RemoteException;
}
