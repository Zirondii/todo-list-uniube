const {base, tela} = document.body.dataset;
const parametros = new URLSearchParams(location.search);
const form = document.querySelector("form");
const ir = rota => location.assign(base + rota);
const token = () => sessionStorage.getItem("tarefas.token");

function sair() {
    sessionStorage.removeItem("tarefas.token");
    sessionStorage.removeItem("tarefas.login");
}

async function requisitar(caminho, metodo = "GET", dados) {
    const headers = {Accept: "application/json"};
    if (token()) headers.Authorization = "Bearer " + token();
    if (dados) headers["Content-Type"] = "application/json";
    const resposta = await fetch(base + caminho, {
        method: metodo, headers, body: JSON.stringify(dados)
    });
    if (resposta.status === 401 && !caminho.startsWith("/api/auth/")) {
        sair();
        ir("/login");
        throw new Error("Sessão expirada.");
    }
    const texto = await resposta.text();
    const resultado = texto ? JSON.parse(texto) : null;
    if (!resposta.ok) throw new Error(resultado?.erro || "Falha ao acessar a API.");
    return resultado;
}

const api = (caminho, metodo, dados) => requisitar("/api" + caminho, metodo, dados);

function mostrarErro(erro) {
    const aviso = document.querySelector(".erro");
    aviso.textContent = erro.message;
    aviso.hidden = false;
}

async function salvar(evento) {
    evento.preventDefault();
    const botao = form.querySelector("button");
    botao.disabled = true;
    try {
        const campos = Object.fromEntries(new FormData(form));
        if (tela === "login") {
            const conta = await api("/auth/login", "POST", campos);
            sessionStorage.setItem("tarefas.token", conta.token);
            sessionStorage.setItem("tarefas.login", conta.login);
        } else if (tela === "cadastro") {
            await api("/auth/cadastro", "POST", campos);
            ir("/login");
            return;
        } else {
            const edicao = parametros.get("acao") === "editar";
            const caminho = edicao ? "/tarefas/" + encodeURIComponent(parametros.get("id")) : "/tarefas";
            await api(caminho, edicao ? "PUT" : "POST", {
                titulo: campos.titulo, descricao: campos.descricao, concluida: form.concluida.checked
            });
        }
        ir("/tarefas");
    } catch (erro) {
        mostrarErro(erro);
    } finally {
        botao.disabled = false;
    }
}

function elemento(tag, texto, classe) {
    const el = document.createElement(tag);
    el.textContent = texto;
    if (classe) el.className = classe;
    return el;
}

async function listar() {
    const {tarefas, total, pendentes} = await requisitar("/dados/lista");
    const corpo = document.querySelector("tbody");
    corpo.replaceChildren();
    for (const tarefa of tarefas) {
        const linha = corpo.insertRow();
        if (tarefa.concluida) linha.className = "concluida";
        const info = linha.insertCell();
        info.append(elemento("span", tarefa.titulo, "titulo"));
        if (tarefa.descricao) info.append(elemento("span", tarefa.descricao, "descricao"));
        linha.insertCell().append(elemento("span", tarefa.concluida ? "Concluída" : "Pendente",
            tarefa.concluida ? "situacao feita" : "situacao pendente"));
        const botoes = linha.insertCell();
        botoes.className = "acoes";
        const editar = elemento("a", "Editar");
        editar.href = base + "/tarefas?acao=editar&id=" + tarefa.id;
        const estado = elemento("button", tarefa.concluida ? "Reabrir" : "Concluir");
        const excluir = elemento("button", "Excluir", "excluir");
        botoes.append(estado, editar, excluir);
        async function alterar(metodo, dados) {
            estado.disabled = excluir.disabled = true;
            try {
                await api("/tarefas/" + tarefa.id, metodo, dados);
                await listar();
            } catch (erro) {
                mostrarErro(erro);
                estado.disabled = excluir.disabled = false;
            }
        }
        estado.onclick = () => alterar("PATCH", {concluida: !tarefa.concluida});
        excluir.onclick = () => { if (confirm("Deseja excluir esta tarefa?")) alterar("DELETE"); };
    }
    document.querySelector("table").hidden = tarefas.length === 0;
    document.querySelector(".vazio").hidden = tarefas.length > 0;
    document.querySelector(".resumo").textContent = total + " tarefa(s) - " + pendentes + " pendente(s)";
}

async function iniciar() {
    if (location.pathname === base + "/logout") {
        sair();
        ir("/login");
        return;
    }
    if (tela === "login" && token()) { ir("/tarefas"); return; }
    if ((tela === "form" || tela === "lista") && !token()) { ir("/login"); return; }
    const usuario = document.querySelector("[data-login]");
    if (usuario) usuario.textContent = sessionStorage.getItem("tarefas.login");
    if (tela === "lista") await listar();
    if (form) form.addEventListener("submit", salvar);
    if (tela === "form" && parametros.get("acao") === "editar") {
        const salvar = form.querySelector("button");
        salvar.disabled = true;
        const tarefa = await api("/tarefas/" + encodeURIComponent(parametros.get("id")));
        document.title = document.querySelector("h1").textContent = "Editar tarefa";
        form.titulo.value = tarefa.titulo;
        form.descricao.value = tarefa.descricao || "";
        form.concluida.checked = tarefa.concluida;
        salvar.disabled = false;
    }
}
iniciar().catch(mostrarErro);
