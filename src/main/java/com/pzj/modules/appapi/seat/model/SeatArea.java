package com.pzj.modules.appapi.seat.model;

import java.util.ArrayList;
import java.util.List;

public class SeatArea {
    private boolean isMultiArea = false; // 是否是多区域座位图
    private String  area;
    private String  areaDesc;
    private String  image;
    private Integer rows;
    private Integer seatCount;
    private String  playId;
    List<RowSeats>  rowSeatList;

    public boolean isMultiArea() {
        return isMultiArea;
    }

    public void setMultiArea(boolean isMultiArea) {
        this.isMultiArea = isMultiArea;
    }

    public String getPlayId() {
        return playId;
    }

    public void setPlayId(String playId) {
        this.playId = playId;
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }

    public String getAreaDesc() {
        return areaDesc;
    }

    public void setAreaDesc(String areaDesc) {
        this.areaDesc = areaDesc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public List<RowSeats> getRowSeatList() {
        if (rowSeatList == null)
            rowSeatList = new ArrayList<RowSeats>();
        return rowSeatList;
    }

    public void setRowSeatList(List<RowSeats> rowSeatList) {
        this.rowSeatList = rowSeatList;
    }

}
