package Classes;

import Interfaces.Client;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) throws RemoteException, NotBoundException {
        int port = 6000;
        Registry registry = LocateRegistry.getRegistry(port);
        ClientImpl cli = new ClientImpl(registry.lookup("Servidor"));
        //cli.echo("Ola, mundo!");

        Scanner in = new Scanner(System.in);

        // OFERTAS
        if(Integer.parseInt(args[0]) == 1) {

            while(true) {
                System.out.println("--- Cliente Oferta ---");
                System.out.println("1 - Cadastrar oferta");
                System.out.println("9 - Spam (debug)");

                System.out.print(">: ");

                switch (in.nextInt()) {
                    case 1:
                        TripInfo trip = new TripInfo();
                        System.out.print("Itinerario: ");
                        trip.setItinerario(in.nextInt());
                        System.out.print("TipoVeiculo: ");
                        trip.setTipoVeiculo(in.nextInt());
                        System.out.print("NumPassageiros: ");
                        trip.setNumPassageiros(in.nextInt());
                        System.out.print("Preco: ");
                        trip.setPreco(in.nextDouble());

                        System.out.println("Enviando oferta pro servidor...");

                        cli.getServer().offerTrip(Serializer.encode(trip), cli);

                        break;
                    case 9:

                        System.out.println("Enviando ofertas pro servidor...");

                        cli.getServer().offerTrip(Serializer.encode(new TripInfo(
                                1, 2, 3, 100)), cli);
                        cli.getServer().offerTrip(Serializer.encode(new TripInfo(
                                2, 2, 3, 200)), cli);
                        cli.getServer().offerTrip(Serializer.encode(new TripInfo(
                                2, 2, 4, 300)), cli);
                        cli.getServer().offerTrip(Serializer.encode(new TripInfo(
                                3, 1, 1, 50)), cli);

                        break;

                    default:
                        System.out.println("Opcao invalida.");
                }

            }
        }

        // DEMANDAS
        if(Integer.parseInt(args[0]) == 2) {

            while(true) {

                System.out.println("--- Cliente Oferta ---");
                System.out.println("1 - Cadastrar demanda");
                System.out.println("2 - Buscar demanda");
                System.out.println("9 - debug");

                System.out.print(">: ");

                switch (in.nextInt()) {
                    case 1:
                        TripInfo trip = new TripInfo();
                        System.out.print("Itinerario: ");
                        trip.setItinerario(in.nextInt());
                        System.out.print("TipoVeiculo: ");
                        trip.setTipoVeiculo(in.nextInt());
                        System.out.print("NumPassageiros: ");
                        trip.setNumPassageiros(in.nextInt());
                        System.out.print("Preco: ");
                        trip.setPreco(in.nextDouble());

                        System.out.println("Enviando oferta pro servidor...");

                        cli.getServer().demandTrip(Serializer.encode(trip), cli);
                        break;
                    case 2:

                        System.out.print("Itnerario: ");
                        int itinerario = in.nextInt();
                        System.out.print("NumPassageiros: ");
                        int numPassageiros = in.nextInt();
                        System.out.print("Preco: ");
                        double preco = in.nextDouble();

                        System.out.println("Buscando demanda no servidor... ");

                        ArrayList<TripInfo> trips = cli.getServer().searchOffers(itinerario, numPassageiros, preco);

                        if(trips.size() > 0) {
                            System.out.println("Viagens encontradas:");
                            int i = 1;

                            for(TripInfo candidate : trips) {
                                System.out.println((i++) + " ------------------------");
                                candidate.printTrip();
                            }

                            System.out.print("Qual viagem deseja reservar?: (-1 para cancelar): ");

                            int aux = in.nextInt();

                            if(aux >= 0) {
                                System.out.println("Enviando proposta para a oferta...");
                                if(cli.getServer().bookOffer(aux) == 1) {

                                }
                            }
                            else
                                System.out.println("Cancelando operacao...");

                        } else {
                            System.out.println("Nao foram encontradas viagens.");
                        }

                    case 9:

                        System.out.println("Enviando oferta pro servidor...");
                        cli.getServer().demandTrip(Serializer.encode(new TripInfo(
                                2, 2, 2, 100
                        )), cli);

                        break;

                    default:
                        System.out.println("Opcao invalida.");
                }

            }

        }
    }

}

class ClientImpl extends UnicastRemoteObject implements Client {

    private Interfaces.Server server;

    ClientImpl(Remote server) throws RemoteException {
        this.server = (Interfaces.Server) server;
    }

    @Override
    public void echo(String frase) throws RemoteException {
        System.out.println(frase);
        this.server.chamar(frase, this);
    }

    @Override
    public boolean receiveOffer(TripInfo trip) throws RemoteException {
        System.out.println("Viagem recebida:");
        System.out.println("Itinerario: " + trip.getItinerario());
        System.out.println("Num passageiros: " + trip.getNumPassageiros());
        System.out.println("Preco: " + trip.getPreco());
        System.out.print("Aceita essa oferta? (1 - Sim, 0 - Nao) >: ");
        Scanner scan = new Scanner(System.in);
        int decision = scan.nextInt();

        if(decision == 1) {
            System.out.println("Oferta aceita.");
            return true;
        } else {
            System.out.println("Oferta recusada.");
            return false;
        }

    }

    @Override
    public void notification(TripInfo trip) throws RemoteException {
        System.out.println("\n---- Notificacao ----");
        trip.printTrip();
    }

    Interfaces.Server getServer() {
        return this.server;
    }

}
