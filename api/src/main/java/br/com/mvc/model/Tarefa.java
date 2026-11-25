package br.com.mvc.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Representa uma tarefa da lista.
 *
 * Assim como Usuario, implementa Mapeavel e sabe se preencher sozinha
 * a partir de um ResultSet.
 */
public class Tarefa implements Mapeavel {

    private Long id;
    private Long usuarioId;
    private String titulo;
    private String descricao;
    private boolean concluida;
    private Timestamp criadaEm;

    public Tarefa() {
    }

    public Tarefa(Long id, Long usuarioId, String titulo, String descricao, boolean concluida) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.concluida = concluida;
    }

    @Override
    public void preencher(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.usuarioId = rs.getLong("usuario_id");
        this.titulo = rs.getString("titulo");
        this.descricao = rs.getString("descricao");
        this.concluida = rs.getBoolean("concluida");
        this.criadaEm = rs.getTimestamp("criada_em");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    public Timestamp getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(Timestamp criadaEm) {
        this.criadaEm = criadaEm;
    }
}
