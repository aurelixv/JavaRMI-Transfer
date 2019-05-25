import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

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

    ServerImpl() throws RemoteException {
        System.out.println("Criando objeto remoto servidor...");
    }

    public void chamar(String frase, Remote client) throws RemoteException {
        System.out.println("servidor");
        Interfaces.ClientDemand cli = (Interfaces.ClientDemand) client;
        cli.echo(frase);
        System.out.println("tchau");
    }

}
