package br.com.imd.dto;
import br.com.imd.model.ParkingSpace;
import com.google.common.base.Splitter;

import java.util.Map;


public class ParkingSpaceDto {

    public static String getIdToString(String str){
        String id = str.replaceAll("[id option SAVE FIND CREATE REMOVE :,]", "");
        return id;
    }

    public static ParkingSpace convertRequestToData(String str){

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
        return parkingSpace;
    }

    public static ParkingSpace convertStringToParkingSpace(String str){
        String dados = str.replaceAll("[^A-Za-z0-9:,]", "");
        Map<String, String> properties = Splitter.on(",")
                .withKeyValueSeparator(":")
                .split(dados);
        ParkingSpace parkingSpace = new ParkingSpace();
        if(properties.containsKey("hoursinit")){
            Integer hours_init = Integer.parseInt(properties.get("hoursinit"));
            parkingSpace.setHoursInit(hours_init);
        }
        if(properties.containsKey("hourstotal")){
            Integer hours = Integer.parseInt(properties.get("hourstotal"));
            parkingSpace.setHours(hours);
            parkingSpace.setValue(ParkingSpace.taxaPorHora * hours );
        }
        if(properties.containsKey("id")){
            Integer id = Integer.parseInt(properties.get("id"));
            parkingSpace.setId(id);
        }
        parkingSpace.setLicensePlate(properties.get("licensePlate"));
        return parkingSpace;
    }

    public static String convertStringToMsg(String str){
        String dados = str.replaceAll("[^A-Za-z0-9:,]", "");
        return dados;
    }

    public static String formatDataToResponse(String msg, String value){

        return null;
    }
}
