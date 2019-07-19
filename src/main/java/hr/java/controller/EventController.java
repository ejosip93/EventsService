package hr.java.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hr.java.dao.EventRepository;
import hr.java.model.Event;

@RestController
public class EventController {
	@Autowired
	EventRepository eventRepository;
	
	@GetMapping(path="/events", produces="application/json")
	public List<Event> getAllEvents(){
		return eventRepository.findAll();
	}
	
	@PostMapping("/event/add")
	public ResponseEntity<HttpStatus> addNewEvent(@RequestBody Event e, Principal p){
		try {
			e.setUser(p.getName());
			eventRepository.save(e);
			return new ResponseEntity<HttpStatus>(HttpStatus.CREATED);
		}
		catch(Exception ex) {
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/event/update")
	public ResponseEntity<HttpStatus> updateEvent(@RequestBody Event uEvent, Principal p){
		try {
			Event event = eventRepository.findById(uEvent.getId()).get();
			event.setUser(p.getName());
			event.setDescription(uEvent.getDescription());
			event.setEndTime(uEvent.getEndTime());
			event.setStartTime(uEvent.getStartTime());
			eventRepository.save(event);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}
		catch(Exception ex) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/events/range")
	public List<Event> getEventsInRange(@RequestParam("firstDate") @DateTimeFormat(pattern="yyyyMMdd") Date firstDate, @RequestParam("secondDate") @DateTimeFormat(pattern="yyyyMMdd") Date secondDate){
		return eventRepository.findAllByStartTimeBetween(firstDate, secondDate);	
	}
	
	@GetMapping("/events/range/count")
	public Long getEventsCountInRange(@RequestParam("firstDate") @DateTimeFormat(pattern="yyyyMMdd") Date firstDate, @RequestParam("secondDate") @DateTimeFormat(pattern="yyyyMMdd") Date secondDate){
		return eventRepository.findAllByStartTimeBetween(firstDate, secondDate).stream().count();	
	}
	
	@GetMapping("/events/range/sqlcount")
	public Long getEventsCountInRangeSql(@RequestParam("firstDate") @DateTimeFormat(pattern="yyyyMMdd") Date firstDate, @RequestParam("secondDate") @DateTimeFormat(pattern="yyyyMMdd") Date secondDate){
		return eventRepository.countEventsInRange(firstDate, secondDate);	
	}
	
	@GetMapping("/events/range/duration")
	public Double getEventsAverageDurationInRange(@RequestParam("firstDate") @DateTimeFormat(pattern="yyyyMMdd") Date firstDate, @RequestParam("secondDate") @DateTimeFormat(pattern="yyyyMMdd") Date secondDate){
		try{
			return Math.round(getEventsDurationAverage(firstDate, secondDate, 60000L) * 1000d) / 1000d;
		}
		catch(NoSuchElementException e){
			return 0d;
		}
	}
	
	@GetMapping("/events/range/sqlduration")
	public Double getEventsAverageDurationInRangeSql(@RequestParam("firstDate") @DateTimeFormat(pattern="yyyyMMdd") Date firstDate, @RequestParam("secondDate") @DateTimeFormat(pattern="yyyyMMdd") Date secondDate){
		return Math.round(eventRepository.eventsAverageDurationInRange(firstDate, secondDate) * 1000d) / 1000d;
	}
	
	@GetMapping("/events/range/utilization")
	public Double getEventsHourUtilizationInRange(@RequestParam("firstDate") @DateTimeFormat(pattern="yyyyMMdd") Date firstDate, @RequestParam("secondDate") @DateTimeFormat(pattern="yyyyMMdd") Date secondDate){
		return Math.round(getEventsDurationSum(firstDate, secondDate, 3600000d)/timeDifference(secondDate.getTime(), firstDate.getTime(),3600000d) * 1000d) / 1000d;
	}
	
	@GetMapping("/events/range/sqlutilization")
	public Double getEventsHourUtilizationInRangeSql(@RequestParam("firstDate") @DateTimeFormat(pattern="yyyyMMdd") Date firstDate, @RequestParam("secondDate") @DateTimeFormat(pattern="yyyyMMdd") Date secondDate){
		return Math.round(eventRepository.eventsHourUtilizationInRange(firstDate, secondDate) * 1000d) / 1000d;
	}
	
	private Double getEventsDurationAverage(Date firstDate, Date secondDate, double convRate){
		return eventRepository.findAllByStartTimeBetween(firstDate, secondDate).stream().mapToDouble(i -> timeDifference((double)i.getEndTime().getTime(), (double)i.getStartTime().getTime(), convRate)).average().getAsDouble();
	}
	
	private Double getEventsDurationSum(Date firstDate, Date secondDate, double convRate){
		return eventRepository.findAllByStartTimeBetween(firstDate, secondDate).stream().mapToDouble(i -> timeDifference((double)i.getEndTime().getTime(), (double)i.getStartTime().getTime(), convRate)).sum();
	}
	
	private Double timeDifference(double firstTime, double secondTime, double convRate){
		return (firstTime - secondTime)/convRate;
	}
	
}
