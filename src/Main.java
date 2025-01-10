import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Main {
    // Classe para representar os dados do aluno
    static class Aluno {
        private String nome;
        private int idade;
        private float peso;
        private float altura;
        private String objetivo;

        public Aluno(String nome, int idade, float peso, float altura, String objetivo) {
            this.nome = nome;
            this.idade = idade;
            this.peso = peso;
            this.altura = altura;
            this.objetivo = objetivo;
        }
    }

    public class DatabaseUtil {
        private static final String URL = "jdbc:mysql://localhost:3306/academia";
        private static final String USER = "";
        private static final String PASSWORD = "";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }

    public static void main(String[] args) {
        // Lista para simular o banco de dados
        List<Aluno> bancoDeDados = new ArrayList<>();

        // Criar o frame principal
        JFrame frame = new JFrame("Cadastro de Aluno - Academia");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(7, 2));

        // Componentes da tela
        JLabel lblNome = new JLabel("Nome:");
        JTextField txtNome = new JTextField();
        JLabel lblIdade = new JLabel("Idade:");
        JTextField txtIdade = new JTextField();
        JLabel lblPeso = new JLabel("Peso (kg):");
        JTextField txtPeso = new JTextField();
        JLabel lblAltura = new JLabel("Altura (m):");
        JTextField txtAltura = new JTextField();
        JLabel lblObjetivo = new JLabel("Objetivo:");
        JTextField txtObjetivo = new JTextField();

        JButton btnIncluir = new JButton("Incluir");
        JButton btnLimpar = new JButton("Limpar");
        JButton btnApresentar = new JButton("Apresentar Dados");
        JButton btnSair = new JButton("Sair");

        // Adicionando os componentes ao frame
        frame.add(lblNome);
        frame.add(txtNome);
        frame.add(lblIdade);
        frame.add(txtIdade);
        frame.add(lblPeso);
        frame.add(txtPeso);
        frame.add(lblAltura);
        frame.add(txtAltura);
        frame.add(lblObjetivo);
        frame.add(txtObjetivo);
        frame.add(btnIncluir);
        frame.add(btnLimpar);
        frame.add(btnApresentar);
        frame.add(btnSair);

        // Ação para o botão "Incluir"
        btnIncluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connection = DatabaseUtil.getConnection()) {
                    String nome = txtNome.getText();
                    int idade = Integer.parseInt(txtIdade.getText());
                    float peso = Float.parseFloat(txtPeso.getText());
                    float altura = Float.parseFloat(txtAltura.getText());
                    String objetivo = txtObjetivo.getText();

                    String sql = "INSERT INTO alunos (nome, idade, peso, altura, objetivo) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, nome);
                        stmt.setInt(2, idade);
                        stmt.setFloat(3, peso);
                        stmt.setFloat(4, altura);
                        stmt.setString(5, objetivo);
                        stmt.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(frame, "Aluno incluído com sucesso!");
                } catch (Exception ex) {
                    System.err.println("Erro ao incluir aluno: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Erro ao incluir aluno: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Ação para o botão "Limpar"
        btnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtNome.setText("");
                txtIdade.setText("");
                txtPeso.setText("");
                txtAltura.setText("");
                txtObjetivo.setText("");
            }
        });

        // Ação para o botão "Apresentar Dados"
        btnApresentar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connection = DatabaseUtil.getConnection()) {
                    String sql = "SELECT nome, idade, peso, altura, objetivo FROM alunos";
                    try (PreparedStatement stmt = connection.prepareStatement(sql);
                         ResultSet rs = stmt.executeQuery()) {

                        List<Aluno> alunos = new ArrayList<>();
                        while (rs.next()) {
                            String nome = rs.getString("nome");
                            int idade = rs.getInt("idade");
                            float peso = rs.getFloat("peso");
                            float altura = rs.getFloat("altura");
                            String objetivo = rs.getString("objetivo");
                            alunos.add(new Aluno(nome, idade, peso, altura, objetivo));
                        }

                        if (alunos.isEmpty()) {
                            JOptionPane.showMessageDialog(frame, "Nenhum dado disponível.");
                        } else {
                            Gson gson = new Gson();
                            String jsonDados = gson.toJson(alunos);
                            JOptionPane.showMessageDialog(frame, jsonDados);
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Erro ao apresentar dados: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Erro ao apresentar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Ação para o botão "Sair"
        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Exibir a tela
        frame.setVisible(true);
    }
}