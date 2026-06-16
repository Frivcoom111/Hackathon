// Pacote "dao": guarda as classes de acesso a dados (Data Access Object).
package com.portal.dao;

import com.portal.config.DatabaseConfig; // De onde vêm as conexões com o banco.
import com.portal.model.Address;         // Entidade montada pelo método mapAddress.

import java.sql.Connection;       // Representa uma conexão com o banco.
import java.sql.ResultSet;        // Representa o resultado (linhas) de uma consulta SQL.
import java.sql.SQLException;     // Exceção lançada em erros de SQL.
import java.sql.Timestamp;        // Tipo de data/hora compatível com o banco.
import java.time.LocalDateTime;   // Data/hora do Java.
import java.time.ZoneId;          // Representa um fuso horário.

/**
 * BaseDAO: classe-mãe (base) de todos os DAOs do sistema.
 *
 * O QUE É UM DAO (Data Access Object)? É a camada responsável por CONVERSAR com o
 * banco de dados: executar consultas (SELECT), inserções (INSERT), atualizações
 * (UPDATE) etc. Cada entidade costuma ter seu próprio DAO (UserDAO, JobDAO...).
 *
 * Esta classe é "abstract" (abstrata): não pode ser instanciada diretamente; serve
 * apenas para ser HERDADA pelos DAOs concretos, reunindo o código comum a todos eles:
 *   - obter uma conexão do pool;
 *   - gerar a data/hora atual no fuso do Brasil;
 *   - montar um objeto Address a partir de um resultado do banco.
 */
public abstract class BaseDAO {

    // Fuso horário de São Paulo, usado para gravar datas no horário correto do Brasil.
    private static final ZoneId FUSO_BRASIL = ZoneId.of("America/Sao_Paulo");

    /**
     * Devolve uma conexão emprestada do pool (HikariCP), pronta para executar SQL.
     * É "protected" para que apenas as subclasses (os DAOs) possam usá-la.
     */
    protected Connection getConnection() {
        return DatabaseConfig.getConnection();
    }

    /** Devolve a data/hora atual no fuso de São Paulo, já no formato Timestamp do banco. */
    protected Timestamp now() {
        return Timestamp.valueOf(LocalDateTime.now(FUSO_BRASIL));
    }

    /**
     * Monta um objeto Address a partir das colunas de um ResultSet (resultado de consulta).
     *
     * "Mapear" significa transformar uma LINHA do banco em um OBJETO Java. Aqui pegamos
     * as colunas addressId, street, number... e montamos um Address.
     *
     * @return o Address preenchido, ou null quando addressId for nulo (ou seja, quando
     *         a consulta usou LEFT JOIN e aquele registro não tinha endereço associado).
     */
    protected Address mapAddress(ResultSet rs) throws SQLException {
        String addressId = rs.getString("addressId");
        if (addressId == null) return null; // Sem endereço: devolve null.
        return new Address(
            addressId,
            rs.getString("street"),
            rs.getString("number"),
            rs.getString("complement"),
            rs.getString("district"),
            rs.getString("city"),
            rs.getString("state"),
            rs.getString("zipCode")
        );
    }
}
