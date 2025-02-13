package vttp.batch5.paf.movies.models;

public class Director implements Comparable<Director>{
    private String name;
    private int movies;
    public Director(String name, int movies) {
        this.name = name;
        this.movies = movies;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getMovies() {
        return movies;
    }
    public void setMovies(int movies) {
        this.movies = movies;
    }
    @Override
    public int compareTo(Director o) {
        if(this.getMovies() > o.getMovies()){
            return 1;
        } else if(this.getMovies() < o.getMovies()){
            return -1;
        } else {
            return 0;
        }
    }
        

    
}
