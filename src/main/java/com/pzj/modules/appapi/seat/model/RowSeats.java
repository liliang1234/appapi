package com.pzj.modules.appapi.seat.model;

import java.util.ArrayList;
import java.util.List;

public class RowSeats {
    private int        index;
    private String     rowNum;
    private List<Seat> list;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getRowNum() {
        return rowNum;
    }

    public void setRowNum(String rowNum) {
        this.rowNum = rowNum;
    }

    public List<Seat> getList() {
        if (list == null)
            list = new ArrayList<Seat>();
        return list;
    }

    public void setList(List<Seat> list) {
        this.list = list;
    }

}
