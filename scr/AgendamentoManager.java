import java.sql.*;
import java.util.*;

public class AgendamentoManager {
    private String arquivo;

    /**
     * @param arquivo O nome do arquivo do banco de dados a ser utilizado.
     */
    public AgendamentoManager(String arquivo) {
        this.arquivo = arquivo;
    }

    /**
     * Agendar um novo atendimento.
     
     * @param nomeCliente Nome do cliente para o agendamento.
     * @param descricaoProblema Descrição do problema relatado pelo cliente.
     * @param endereco Endereço onde o atendimento será realizado.
     * @param numeroTelefone Número de telefone para contato do cliente.
     */
    public void agendar(String nomeCliente, String descricaoProblema,
                        String endereco, String numeroTelefone) {
        try {
            // Estabelece a conexão com o banco de dados
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/agendamentos", "root", "1234");

            // Cria a consulta para inserção do agendamento
            String insertQuery = "INSERT INTO agendamentos (nome_cliente, descricao_problema, data_hora, endereco, numero_telefone) VALUES (?, ?, NOW(), ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, nomeCliente);
            preparedStatement.setString(2, descricaoProblema);
            preparedStatement.setString(3, endereco);
            preparedStatement.setString(4, numeroTelefone);

            // Executa a inserção
            preparedStatement.executeUpdate();

            // Fecha recursos
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /* Limpa todos os agendamentos do banco de dados. */
    public void limparAgendamentos() {
        try {
            // Estabelece a conexão com o banco de dados
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/agendamentos", "root", "1234");
            Statement statement = connection.createStatement();
            // Executa o comando para apagar todos os agendamentos
            statement.executeUpdate("DELETE FROM agendamentos");
            // Fecha recursos
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
