package com.pzj.modules.appapi.seat.model;

import java.util.ArrayList;
import java.util.List;

public class Seat {

    private String seatNum;
    private String style;
    private int    index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(String seatNum) {
        this.seatNum = seatNum;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((seatNum == null) ? 0 : seatNum.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Seat other = (Seat) obj;
        if (seatNum == null) {
            if (other.seatNum != null)
                return false;
        } else if (!seatNum.equals(other.seatNum))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Seat [seatNum=" + seatNum + ", style=" + style + "]";
    }

    public static void main(String[] args) {
        Seat seat = new Seat();
        Seat seat1 = new Seat();
        seat.seatNum = "1";
        seat.style = "a";
        seat1.seatNum = "1";
        seat1.style = "a";
        List<Seat> list = new ArrayList<Seat>();

        list.add(seat);
        if (list.contains(seat1)) {
            int indexOf = list.indexOf(seat1);
            System.out.println(indexOf);
            for (Seat seat2 : list) {
                System.out.println(seat2);
            }
        }

    }
}
