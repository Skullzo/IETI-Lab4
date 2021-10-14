package org.adaschool.tdd;

import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.adaschool.tdd.service.MongoWeatherService;
import org.adaschool.tdd.service.WeatherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
class MongoWeatherServiceTest
{
    WeatherService weatherService;

    @Mock
    WeatherReportRepository repository;

    @BeforeAll()
    public void setup()
    {
        weatherService = new MongoWeatherService( repository );
    }

    @Test
    void createWeatherReportCallsSaveOnRepository()
    {
        weatherService = new MongoWeatherService( repository );
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReportDto weatherReportDto = new WeatherReportDto( location, 35f, 22f, "tester", new Date() );
        weatherService.report( weatherReportDto );
        verify( repository ).save( any( WeatherReport.class ) );
    }

    @Test
    void weatherReportIdFoundTest()
    {
        weatherService = new MongoWeatherService( repository );
        String weatherReportId = "awae-asd45-1dsad";
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "tester", new Date() );
        when( repository.findById( weatherReportId ) ).thenReturn( Optional.of( weatherReport ) );
        WeatherReport foundWeatherReport = weatherService.findById( weatherReportId );
        Assertions.assertEquals( weatherReport, foundWeatherReport );
    }

    @Test
    void weatherReportIdNotFoundTest()
    {
        weatherService = new MongoWeatherService( repository );
        String weatherReportId = "dsawe1fasdasdoooq123";
        when( repository.findById( weatherReportId ) ).thenReturn( Optional.empty() );
        Assertions.assertThrows( WeatherReportNotFoundException.class, () -> {
            weatherService.findById( weatherReportId );
        } );
    }

    @Test
    void findWeatherReportsByNameTest(){
        String User = "User";
        double latitude = 4.7110;
        double longitude = 74.0721;
        List<WeatherReport> weatherReportsByNameList=new ArrayList<>();
        GeoLocation location = new GeoLocation( latitude, longitude );
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "User", new Date() );
        weatherReportsByNameList.add(weatherReport);
        when( repository.findByReporter( User ) ).thenReturn((weatherReportsByNameList));
        List<WeatherReport> foundWeatherReport = weatherService.findWeatherReportsByName( User );
        Assertions.assertEquals( weatherReportsByNameList, foundWeatherReport );
    }

    @Test
    void weatherReportNearLocationTest()
    {
        weatherService = new MongoWeatherService( repository );
        String weatherReportId = "awae-asd45-1dsad";
        List<WeatherReport> WeatherReportList=new ArrayList<WeatherReport>();
        WeatherReportList.add(new WeatherReport( new GeoLocation( 0, 0 ), 35f, 22f, "tester", new Date() ));
        WeatherReportList.add(new WeatherReport( new GeoLocation( 5, 7 ), 35f, 22f, "tester2", new Date() ));
        WeatherReportList.add(new WeatherReport( new GeoLocation( 5, -1.5 ), 35f, 22f, "tester3", new Date() ));
        when( repository.findAll()).thenReturn( WeatherReportList );
        List<WeatherReport> weatherReport = weatherService.findNearLocation(new GeoLocation(3,4),5);
        Assertions.assertEquals( weatherReport.size(), 2 );
    }
}
