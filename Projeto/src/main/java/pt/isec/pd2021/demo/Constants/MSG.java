package pt.isec.pd2021.demo.Constants;

import java.io.Serializable;

public class MSG implements Serializable {
    String comando;
    String descricao;
    String id_cliente;

    public MSG(String comando, String descricao, String id_cliente) {
        this.comando = comando;
        this.descricao = descricao;
        this.id_cliente = id_cliente;
    }

    public String getId_Cliente() {
        return id_cliente;
    }

    public void setId_Cliente(String id_Cliente) {
        this.id_cliente = id_Cliente;
    }

    public String getComando() {
        return comando;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "Comando: " + comando + " Descricao: " + descricao;
    }
}
