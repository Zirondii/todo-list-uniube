package br.com.mvc.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public record ListaTarefas(List<JsonNode> tarefas, int total, long pendentes) {}
