package br.com.mvc.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Responsavel pela conexao com o banco de dados MySQL.
 *
 * Usa o padrao Singleton: existe apenas UMA instancia desta classe na aplicacao
 * inteira, reaproveitando a mesma conexao em vez de abrir uma nova a cada consulta.
 */
public class MysqlSingleton {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    // Le os dados de conexao das variaveis de ambiente (definidas no docker-compose.yml).
    // Se nao existirem, usa os valores padrao - assim o projeto tambem roda fora do Docker.
    static {
        URL = valorOuPadrao("DB_URL",
                "jdbc:mysql://localhost:3306/todolist?useSSL=false&serverTimezone=UTC");
        USER = valorOuPadrao("DB_USER", "root");
        PASSWORD = valorOuPadrao("DB_PASSWORD", "root");
    }

    private static String valorOuPadrao(String variavel, String padrao) {
        String valor = System.getenv(variavel);
        if (valor == null || valor.isBlank()) {
            return padrao;
        }
        return valor;
    }

    private static MysqlSingleton instance;
    private Connection conexao;

    // Construtor privado: ninguem consegue dar "new MysqlSingleton()" de fora.
    // A unica forma de obter a instancia e pelo metodo getInstance().
    private MysqlSingleton() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL nao encontrado no projeto.", e);
        }
    }

    public static synchronized MysqlSingleton getInstance() {
        if (instance == null) {
            instance = new MysqlSingleton();
        }
        return instance;
    }

    private Connection obterConexao() throws SQLException {
        if (this.conexao == null || this.conexao.isClosed()) {
            this.conexao = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return this.conexao;
    }

    /**
     * Executa consultas SELECT e devolve o resultado.
     */
    public ResultSet executar(String sql, Object... parametros) throws SQLException {
        Connection conn = this.obterConexao();
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < parametros.length; i++) {
            ps.setObject(i + 1, parametros[i]);
        }
        return ps.executeQuery();
    }

    /**
     * Executa INSERT, UPDATE e DELETE. Devolve a quantidade de linhas afetadas.
     */
    public int executarUpdate(String sql, Object... parametros) throws SQLException {
        Connection conn = this.obterConexao();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            return ps.executeUpdate();
        }
    }
}
