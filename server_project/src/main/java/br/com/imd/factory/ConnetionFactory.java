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
        pool.setMaxPoolSize(30);
        this.dataSource = pool;
    }

    public static Connection getConnetion()  {
        Connection connection = null;
        try {
            if(dataSource == null){
                new ConnetionFactory();
            }
            System.out.println("conexão estabelecida com sucesso!");
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}
