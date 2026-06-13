package com.portal.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;

public class DatabaseConfig {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");
    private static HikariDataSource dataSource;

    // Executado uma única vez quando a classe é carregada pela JVM
    static {
        try {
            HikariConfig config = new HikariConfig();

            // Configurações de acesso ao banco
            config.setJdbcUrl(URL);
            config.setUsername(USER);
            config.setPassword(PASSWORD);

            // Número máximo de conexões simultâneas no pool
            config.setMaximumPoolSize(10);

            // Mantém pelo menos 2 conexões prontas para uso
            config.setMinimumIdle(2);

            // Tempo máximo de espera por uma conexão livre (30s)
            config.setConnectionTimeout(30000);

            // Fecha conexões ociosas após 10 minutos
            // (respeitando o mínimo definido em MinimumIdle)
            config.setIdleTimeout(600000);

            // Renova conexões após 30 minutos de vida
            config.setMaxLifetime(1800000);

            // Cria o pool de conexões
            dataSource = new HikariDataSource(config);

            System.out.println("HikariCP Pool inicializado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao inicializar HikariCP: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Obtém uma conexão do pool
    public static Connection getConnection() {
        try {
            if (dataSource != null) {
                return dataSource.getConnection();
            }
        } catch (Exception e) {
            System.out.println("Erro ao obter conexão: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Fecha o pool ao encerrar a aplicação
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Pool de conexões fechado!");
        }
    }
}
