package Classes;

import Interfaces.Client;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientMain {

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(6000);
        ClientImpl cli = new ClientImpl(registry.lookup("Servidor"));
        //cli.echo("Ola, mundo!");

        if(Integer.parseInt(args[0]) == 1) {

            TripInfo trip = new TripInfo();
            trip.setItinerario(5);
            trip.setNumPassageiros(2);
            trip.setPreco(100);

            cli.getServer().offerTrip(Serializer.encode(trip), cli);
        }

        if(Integer.parseInt(args[0]) == 2) {

            TripInfo trip = new TripInfo();
            trip.setItinerario(5);
            trip.setNumPassageiros(2);
            trip.setPreco(100);

            cli.getServer().demandTrip(Serializer.encode(trip), cli);
        }
    }

}

class ClientImpl extends UnicastRemoteObject implements Client {

    private Interfaces.Server server;

    ClientImpl(Remote server) throws RemoteException {
        this.server = (Interfaces.Server) server;
    }

    public void echo(String frase) throws RemoteException {
        System.out.println(frase);
        this.server.chamar(frase, this);
    }

    @Override
    public void receiveOffer(TripInfo trip) throws RemoteException {
        System.out.println("Viagem recebida:");
        System.out.println(trip.toString());
    }

    public Interfaces.Server getServer() {
        return this.server;
    }

}
