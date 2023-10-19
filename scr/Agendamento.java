import java.io.Serializable;

// Classe para representar um agendamento
public class Agendamento implements Serializable {
    private String nomeCliente;
    private String descricaoProblema;
    private String dataHora;
    private String endereco;
    private String numeroTelefone;

    // Construtor da classe Agendamento
    public Agendamento(String nomeCliente, String descricaoProblema, String dataHora,
                       String endereco, String numeroTelefone) {
        this.nomeCliente = nomeCliente;
        this.descricaoProblema = descricaoProblema;
        this.dataHora = dataHora;
        this.endereco = endereco;
        this.numeroTelefone = numeroTelefone;
    }

    // Método para obter o nome do cliente
    public String getNomeCliente() {
        return nomeCliente;
    }

    // Método para obter a descrição do problema
    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    // Método para obter a data e hora do agendamento
    public String getDataHora() {
        return dataHora;
    }

    // Método para obter o endereço do cliente
    public String getEndereco() {
        return endereco;
    }

    // Método para obter o número de telefone do cliente
    public String getNumeroTelefone() {
        return numeroTelefone;
    }
}
