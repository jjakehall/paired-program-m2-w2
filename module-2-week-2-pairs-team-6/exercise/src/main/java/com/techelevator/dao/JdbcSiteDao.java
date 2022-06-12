package com.techelevator.dao;

import com.techelevator.model.Site;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcSiteDao implements SiteDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcSiteDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Site> getSitesThatAllowRVs(int parkId) {
        List<Site>sites=new ArrayList<>();
        String sql="SELECT site_id,A.campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities FROM site A JOIN campground B ON A.campground_id=B.campground_id WHERE max_rv_length!=0 AND park_id=?;";
        SqlRowSet results=jdbcTemplate.queryForRowSet(sql,parkId);
        while(results.next()){
            Site site=mapRowToSite(results);
            sites.add(site);
        }
        return sites;
    }

    @Override
    public List<Site> getAvailableSites(int parkId) {
        List<Site> sites = new ArrayList<>();
        String sql = "SELECT site.site_id, site.campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities FROM site " +
                "JOIN reservation ON site.site_id = reservation.site_id " +
                "JOIN campground ON site.campground_id = campground.campground_id " +
                "WHERE park_id = ? AND current_date NOT BETWEEN from_date AND to_date;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while(results.next()) {
            Site site = mapRowToSite(results);
            sites.add(site);
        }
        return sites;
    }

    private Site mapRowToSite(SqlRowSet results) {
        Site site = new Site();
        site.setSiteId(results.getInt("site_id"));
        site.setCampgroundId(results.getInt("campground_id"));
        site.setSiteNumber(results.getInt("site_number"));
        site.setMaxOccupancy(results.getInt("max_occupancy"));
        site.setAccessible(results.getBoolean("accessible"));
        site.setMaxRvLength(results.getInt("max_rv_length"));
        site.setUtilities(results.getBoolean("utilities"));
        return site;
    }
}
