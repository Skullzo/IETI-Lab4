package org.adaschool.tdd;
import org.adaschool.tdd.controller.weather.dto.NearByWeatherReportsQueryDto;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.WeatherReport;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Date;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
public class WeatherReportControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    WeatherReportRepository repository;

    @Test
    public void greetingShouldReturnDefaultMessage()
            throws Exception
    {
        assertThat(
                this.restTemplate.getForObject( "http://localhost:" + port + "/v1/health", String.class ) ).contains(
                "API Working OK!" );
    }

    /*
    @Test
    public void weatherControllerPostTest()
            throws Exception
    {
        double latitude = 4.7110;
        double longitude = 74.0721;
        GeoLocation location = new GeoLocation( latitude, longitude );
        WeatherReportDto dto=new WeatherReportDto( location, 35f, 22f, "User", new Date() );
        ResponseEntity<WeatherReportDto> request = restTemplate.postForEntity("http://localhost/:" + port + "/v1/weather",dto,WeatherReportDto.class);
        Assertions.assertEquals(request.getBody().getReporter(),"User");
    }
    */

    @Test
    public void weatherFindNearByReportsPostTest() throws Exception {

        List<WeatherReport> weatherReports = new ArrayList<WeatherReport>();

        weatherReports.add(new WeatherReport( new GeoLocation( 0, 0 ), 35f, 22f, "1", new Date() ));
        weatherReports.add(new WeatherReport( new GeoLocation( 5, 7 ), 35f, 22f, "2", new Date() ));

        when( repository.findAll()).thenReturn( weatherReports );
        NearByWeatherReportsQueryDto nQuery=new NearByWeatherReportsQueryDto(new GeoLocation(3,4),5);
        assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/v1/weather/nearby", nQuery, List.class).size()).isEqualTo(2);
    }

    @Test
    public void weatherFindByIdGetTest() throws Exception {
        String weatherReportId="sample";
        WeatherReport weatherReport = new WeatherReport(new GeoLocation(0, 0), 35f, 22f, "Sample", new Date());
        when( repository.findById( weatherReportId ) ).thenReturn( Optional.of( weatherReport ) );
        WeatherReport weatherReports = this.restTemplate.getForObject("http://localhost:" + port + "/v1/weather/sample", WeatherReport.class);
        Assertions.assertEquals( weatherReport.getReporter(), weatherReports.getReporter() );
    }

    @Test
    public void weatherFindByReporterIdGetTest() throws Exception {
        double latitude = 4.7110;
        double length = 74.0721;
        GeoLocation location = new GeoLocation( latitude, length );
        List<WeatherReport> weatherReports = new ArrayList<>();
        weatherReports.add(new WeatherReport( location, 35f, 22f, "sample", new Date() ));
        when( repository.findByReporter("sample") ).thenReturn( weatherReports );
        List<WeatherReport> weatherReport = this.restTemplate.getForObject("http://localhost:" + port + "/v1/weather/reporter/sample", List.class);
        Assertions.assertEquals(weatherReport.get(0).getReporter(), weatherReports.get(0).getReporter());
    }
}