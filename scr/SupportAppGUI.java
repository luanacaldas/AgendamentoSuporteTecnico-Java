import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import java.util.List;
import com.toedter.calendar.JCalendar;
import java.util.stream.Collectors;
import java.util.*;

// Classe principal que implementa a interface gráfica da aplicação de suporte
public class SupportAppGUI {
    // Variáveis de instância para os componentes da GUI
    private JFrame frame; // Janela principal da aplicação
    private JTextArea textArea; // Área de texto para exibir agendamentos
    private JTextField nomeClienteField; // Campo de texto para inserir o nome do cliente
    private JTextArea descricaoProblemaArea; // Área de texto para a descrição do problema
    private JTextField enderecoField; // Campo de texto para inserir o endereço
    private JTextField numeroTelefoneField; // Campo de texto para o número de telefone
    private JCalendar calendar; // Calendário para selecionar a data

    // Construtor da classe
    public SupportAppGUI() {
        createAndShowGUI(); // Inicializa a interface gráfica
    }

    // Método para criar e exibir a interface gráfica
    private void createAndShowGUI() {
        // Criação da janela principal
        frame = new JFrame("Agendamento de Suporte Técnico VIVO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());

        // Painel de entrada para agendamento
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
            "Agendar Suporte", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16), new Color(80, 80, 80)));

        // Componentes de entrada
        inputPanel.add(new JLabel("Nome do Cliente:"));
        nomeClienteField = new JTextField();
        inputPanel.add(nomeClienteField);
        inputPanel.add(new JLabel("Descrição do Problema:"));
        descricaoProblemaArea = new JTextArea(4, 20);
        descricaoProblemaArea.setLineWrap(true);
        inputPanel.add(new JScrollPane(descricaoProblemaArea));
        inputPanel.add(new JLabel("Endereço:"));
        enderecoField = new JTextField();
        inputPanel.add(enderecoField);
        inputPanel.add(new JLabel("Número de Telefone:"));
        numeroTelefoneField = new JTextField();
        inputPanel.add(numeroTelefoneField);

        // Botões para agendar e limpar
        JButton agendarButton = new JButton("Agendar");
        agendarButton.addActionListener(e -> agendar());
        inputPanel.add(agendarButton);

        JButton limparButton = new JButton("Limpar base de dados");
        limparButton.addActionListener(e -> limparDados());
        inputPanel.add(limparButton);

        // Adicionar painel de entrada à janela
        frame.add(inputPanel, BorderLayout.WEST);

        // Calendário para filtrar por data
        JPanel calendarPanel = new JPanel();
        calendarPanel.setLayout(new BorderLayout());
        calendar = new JCalendar();
        // Ação de selecionar um dia no calendário
        calendar.getDayChooser().addPropertyChangeListener("day", evt -> {
            Date selectedDate = calendar.getDate(); 
            filterByDate(selectedDate); // Filtrar agendamentos pela data selecionada
        }); 
        calendarPanel.add(calendar, BorderLayout.NORTH);
        frame.add(calendarPanel, BorderLayout.EAST);

        // Área de texto para exibir agendamentos
        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Inicializar a exibição dos agendamentos
        refreshTextArea(); // Atualizar a exibição de todos os agendamentos

        // Tornar a janela visível
        frame.setVisible(true);
    }

    // Método para realizar o agendamento
    private void agendar() {
        // Obter informações do agendamento a partir dos campos
        String nomeCliente = nomeClienteField.getText();
        String descricaoProblema = descricaoProblemaArea.getText();
        String endereco = enderecoField.getText();
        String numeroTelefone = numeroTelefoneField.getText();

        // Verificar se todos os campos estão preenchidos
        if (nomeCliente.isEmpty() || descricaoProblema.isEmpty() || endereco.isEmpty() || numeroTelefone.isEmpty()) {
            // Mostrar mensagem de erro se algum campo estiver vazio
            JOptionPane.showMessageDialog(frame, "Preencha todos os campos antes de agendar.",
                    "Erro de Agendamento", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Conectar ao banco de dados
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/agendamentos", "root", "1234");

            // Preparar e executar a inserção do agendamento
            String insertQuery = "INSERT INTO agendamentos (nome_cliente, descricao_problema, data_hora, endereco, numero_telefone) VALUES (?, ?, NOW(), ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, nomeCliente);
            preparedStatement.setString(2, descricaoProblema);
            preparedStatement.setString(3, endereco);
            preparedStatement.setString(4, numeroTelefone);

            preparedStatement.executeUpdate();

            // Fechar recursos e atualizar a exibição
            preparedStatement.close();
            connection.close();

            refreshTextArea();
            limparCampos();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Método para limpar todos os dados
    private void limparDados() {
        int result = JOptionPane.showConfirmDialog(frame,
            "Tem certeza de que deseja limpar todos os dados?",
            "Limpar Dados", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                // Conectar ao banco de dados
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/agendamentos", "root", "1234");
                Statement statement = connection.createStatement();
                statement.executeUpdate("DELETE FROM agendamentos");
                statement.close();
                connection.close();

                // Atualizar a exibição e mostrar mensagem
                refreshTextArea();
                JOptionPane.showMessageDialog(frame,
                    "Dados apagados com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Método para limpar campos de entrada
    private void limparCampos() {
        nomeClienteField.setText("");
        descricaoProblemaArea.setText("");
        enderecoField.setText("");
        numeroTelefoneField.setText("");
    }

    // Método para filtrar agendamentos por data selecionada no calendário
    private void filterByDate(Date selectedDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = dateFormat.format(selectedDate);
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/agendamentos", "root", "1234");

            // Crie a consulta para filtrar por data
            String selectQuery = "SELECT * FROM agendamentos WHERE DATE(data_hora) = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, dateString);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Agendamento> filteredAgendamentos = new ArrayList<>();
            while (resultSet.next()) {
                String nomeCliente = resultSet.getString("nome_cliente");
                String descricaoProblema = resultSet.getString("descricao_problema");
                String dataHora = resultSet.getString("data_hora");
                String endereco = resultSet.getString("endereco");
                String numeroTelefone = resultSet.getString("numero_telefone");
                filteredAgendamentos.add(new Agendamento(nomeCliente, descricaoProblema, dataHora, endereco, numeroTelefone));
            }

            // Exibir agendamentos filtrados
            displayAgendamentos(filteredAgendamentos);

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Método para exibir lista de agendamentos na área de texto
    private void displayAgendamentos(List<Agendamento> agendamentos) {
        textArea.setText("");

        if (agendamentos.isEmpty()) {
            textArea.append("Nenhum agendamento encontrado.");
        } else {
            for (Agendamento agendamento : agendamentos) {
                textArea.append("Cliente: " + agendamento.getNomeCliente() + "\n");
                textArea.append("Problema: " + agendamento.getDescricaoProblema() + "\n");
                textArea.append("Data/Hora: " + agendamento.getDataHora() + "\n");
                textArea.append("Endereço: " + agendamento.getEndereco() + "\n");
                textArea.append("Número de Telefone: " + agendamento.getNumeroTelefone() + "\n");
                textArea.append("---------------------------\n");
            }
        }
    }

    // Método para atualizar a área de texto com todos os agendamentos
    private void refreshTextArea() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/agendamentos", "root", "1234");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM agendamentos");

            List<Agendamento> agendamentos = new ArrayList<>();
            while (resultSet.next()) {
                String nomeCliente = resultSet.getString("nome_cliente");
                String descricaoProblema = resultSet.getString("descricao_problema");
                String dataHora = resultSet.getString("data_hora");
                String endereco = resultSet.getString("endereco");
                String numeroTelefone = resultSet.getString("numero_telefone");
                agendamentos.add(new Agendamento(nomeCliente, descricaoProblema, dataHora, endereco, numeroTelefone));
            }

            displayAgendamentos(agendamentos);

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Método principal para iniciar a aplicação
    public static void main(String[] args) {
        // Executa a criação da interface gráfica na thread de despacho de eventos
        SwingUtilities.invokeLater(() -> new SupportAppGUI());
    }
}
