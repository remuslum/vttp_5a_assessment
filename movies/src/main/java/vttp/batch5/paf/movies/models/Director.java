package vttp.batch5.paf.movies.models;

import java.util.List;

public class Director implements Comparable<Director>{
    private String name;
    private List<String> movieIds;
    private double revenue;
    private double budget;

    public Director(String name, List<String> movieIds) {
        this.name = name;
        this.movieIds = movieIds;
        this.revenue = 0.00;
        this.budget = 0.00;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMovieIds() {
        return movieIds;
    }

    public void setMovieIds(List<String> movieIds) {
        this.movieIds = movieIds;
    }

    public int getMovies() {
        return this.getMovieIds().size();
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    @Override
    public int compareTo(Director o) {
        return Integer.compare(this.getMovies(), o.getMovies());
        
    }

    @Override
    public String toString() {
        return String.format(this.getName(), this.getMovies());
    }
        
}
