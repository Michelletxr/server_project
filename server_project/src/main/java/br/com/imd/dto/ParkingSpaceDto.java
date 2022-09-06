package br.com.imd.dto;
import br.com.imd.dao.ParkingSpaceDao;
import br.com.imd.factory.ConnetionFactory;
import br.com.imd.model.ParkingSpace;
import com.google.common.base.Splitter;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;


public class ParkingSpaceDto {

    public static Pair<String, ParkingSpace> convertStringToData(String str){

        String dados = str.replaceAll("[^A-Za-z0-9:,]", "");
        Map<String, String> properties = Splitter.on(",")
                .withKeyValueSeparator(":")
                .split(dados);
        ParkingSpace parkingSpace = new ParkingSpace();
        if(properties.containsKey("horas")){
            Integer hours = Integer.parseInt(properties.get("horas"));
            parkingSpace.setHours(hours);
            parkingSpace.setValue(ParkingSpace.taxaPorHora * hours );
        }
        if(properties.containsKey("id")){
            Integer id = Integer.parseInt(properties.get("id"));
            parkingSpace.setId(id);
        }
        parkingSpace.setLicensePlate(properties.get("placa"));
        System.out.println("parking " + properties);
        return new Pair<>(properties.get("option"), parkingSpace);
    }
}
