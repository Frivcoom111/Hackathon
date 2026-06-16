package com.portal.dao;

import com.portal.config.DatabaseConfig;
import com.portal.model.Address;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Classe base de todos os DAOs. Reúne aqui o que é comum a todos eles:
 * obter conexão, gerar o horário atual e montar um Address vindo do banco.
 */
public abstract class BaseDAO {

    private static final ZoneId FUSO_BRASIL = ZoneId.of("America/Sao_Paulo");

    /** Conexão do pool (HikariCP). */
    protected Connection getConnection() {
        return DatabaseConfig.getConnection();
    }

    /** Data/hora atual no fuso de São Paulo, pronta para gravar no banco. */
    protected Timestamp now() {
        return Timestamp.valueOf(LocalDateTime.now(FUSO_BRASIL));
    }

    /**
     * Monta um Address a partir das colunas do ResultSet (addressId, street, ...).
     * Retorna null quando não há endereço (addressId nulo) — usado por vários DAOs
     * que fazem LEFT JOIN com a tabela Address.
     */
    protected Address mapAddress(ResultSet rs) throws SQLException {
        String addressId = rs.getString("addressId");
        if (addressId == null) return null;
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
