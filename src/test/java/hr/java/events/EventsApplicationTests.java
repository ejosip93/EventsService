package hr.java.events;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hr.java.controller.EventController;
import hr.java.dao.EventRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventsApplicationTests {

	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	EventController eventController;
	
	@Test
	public void contextLoads() {
	}

	@Test
	public void countEventsTest() throws ParseException{
		Date firstDate = new SimpleDateFormat("yyyy-MM-dd").parse("2019-04-07");
		Date secondDate = new SimpleDateFormat("yyyy-MM-dd").parse("2019-04-08");
		assertEquals(eventRepository.countEventsInRange(firstDate, secondDate), eventController.getEventsCountInRange(firstDate, secondDate));	
	}
	
	@Test
	public void countAvarageDurationTest() throws ParseException{
		Date firstDate = new SimpleDateFormat("yyyy-MM-dd").parse("2019-04-07");
		Date secondDate = new SimpleDateFormat("yyyy-MM-dd").parse("2019-04-08");		
		assertEquals(Math.round(eventRepository.eventsAverageDurationInRange(firstDate, secondDate) * 100) / 100, Math.round(eventController.getEventsAverageDurationInRange(firstDate, secondDate) * 100) / 100);	
	}
	
	@Test
	public void countUtilizationTest() throws ParseException{
		Date firstDate = new SimpleDateFormat("yyyy-MM-dd").parse("2019-04-07");
		Date secondDate = new SimpleDateFormat("yyyy-MM-dd").parse("2019-04-08");		
		assertEquals(Math.round(eventRepository.eventsHourUtilizationInRange(firstDate, secondDate) * 100) / 100, Math.round(eventController.getEventsHourUtilizationInRange(firstDate, secondDate) * 100) / 100);	
	}
}
