import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientDemandMain {

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(6000);
        ClientDemandImpl cli = new ClientDemandImpl(registry.lookup("Servidor"));
        cli.echo("Ola, mundo!");
    }

}

class ClientDemandImpl extends UnicastRemoteObject implements Interfaces.ClientDemand {

    private Interfaces.Server server;

    ClientDemandImpl(Remote server) throws RemoteException {
        this.server = (Interfaces.Server) server;
    }

    public void echo(String frase) throws RemoteException {
        System.out.println(frase);
        this.server.chamar(frase, this);
    }
}
