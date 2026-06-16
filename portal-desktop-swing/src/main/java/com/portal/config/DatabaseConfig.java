// Pacote "config": guarda classes de configuração técnica da aplicação.
package com.portal.config;

// HikariCP é uma biblioteca que gerencia um "pool" (conjunto) de conexões com o banco.
import com.zaxxer.hikari.HikariConfig;       // Classe para configurar o pool.
import com.zaxxer.hikari.HikariDataSource;   // O pool de conexões em si.
// Dotenv lê variáveis de um arquivo ".env" (onde ficam dados sensíveis como senha do banco).
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;                  // Representa uma conexão aberta com o banco.

/**
 * DatabaseConfig: responsável por configurar e fornecer conexões com o banco de dados.
 *
 * CONCEITOS IMPORTANTES:
 *
 * 1) PADRÃO SINGLETON: existe uma única instância do pool de conexões em toda a
 *    aplicação. Em vez de abrir e fechar conexão toda hora (lento e caro), criamos
 *    um "pool" — um conjunto de conexões prontas que são emprestadas e devolvidas.
 *
 * 2) POOL DE CONEXÕES (HikariCP): mantém várias conexões abertas e reaproveitáveis.
 *    Quando o código precisa falar com o banco, pega uma conexão emprestada do pool;
 *    ao terminar, ela volta para o pool em vez de ser destruída.
 *
 * A inicialização acontece no "bloco static" (executado uma vez, quando a classe é
 * carregada pela JVM). Chamadas seguintes reutilizam a mesma instância já criada.
 */
public class DatabaseConfig {

    // Campo estático que guarda a ÚNICA instância do pool (coração do padrão Singleton).
    private static HikariDataSource dataSource;

    // Construtor privado: impede que alguém crie um objeto "new DatabaseConfig()".
    // O acesso é feito apenas pelos métodos estáticos abaixo.
    private DatabaseConfig() {}

    // Carrega o arquivo .env e lê dele os dados de acesso ao banco.
    // Manter esses dados fora do código (em .env) é uma boa prática de segurança.
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");           // Endereço do banco.
    private static final String USER = dotenv.get("DB_USER");         // Usuário do banco.
    private static final String PASSWORD = dotenv.get("DB_PASSWORD"); // Senha do banco.

    // Bloco "static": roda automaticamente UMA única vez, quando a classe é carregada.
    // É aqui que o pool de conexões é montado e configurado.
    static {
        try {
            // Objeto que reúne todas as configurações do pool.
            HikariConfig config = new HikariConfig();

            // Define onde está o banco e como se autenticar nele.
            config.setJdbcUrl(URL);
            config.setUsername(USER);
            config.setPassword(PASSWORD);

            // No máximo 10 conexões simultâneas abertas ao mesmo tempo.
            config.setMaximumPoolSize(10);

            // Mantém pelo menos 2 conexões "de prontidão" (ociosas), prontas para uso imediato.
            config.setMinimumIdle(2);

            // Se nenhuma conexão estiver livre, espera no máximo 30 segundos (30000 ms)
            // antes de desistir e lançar erro.
            config.setConnectionTimeout(30000);

            // Conexões paradas (sem uso) por mais de 10 minutos são fechadas,
            // respeitando o mínimo definido em setMinimumIdle.
            config.setIdleTimeout(600000);

            // Toda conexão é renovada após 30 minutos de vida, evitando conexões "velhas".
            config.setMaxLifetime(1800000);

            // Cria efetivamente o pool com todas as configurações acima.
            dataSource = new HikariDataSource(config);

            System.out.println("HikariCP Pool inicializado com sucesso!");
        } catch (Exception e) {
            // Se algo der errado na criação do pool, mostra o erro no console.
            System.out.println("Erro ao inicializar HikariCP: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Empresta uma conexão do pool para quem precisar falar com o banco.
     *
     * IMPORTANTE: quem chama deve fechar a conexão depois de usar (geralmente com
     * try-with-resources). "Fechar" aqui significa DEVOLVER a conexão ao pool, não destruí-la.
     *
     * @return uma conexão pronta para uso, ou null se houver erro.
     */
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

    /**
     * Fecha o pool inteiro de conexões. Deve ser chamado ao encerrar a aplicação,
     * para liberar todos os recursos de banco que estavam abertos.
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Pool de conexões fechado!");
        }
    }
}
