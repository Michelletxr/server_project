package br.com.imd.dao;

import br.com.imd.factory.ConnetionFactory;
import br.com.imd.model.ParkingSpace;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ParkingSpaceDao {
    private Connection connection;

    public ParkingSpaceDao(Connection connection){
        this.connection = connection;
    }

    public Integer saveParkingSpace(ParkingSpace newParkingSpace ) {

        Integer idParkingSpace = null;
        try{
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
        }catch (SQLException e){
            System.err.println("não foi possível realizar a operação save"+ e.getStackTrace());
        }

        return idParkingSpace;
    }
    public boolean deleteParkingSpace(Integer id){
        boolean isDelet = false;
        try{
            PreparedStatement stm = connection.prepareStatement("DELETE FROM parking_space WHERE id=?");
            stm.setInt(1, id);
            stm.execute();
            isDelet = true;
        }catch (SQLException e){
            System.err.println("não foi possível realizar a operação delete"+ e.getStackTrace());
        }
        return isDelet;
    }

    public ParkingSpace findParkingSpaceById(Integer id){
        ParkingSpace parkingSpace = null;

        try{

            PreparedStatement stm = connection.prepareStatement("SELECT * FROM parking_space WHERE id=?");
            stm.setInt(1, id);
            stm.execute();
            ResultSet result = stm.executeQuery();

            while(result.next()){
                parkingSpace = new ParkingSpace();
                parkingSpace.setLicensePlate(result.getString("license_plate"));
                parkingSpace.setHours(result.getInt("hours_total"));
                parkingSpace.setValue(result.getInt("value"));
                parkingSpace.setHoursInit(result.getInt("hours_init"));
            }

        }catch (SQLException e){
            System.err.println("não foi possível realizar a operação find" + e.getStackTrace());
        }
        return parkingSpace;
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
        ParkingSpaceDao dao = new ParkingSpaceDao(ConnetionFactory.getConnetion());
        ParkingSpace teste = new ParkingSpace();
        teste.setLicensePlate("TESTE123");
        dao.saveParkingSpace(teste);

        for (int i = 0; i < 25; i++) {
            ConnetionFactory.getConnetion();
            System.out.println("conexão estabelecida");
        }

    }

}



