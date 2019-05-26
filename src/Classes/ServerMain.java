package Classes;

import Interfaces.Client;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServerMain {

    private static int port = 6000;

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        System.out.println("Inicializando servidor na porta " + port + "...");
        Registry registry = LocateRegistry.createRegistry(port);
        ServerImpl server = new ServerImpl();
        registry.bind("Servidor", server);
        System.out.println("Servidor inicializado.");
    }

}

class ServerImpl extends UnicastRemoteObject implements Interfaces.Server {

    private ArrayList<TripInfo> offer;
    private ArrayList<TripInfo> demand;

    ServerImpl() throws RemoteException {
        System.out.println("Criando objeto remoto servidor...");

        // TODO: inicializar estrutura de dados aqui
        offer = new ArrayList<>();
        demand = new ArrayList<>();
    }

    ArrayList<TripInfo> getTrips(ArrayList<TripInfo> trips, TripInfo trip) {
        ArrayList<TripInfo> candidates = new ArrayList<>();
        for (TripInfo candidate : trips) {
            if(trip.getItinerario() == candidate.getItinerario() &&
                    trip.getTipoVeiculo() == candidate.getTipoVeiculo() &&
                    trip.getNumPassageiros() <= candidate.getNumPassageiros()) {
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    //
    public void offerTrip(byte[] offer, Remote cliente) throws RemoteException {
        TripInfo trip = (TripInfo) Serializer.decode(offer);
        trip.setCliente(cliente);
        this.offer.add(trip);

    }

    // Metodo para inserir interesse de viajens, chamado por clientes
    public void demandTrip(byte[] demand, Remote client) throws RemoteException {

        TripInfo trip = (TripInfo) Serializer.decode(demand);

        // Verifica se a viagem ja foi cadastrado como oferta
        ArrayList<TripInfo> candidates = getTrips(this.offer, trip);

        // Se achou alguma viagem
        if(candidates.size() > 0) {

            TripInfo candidate = candidates.get(0);

            for(TripInfo i : candidates) {
                if(i.getPreco() < candidate.getPreco()) {
                    candidate = i;
                }
            }

            ((Client) client).receiveOffer(candidate);
            //((Client) candidate.getCliente()).receiveDemand();

        } else {
            trip.setCliente(client);
            this.demand.add(trip);
        }

    }

    public void chamar(String frase, Remote client) throws RemoteException {
        System.out.println("servidor");
        Client cli = (Client) client;
        cli.echo(frase);
        System.out.println("tchau");
    }

}
