package ru.emil.springwebapp.first.pojo;

public class StocksPagination extends Pagination{

    public Filter filter;

    public StocksPagination() {
        super();
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
