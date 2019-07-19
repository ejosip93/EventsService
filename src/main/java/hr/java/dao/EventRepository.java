package hr.java.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import hr.java.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>{
	List<Event> findAllByStartTimeBetween(Date timeStart, Date timeEnd);
	
	@Query(value = "SELECT count(*) FROM event WHERE start_time BETWEEN ?1 AND ?2", nativeQuery = true)
    Long countEventsInRange(Date timeStart, Date timeEnd);
	
	@Query(value = "SELECT IFNULL(AVG(TIMESTAMPDIFF(minute, start_time, end_time)), 0) FROM event WHERE start_time BETWEEN ?1 AND ?2", nativeQuery = true)
	Double eventsAverageDurationInRange(Date timeStart, Date timeEnd);
	
	@Query(value = "SELECT IFNULL(SUM(TIMESTAMPDIFF(minute, start_time, end_time))/60/TIMESTAMPDIFF(hour, ?1, ?2), 0) FROM event WHERE start_time BETWEEN ?1 AND ?2", nativeQuery = true)
	Double eventsHourUtilizationInRange(Date timeStart, Date timeEnd);
}
