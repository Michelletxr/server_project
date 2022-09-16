package br.com.imd.dao;

import br.com.imd.factory.ConnetionFactory;
import br.com.imd.model.ParkingSpace;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ParkingSpaceDao {


    private Connection connection;
    public ParkingSpaceDao(){
    }

    public Integer saveParkingSpace(ParkingSpace newParkingSpace ) throws SQLException {

        Integer idParkingSpace = null;
          //  this.connection = ConnetionFactory.getConnetion();
            PreparedStatement stm =  connection.prepareStatement("INSERT INTO parking_space (license_plate," +
                    " hours_total, hours_init, value) VALUES (?, ?, ?,?)", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, newParkingSpace.getLicensePlate()); //inserindo o numero da placa
            stm.setInt(2, newParkingSpace.getHours());
            stm.setInt(3, newParkingSpace.getHoursInit());
            stm.setInt(4, newParkingSpace.getValue());
            stm.execute();


            ResultSet result = stm.getGeneratedKeys();
            while (result.next()){
                idParkingSpace = result.getInt(1);
            }
            result.close();
            stm.close();
           // connection.close();


        return idParkingSpace;
    }
    public boolean deleteParkingSpace(Integer id) throws RuntimeException {

        boolean isDelet = false;
        try{
           // this.connection = ConnetionFactory.getConnetion();
            PreparedStatement stm = connection.prepareStatement("DELETE FROM parking_space WHERE id=?");
            stm.setInt(1, id);
            stm.execute();
            stm.close();
            isDelet = true;
           // connection.close();
        }catch (SQLException e){
           System.out.println("erro ao tentar deletar");
        }
        return isDelet;
    }

    public ParkingSpace findParkingSpaceById(String id) throws SQLException {

        ParkingSpace parkingSpace = null;


           // this.connection = ConnetionFactory.getConnetion();

            PreparedStatement stm = connection.prepareStatement("SELECT * FROM parking_space WHERE id=?");
            stm.setInt(1, Integer.parseInt(id));
            stm.execute();
            ResultSet result = stm.executeQuery();

            while(result.next()){
                parkingSpace = new ParkingSpace();
                parkingSpace.setLicensePlate(result.getString("license_plate"));
                parkingSpace.setHours(result.getInt("hours_total"));
                parkingSpace.setValue(result.getInt("value"));
                parkingSpace.setHoursInit(result.getInt("hours_init"));
            }
            result.close();
            stm.close();
            //connection.close();


        return parkingSpace;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void closeConeetion(){
        try {
            this.connection.close();
        } catch (SQLException e) {
            System.out.println("erro ao tentar fechar conexão");
            throw new RuntimeException(e);
        }
    }

    public List<ParkingSpace> listParkingSpace() {
        ArrayList<ParkingSpace> parkingSpaces = new ArrayList<ParkingSpace>();

        try{
            PreparedStatement stm = connection.prepareStatement("SELECT id, license_plate FROM parking_space");
            stm.execute();
            ResultSet result = stm.getResultSet();

            while (result.next()){
                ParkingSpace parkingSpace = new ParkingSpace();
                parkingSpace.setLicensePlate(result.getString("license_plate"));
                parkingSpace.setId(Integer.parseInt(result.getString("id")));
                parkingSpaces.add(parkingSpace);
            }
        }catch (SQLException e){
            System.err.println("não foi possível realizar a operação de listagem" + e.getStackTrace());
        }

        return parkingSpaces;
    }

    public static void main(String[] args) throws InterruptedException, SQLException {
        ParkingSpaceDao dao = new ParkingSpaceDao();
        ParkingSpace teste = new ParkingSpace();
        teste.setLicensePlate("TESTE123");
        teste.setHours(5);
        teste.setValue(10);
        dao.saveParkingSpace(teste);

        for (int i = 0; i < 25; i++) {
            ConnetionFactory.getConnetion();
            System.out.println("conexão estabelecida");
        }

    }

}



