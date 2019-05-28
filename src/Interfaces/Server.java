package Interfaces;

import Classes.TripInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Server extends Remote {
    void offerTrip(byte[] offer, Remote client) throws RemoteException;
    ArrayList<TripInfo> demandTrip(byte[] demand, Remote client) throws RemoteException;
    boolean bookOffer(int tripIndex) throws RemoteException;
    boolean makeOffer(int client, TripInfo trip, double value) throws RemoteException;
}
