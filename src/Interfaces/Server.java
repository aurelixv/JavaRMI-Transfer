package Interfaces;

import Classes.TripInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Server extends Remote {
    void chamar(String frase, Remote client) throws RemoteException;
    void offerTrip(byte[] offer, Remote client) throws RemoteException;
    void demandTrip(byte[] demand, Remote client) throws RemoteException;
    ArrayList<TripInfo> searchOffers(int itinerario, int numPassageiros, double preco) throws RemoteException;
    ArrayList<TripInfo> searchDemands(int itinerario, int numPassageiros, double preco) throws RemoteException;
    int bookOffer(int tripIndex) throws RemoteException;
}
