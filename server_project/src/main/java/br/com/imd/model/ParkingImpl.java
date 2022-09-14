package br.com.imd.model;

import java.time.ZonedDateTime;

public class ParkingImpl {


    public static Integer createTaxa(Integer hours_init, Integer hours_current) {

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
}
