package Classes;

import Interfaces.Client;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerMain {

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        int port = 6000;
        System.out.println("Inicializando servidor na porta " + port + "...");
        Registry registry = LocateRegistry.createRegistry(port);
        ServerImpl server = new ServerImpl();
        registry.bind("Servidor", server);
        System.out.println("Servidor inicializado.");
    }

}

class ServerImpl extends UnicastRemoteObject implements Interfaces.Server {

    private final ArrayList<TripInfo> offer;
    private final ArrayList<TripInfo> demand;
    private int index;

    ServerImpl() throws RemoteException {
        System.out.println("Criando objeto remoto servidor...");

        offer = new ArrayList<>();
        demand = new ArrayList<>();
        index = 0;
    }

    private synchronized void addTrip(ArrayList<TripInfo> trips, TripInfo trip) {
        trip.setIndex(index++);
        trips.add(trip);
    }

    private ArrayList<TripInfo> getTrips(ArrayList<TripInfo> trips, TripInfo trip) {
        ArrayList<TripInfo> candidates = new ArrayList<>();
        for (TripInfo candidate : trips) {
            if(trip.getTipoVeiculo() == candidate.getTipoVeiculo() &&
                    trip.getNumPassageiros() <= candidate.getNumPassageiros()) {
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    // Metodo para inserir ofertas
    @Override
    public void offerTrip(byte[] offer, Remote client) throws RemoteException {

        // Deserealiza a oferta
        TripInfo trip = (TripInfo) Serializer.decode(offer);
        trip.setCliente(client);

        // Adiciona a oferta na lista
        System.out.println("\n[ offerTrip ] Adicionando viagem como oferta: ");
        addTrip(this.offer, trip);
        trip.printTrip();

        // Verifica se existe uma demanda para essa oferta
        ArrayList<TripInfo> trips = getTrips(this.demand, trip);

        // Notifica as demandas existentes sobre a sua chegada
        for(TripInfo i : trips) {
            ((Interfaces.Client) i.getCliente()).notification(trip);
        }


    }

    // Metodo para inserir demandas e retornar as cotacoes
    @Override
    public ArrayList<TripInfo> demandTrip(byte[] demand, Remote client) throws RemoteException {

        // Deserealiza a demanda
        TripInfo trip = (TripInfo) Serializer.decode(demand);
        trip.setCliente(client);

        // Adiciona a demanda na lista
        System.out.println("\n[ demandTrip ] Adicionando viagem como demanda: ");
        addTrip(this.demand, trip);
        trip.printTrip();

        // Verifica se a viagem ja foi cadastrado como oferta
        ArrayList<TripInfo> trips = getTrips(this.offer, trip);

        // Notifica as ofertas existentes sobre a sua chegada
        for(TripInfo i : trips) {
            ((Interfaces.Client) i.getCliente()).proposal(i.getCliente(), trip, i.getIndex());
        }

        return trips;
    }

    private synchronized TripInfo delTrip(ArrayList<TripInfo> trips, int index) {
        TripInfo aux = null;
        for(TripInfo trip : trips) {
            if(trip.getIndex() == index) {
                aux = trip;
                trips.remove(aux);
                break;
            }
        }
        return aux;
    }

    @Override
    public boolean bookOffer(int tripIndex) throws RemoteException {

        System.out.println("[ bookOffer ] Removendo demanda e oferta...");

        System.out.println("index " + tripIndex);

        TripInfo trip = delTrip(this.offer, tripIndex);

        if(trip != null) {

            trip.printTrip();

            ArrayList<TripInfo> trips = getTrips(this.demand, trip);

            for (TripInfo i : trips) {
                if (i.getCliente() == trip.getCliente()) {
                    delTrip(this.demand, i.getIndex());
                }
            }


            return true;
        }

        //((Interfaces.Client) trip.getCliente()).receiveOffer(trip);

        return false;
    }

    @Override
    public boolean makeOffer(int index, TripInfo trip, double value) throws RemoteException {
        if(((Interfaces.Client) trip.getCliente()).receiveOffer(trip, value)) {
            return bookOffer(index);
        }
        return false;
    }
}
