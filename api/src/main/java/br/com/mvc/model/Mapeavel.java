package br.com.mvc.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Contrato para as classes que podem ser lidas do banco de dados.
 *
 * Antes, cada DAO tinha um metodo "mapear" que copiava os dados do ResultSet
 * para o objeto. Isso se repetia em todo DAO novo.
 *
 * Agora quem sabe preencher os proprios dados e a propria classe de modelo,
 * e o DAO apenas pede que ela se preencha.
 */
public interface Mapeavel {

    void preencher(ResultSet rs) throws SQLException;
}
