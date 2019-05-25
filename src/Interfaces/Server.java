package Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    void chamar(String frase, Remote cliente) throws RemoteException;
}
