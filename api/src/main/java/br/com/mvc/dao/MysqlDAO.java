package br.com.mvc.dao;

import br.com.mvc.config.MysqlSingleton;
import br.com.mvc.model.Mapeavel;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe base dos DAOs que usam MySQL.
 *
 * Guarda a referencia ao MysqlSingleton (uma conexao reutilizada).
 * Os DAOs filhos usam executar() para SELECT e executarUpdate() para
 * INSERT / UPDATE / DELETE.
 */
public class MysqlDAO {

    protected final MysqlSingleton banco;

    public MysqlDAO() {
        this.banco = MysqlSingleton.getInstance();
    }

    protected ResultSet executar(String sql, Object... parametros) throws SQLException {
        return this.banco.executar(sql, parametros);
    }

    protected int executarUpdate(String sql, Object... parametros) throws SQLException {
        return this.banco.executarUpdate(sql, parametros);
    }

    /**
     * Transforma a linha atual do ResultSet em um objeto.
     *
     * Funciona para qualquer classe que implemente Mapeavel: cria uma instancia
     * vazia e pede que ela mesma se preencha. Com isso nenhum DAO precisa mais
     * escrever o metodo "mapear" na mao.
     */
    protected <T extends Mapeavel> T mapearSimples(ResultSet rs, Class<T> classe) {
        try {
            // Cria um objeto novo da classe pedida (ex.: Usuario ou Tarefa)
            T objeto = classe.getDeclaredConstructor().newInstance();

            // O proprio objeto copia os dados do ResultSet para seus atributos
            objeto.preencher(rs);

            return objeto;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao mapear objeto: " + e.getMessage(), e);
        }
    }
}
