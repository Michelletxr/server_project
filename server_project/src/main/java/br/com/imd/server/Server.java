package br.com.imd.server;

import br.com.imd.dao.ParkingSpaceDao;
import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.factory.ConnetionFactory;
import br.com.imd.model.ParkingSpace;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Objects;

public abstract class Server {

    private ParkingSpaceDao parkingSpaceDao;
    private Connection connection;

    public abstract  void startServer() throws IOException;

    public abstract void stopServer();

    private static Integer verifyTaxa(Integer hours_init, Integer hours_current) {

        Integer taxa = 0;
        Integer hours_atual = ZonedDateTime.now().getHour();
        Integer total = Math.abs(hours_init - hours_atual);
        if(total < hours_current){
            taxa = (Math.abs(total - hours_current)* ParkingSpace.taxaPorHora) * -1;
        }else{
            taxa = Math.abs(total - hours_current)* ParkingSpace.multaPorHora;
        }
        return taxa;
    }

    public String generateResponse(String request) {
        String response = null;
        Pair<String, ParkingSpace> entry = ParkingSpaceDto.convertStringToData(request);


        try {
            this.connection = ConnetionFactory.getConnetion();
            //connection.setAutoCommit(false);
            this.parkingSpaceDao =  new ParkingSpaceDao(connection);

            if("estacionar".equals(entry.getKey())){
                Integer id = parkingSpaceDao.saveParkingSpace(entry.getValue());
                response = "carro estacionado com sucesso! \n idParkingSpace: " + id;
            }
            if("retirar".equals(entry.getKey())){
                ParkingSpace parkingSpace = parkingSpaceDao.findParkingSpaceById(entry.getValue().getId());
                if(Objects.isNull(parkingSpace)){
                    response = "a vaga informada não existe!";
                }else{

                    parkingSpaceDao.deleteParkingSpace(entry.getValue().getId());
                    response = "carro retirado com sucesso!";
                    Integer taxa = this.verifyTaxa(parkingSpace.getHoursInit(), parkingSpace.getHours());

                    if(taxa<0){
                        response =  response.concat("\n desconto: " + taxa);
                    } else if (taxa > 0) {
                        response = response.concat("\n acrescimo: " + taxa);
                    }
                    response = response.concat("\n total a ser pago: " + (taxa + parkingSpace.getValue()));
                }
            }

            //connection.commit();
            connection.close();

        }catch (SQLException e){
            System.err.println("erro ao tentar estabelecer uma conexão" + e);
        }
        return response;
    }

}
