package org.adaschool.tdd.service;
import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class MongoWeatherService
    implements WeatherService
{

    private final WeatherReportRepository repository;

    public MongoWeatherService( @Autowired WeatherReportRepository repository )
    {
        this.repository = repository;
    }

    @Override
    public WeatherReport report( WeatherReportDto weatherReportDto )
    {
        WeatherReport weatherReport = new WeatherReport(weatherReportDto);
        return repository.save(weatherReport);
    }

    @Override
    public WeatherReport findById( String id ) {
        Optional<WeatherReport> optional = repository.findById( id );
        if ( optional.isPresent() ){
            return optional.get();
        } else {
            throw new WeatherReportNotFoundException();
        }
    }

    private double calculateNearLocation(double latitude, double length,double other_latitude, double other_length){
        return Math.pow(Math.pow((latitude-other_latitude),2)+Math.pow((length-other_length),2),0.5);
    }

    @Override
    public List<WeatherReport> findNearLocation( GeoLocation geoLocation, float distanceRangeInMeters )
    {
        List<WeatherReport> nearLocation = new ArrayList<>();
        List<WeatherReport> weatherReport = repository.findAll();
        for(WeatherReport location:weatherReport){
            if(calculateNearLocation(location.getGeoLocation().getLat(),location.getGeoLocation().getLng(),geoLocation.getLat(),
                    geoLocation.getLng())<=distanceRangeInMeters){
                nearLocation.add(location);
            }
        }
        return nearLocation;
    }

    @Override
    public List<WeatherReport> findWeatherReportsByName( String reporter )
    {
        return repository.findByReporter(reporter);
    }

}