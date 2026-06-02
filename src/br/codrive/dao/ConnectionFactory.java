/**
 * Classe: ConnectionFactory
 * Objetivo: Fornece conexões JDBC com o banco de dados MySQL, lendo as
 *           credenciais do arquivo db.properties no classpath. Ponto único
 *           de acesso à conexão para todas as classes DAO do sistema.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream in = ConnectionFactory.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException(
                    "Arquivo db.properties nao encontrado no classpath.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar db.properties: " + e.getMessage(), e);
        }
        URL      = props.getProperty("db.url");
        USER     = props.getProperty("db.user");
        PASSWORD = props.getProperty("db.password");
    }

    private ConnectionFactory() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
