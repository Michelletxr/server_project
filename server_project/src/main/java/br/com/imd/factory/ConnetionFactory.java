package br.com.imd.factory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.*;
public class ConnetionFactory {
    private static DataSource dataSource;


    private ConnetionFactory() {
        ComboPooledDataSource pool = new ComboPooledDataSource();
        pool.setJdbcUrl( "jdbc:postgresql://localhost:5432/parking_db");
        pool.setUser("postgres");
        pool.setPassword("postgres");
        pool.setMaxPoolSize(20);
        this.dataSource = pool;
    }

    public static Connection getConnetion() throws SQLException {

        if(dataSource == null){
            new ConnetionFactory();
        }
        System.out.println("conexão estabelecida com sucesso!");
        return dataSource.getConnection();
    }
}
