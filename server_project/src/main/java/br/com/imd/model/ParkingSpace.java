package br.com.imd.model;

import java.time.ZonedDateTime;
import java.util.Objects;

public class ParkingSpace {
    private String licensePlate; //placa do carro
    private Integer id;
    private Integer hours; //quantas horas pretende usar a vaga
    private Integer hoursInit; //hora de inicio
    private Integer value;
    public static final Integer taxaPorHora = 2;
    public static final Integer multaPorHora = 3;

    public ParkingSpace() {this.hoursInit = ZonedDateTime.now().getHour();}
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getValue() {
        return value;
    }
    public String getLicensePlate() {
        return licensePlate;
    }
    public Integer getHours() {
        return hours;
    }

    public Integer getHoursInit() { return hoursInit; }
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    public void setValue(Integer value) { this.value = value; }
    public void setHours(Integer hours) { this.hours = hours; }
    public void setHoursInit(Integer hoursInit) { this.hoursInit = hoursInit; }

    @Override
    public String toString() {
        return "ParkingSpace{" +
                "licensePlate='" + licensePlate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingSpace that = (ParkingSpace) o;
        return licensePlate.equals(that.licensePlate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(licensePlate);
    }
}
