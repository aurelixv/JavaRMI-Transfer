package Interfaces;

import Classes.TripInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    void echo(String frase) throws RemoteException;
    boolean receiveOffer(TripInfo trip) throws RemoteException;
    void notification(TripInfo trip) throws RemoteException;
}
