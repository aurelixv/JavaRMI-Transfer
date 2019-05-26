package Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    void chamar(String frase, Remote client) throws RemoteException;
    void offerTrip(byte[] offer, Remote client) throws RemoteException;
    void demandTrip(byte[] demand, Remote client) throws RemoteException;
}
