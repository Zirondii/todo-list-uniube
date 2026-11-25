# ETAPA 1 - Compilacao
# Usa uma imagem que ja tem Maven e Java instalados para gerar o arquivo .war.
# Assim nao e preciso ter Java instalado na maquina.
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia primeiro o pom.xml e baixa as dependencias.
# Como o Docker guarda cache por etapa, as dependencias so sao baixadas de novo
# se o pom.xml mudar - o que deixa as proximas compilacoes bem mais rapidas.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Agora copia o codigo-fonte e gera o .war
COPY src ./src
RUN mvn clean package -B


# ETAPA 2 - Execucao
# Imagem enxuta, apenas com o Tomcat. O Maven e o codigo-fonte ficam para tras.
FROM tomcat:10.1-jdk17

# Remove as aplicacoes de exemplo que vem junto com o Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia o .war gerado na etapa anterior.
# O nome ROOT.war faz a aplicacao responder direto em http://localhost:8080
COPY --from=build /app/target/api.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
