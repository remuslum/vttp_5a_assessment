package vttp.batch5.paf.movies.util.SQL;

public class MySQLQueries {
    public static final String MYSQL_TABLE="imdb";
    public static final String MYSQL_COUNT="count";

    public static final String INSERT_INTO_TABLE_IMDB=
    """
        INSERT INTO imdb(imdb_id,vote_average,vote_count,release_date,revenue,budget,runtime)
        VALUES (?,?,?,?,?,?,?);        
    """;

    public static final String SELECT_ALL_FIELDS=
    """
        SELECT COUNT(*) AS count FROM imdb;        
    """;

    public static final String FIND_PROFIT=
    """
        SELECT SUM(revenue) AS revenue_sum, SUM(budget) AS budget_sum
        FROM imdb WHERE imdb_id = ?;        
    """;
}
