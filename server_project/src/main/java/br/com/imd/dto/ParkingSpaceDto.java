package br.com.imd.dto;
import br.com.imd.model.ParkingSpace;
import br.com.imd.model.ResponseMsg;
import br.com.imd.model.UserLogin;
import com.google.common.base.Splitter;

import java.util.Map;


public class ParkingSpaceDto {

    public static String getIdToString(String str){
        String dados = convertStringToMsg(str);
        String id = convertMsgToProprietes(dados).get("id");
        return id;
    }
    public static String getTokenToString(String str ){
        String dados = str.replaceAll("[^A-Za-z0-9:,._-]", "");
        String token = convertMsgToProprietes(dados).get("token");
        System.out.println("TOKEN DTO: " + dados);
        return token;
    }

    public static String getTargetToString(String str){
        String msg = convertStringToMsg(str);
        String target = convertMsgToProprietes(msg).get("target");
       return target;
    }
    public static String convertStringToMsg(String str){
        String dados = str.replaceAll("[^A-Za-z0-9:,]", "");
        return dados;
    }

    public static  Map<String, String> convertMsgToProprietes(String str){
        Map<String, String> properties = Splitter.on(",")
                .withKeyValueSeparator(":")
                .split(str);
        return properties;
    }
    public static ParkingSpace convertRequestToParkingSpace(String str){
        String dados = convertStringToMsg(str);
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
        String dados = convertStringToMsg(str);
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

    public static  String generateResponseObj( String target, String action, String data){
        ResponseMsg response = new ResponseMsg(target, action, data);
        return response.toString();
    }

    public static UserLogin convertStringToUser(String str){
        String dados = convertStringToMsg(str);
        Map<String, String> properties = Splitter.on(",")
                .withKeyValueSeparator(":")
                .split(dados);
        UserLogin user = new UserLogin();
        if(properties.containsKey("login")){
            user.setLogin(properties.get("login"));
        }
        if(properties.containsKey("password")){
            user.setPassword(properties.get("password"));
        }
        return user;
    }
}
