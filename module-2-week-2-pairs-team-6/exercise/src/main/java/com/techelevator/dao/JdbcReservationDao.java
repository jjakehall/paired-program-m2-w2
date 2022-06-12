package com.techelevator.dao;

import com.techelevator.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcReservationDao implements ReservationDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcReservationDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {
        String sql="INSERT INTO reservation (site_id, name, from_date, to_date, create_date) "+
                "VALUES (?,?,?,?,?) RETURNING reservation_id;";
        Integer newId=jdbcTemplate.queryForObject(sql,Integer.class,siteId,name,fromDate,toDate,LocalDate.now());
        return newId;
    }

    @Override
    public List<Reservation> viewUpcomingReservations(int parkId){
        List<Reservation>upcomingReservations=new ArrayList<>();
        String sql ="SELECT A.reservation_id, A.site_id, A.name, A.from_date, A.to_date, A.create_date FROM reservation A JOIN site B ON A.site_id=B.site_id " +
                "JOIN campground C ON B.campground_id=C.campground_id WHERE park_id=? AND from_date BETWEEN ? AND ?;";
        SqlRowSet results=jdbcTemplate.queryForRowSet(sql,parkId,LocalDate.now(),LocalDate.now().plusDays(30));
        while(results.next()){
            Reservation reservation=mapRowToReservation(results);
            upcomingReservations.add(reservation);
        }
        return upcomingReservations;
    }

    private Reservation mapRowToReservation(SqlRowSet results) {
        Reservation r = new Reservation();
        r.setReservationId(results.getInt("reservation_id"));
        r.setSiteId(results.getInt("site_id"));
        r.setName(results.getString("name"));
        r.setFromDate(results.getDate("from_date").toLocalDate());
        r.setToDate(results.getDate("to_date").toLocalDate());
        r.setCreateDate(results.getDate("create_date").toLocalDate());
        return r;
    }


}
