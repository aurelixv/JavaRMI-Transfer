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

        // TODO: inicializar estrutura de dados aqui
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
            if(trip.getItinerario() == candidate.getItinerario() &&
                    trip.getTipoVeiculo() == candidate.getTipoVeiculo() &&
                    trip.getNumPassageiros() <= candidate.getNumPassageiros()) {
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    @Override
    public ArrayList<TripInfo> searchOffers(int itinerario, int numPassageiros, double preco) throws RemoteException {
        TripInfo trip = new TripInfo();
        trip.setItinerario(itinerario);
        trip.setNumPassageiros(numPassageiros);
        trip.setPreco(preco);

        ArrayList<TripInfo> trips = getTrips(this.offer, trip);

        System.out.println("[ searchOffers ] Viagens enviadas para o cliente.");

        return trips;
    }

    @Override
    public ArrayList<TripInfo> searchDemands(int itinerario, int numPassageiros, double preco) throws RemoteException {
        TripInfo trip = new TripInfo();
        trip.setItinerario(itinerario);
        trip.setNumPassageiros(numPassageiros);
        trip.setPreco(preco);

        ArrayList<TripInfo> trips = getTrips(this.demand, trip);

        System.out.println("[ searchDemands ] Viagens enviadas para o cliente.");

        return trips;
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

    // Metodo para inserir demandas
    @Override
    public void demandTrip(byte[] demand, Remote client) throws RemoteException {

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
            ((Interfaces.Client) i.getCliente()).notification(trip);
        }

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
    public int bookOffer(int tripIndex) throws RemoteException {

        TripInfo trip = delTrip(this.offer, tripIndex);
        trip.printTrip();

        System.out.println(trip.getCliente());

        ((Interfaces.Client) trip.getCliente()).receiveOffer(trip);

        return 0;
    }

    @Override
    public void chamar(String frase, Remote client) throws RemoteException {
        System.out.println("servidor");
        Client cli = (Client) client;
        cli.echo(frase);
        System.out.println("tchau");
    }

}
