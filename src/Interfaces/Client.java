package Interfaces;

import Classes.TripInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    boolean receiveOffer(TripInfo trip, double newValue) throws RemoteException;
    void notification(TripInfo trip) throws RemoteException;
    boolean proposal(Remote client, TripInfo trip) throws RemoteException;
}
