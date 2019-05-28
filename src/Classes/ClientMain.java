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

        Scanner in = new Scanner(System.in);

        // OFERTAS
        if(Integer.parseInt(args[0]) == 1) {

            while(true) {
                System.out.println("--- Cliente Oferta ---");
                System.out.println("1 - Cadastrar oferta");
                System.out.println("2 - Propor novo preco");
                System.out.println("9 - Spam (debug)");

                System.out.print(">: ");

                switch (in.nextInt()) {
                    case 1:
                        TripInfo trip = new TripInfo();
//                        System.out.print("Itinerario: ");
//                        trip.setItinerario(in.nextInt());
                        System.out.print("TipoVeiculo (1) SUV (2) VAN (3) ONIBUS: ");
                        trip.setTipoVeiculo(in.nextInt());
                        System.out.print("NumPassageiros: ");
                        trip.setNumPassageiros(in.nextInt());
                        System.out.print("Preco: ");
                        trip.setPreco(in.nextDouble());

                        System.out.println("Enviando oferta pro servidor...");

                        cli.getServer().offerTrip(Serializer.encode(trip), cli);

                        System.out.println("Oferta cadastrada.");

                        break;

                    case 2:

                        if(cli.getProposalTrip() != null && cli.getProposalClient() != null) {
                            System.out.print("Quanto deseja oferecer?: ");
                            if(cli.getServer().makeOffer(cli.getProposalIndex(), cli.getProposalTrip(), in.nextDouble())) {
                                System.out.println("Mudanca aceita e oferta reservada.");
                                cli.setProposalClient(null);
                                cli.setProposalTrip(null);
                            } else {
                                System.out.println("Mudanca recusada.");
                            }
                        }
                        else {
                            System.out.println("Nenhum cliente demonstrou interesse ainda.");
                        }

                        break;

                    case 9:

                        System.out.println("Enviando ofertas pro servidor...");

                        cli.getServer().offerTrip(Serializer.encode(new TripInfo(1, 3, 200)), cli);
                        cli.getServer().offerTrip(Serializer.encode(new TripInfo(2, 10, 450)), cli);
                        cli.getServer().offerTrip(Serializer.encode(new TripInfo(2, 8, 550)), cli);
                        cli.getServer().offerTrip(Serializer.encode(new TripInfo(3, 20, 1300)), cli);

                        break;

                    default:
                        System.out.println("Opcao invalida.");
                }

            }
        }

        // DEMANDAS
        if(Integer.parseInt(args[0]) == 2) {

//            while(true) {

                System.out.println("--- Cliente Demanda ---");
                System.out.println("1 - Obter cotacao");
                System.out.println("2 - Reservar oferta");
                System.out.println("9 - debug");

                System.out.print(">: ");

                switch (in.nextInt()) {
                    case 1:
                        TripInfo trip = new TripInfo();
                        System.out.print("Itinerario: ");
                        trip.setItinerario(in.nextInt());
                        System.out.print("TipoVeiculo (1) SUV (2) VAN (3) ONIBUS: ");
                        trip.setTipoVeiculo(in.nextInt());
                        System.out.print("NumPassageiros: ");
                        trip.setNumPassageiros(in.nextInt());
                        System.out.print("Preco: ");
                        trip.setPreco(in.nextDouble());

                        System.out.println("\nConsultando o servidor...\n");

                        ArrayList<TripInfo> trips = cli.getServer().demandTrip(Serializer.encode(trip), cli);

                        if(trips.size() > 0) {
                            for (TripInfo i : trips) {
                                i.printTrip();
                                System.out.println("\n");
                            }
                        } else {
                            System.out.println("Nenhum resultado encontrado.");
                        }

                        break;
                    case 2:
                        System.out.print("Digite o index da oferta a ser reservada: ");

                        if(cli.getServer().bookOffer(in.nextInt())) {
                            System.out.println("Reservado com sucesso.");
                        } else {
                            System.out.println("Erro ao reservar oferta.");
                        }

                        break;

                    case 9:

                        System.out.println("Enviando demanda pro servidor...");
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

//}

class ClientImpl extends UnicastRemoteObject implements Client {

    private Interfaces.Server server;

    private Remote proposalClient;
    private TripInfo proposalTrip;

    public int getProposalIndex() {
        return proposalIndex;
    }

    public void setProposalIndex(int proposalIndex) {
        this.proposalIndex = proposalIndex;
    }

    private int proposalIndex;

    ClientImpl(Remote server) throws RemoteException {
        this.server = (Interfaces.Server) server;
        this.proposalClient = null;
        this.proposalTrip = null;
    }


    @Override
    public boolean receiveOffer(TripInfo trip, double newValue) throws RemoteException {
        System.out.println("Viagem recebida:");
        System.out.println("Itinerario: " + trip.getItinerario());
        System.out.println("Num passageiros: " + trip.getNumPassageiros());
        System.out.println("Preco: " + trip.getPreco());
        System.out.println("Novo preco: " + newValue);
        System.out.print("Aceita essa oferta? (1 - Sim, 0 - Nao) >: ");
        Scanner scan = new Scanner(System.in);
        int decision = scan.nextInt();

        if (decision == 1) {
            System.out.println("Oferta aceita.");
            return true;
        } else {
            System.out.println("Oferta recusada.");
            return false;
        }

    }

    @Override
    public void notification(TripInfo trip) throws RemoteException {
        System.out.println("\n\n---- Notificacao ----");
        trip.printTrip();
        System.out.println("---------------------\n\n");
    }

    Interfaces.Server getServer() {
        return this.server;
    }

    @Override
    public void proposal(Remote client, TripInfo trip, int index) throws RemoteException {
        ((Interfaces.Client) client).notification(trip);

        this.proposalClient = client;
        this.proposalIndex = index;
        this.proposalTrip = trip;

//        System.out.print("Deseja fazer nova proposta diretamente a esse cliente? (1) Sim (0) Nao: ");
//        Scanner in = new Scanner(System.in);
//        int option = in.nextInt();
//
//        if(option == 1) {
//            System.out.print("Digite o novo valor: ");
//            if(((Interfaces.Client) trip.getCliente()).receiveOffer(trip, in.nextDouble())) {
//                return true;
//            }
//        }
//        return false;
    }


    public Remote getProposalClient() {
        return proposalClient;
    }

    public TripInfo getProposalTrip() {
        return proposalTrip;
    }
    public void setProposalClient(Remote proposalClient) {
        this.proposalClient = proposalClient;
    }

    public void setProposalTrip(TripInfo proposalTrip) {
        this.proposalTrip = proposalTrip;
    }

}
