package Classes;

import java.io.Serializable;
import java.rmi.Remote;

public class TripInfo implements Serializable {

    //fornecendo o itinerário, data e horário, tipo de veículo,   número   de   passageirosepreço   proposto

    private int itinerario;
    private int data;
    private int horario;
    private int tipoVeiculo;
    private int numPassageiros;
    private double preco;
    private Remote cliente;

    public TripInfo() {

    }

    public int getItinerario() {
        return itinerario;
    }

    public void setItinerario(int itinerario) {
        this.itinerario = itinerario;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getHorario() {
        return horario;
    }

    public void setHorario(int horario) {
        this.horario = horario;
    }

    public int getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(int tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public int getNumPassageiros() {
        return numPassageiros;
    }

    public void setNumPassageiros(int numPassageiros) {
        this.numPassageiros = numPassageiros;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public Remote getCliente() {
        return cliente;
    }

    public void setCliente(Remote cliente) {
        this.cliente = cliente;
    }
}
