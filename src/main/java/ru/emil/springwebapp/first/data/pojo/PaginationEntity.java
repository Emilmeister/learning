package ru.emil.springwebapp.first.data.pojo;

import java.util.List;

public class PaginationEntity<T>{
    private List<T> data;
    private int total;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public PaginationEntity(List<T> data, int total) {
        this.data = data;
        this.total = total;
    }
}
