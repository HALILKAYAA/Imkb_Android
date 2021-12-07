package com.example.imkb.Models.ListModel;

import com.example.imkb.Models.HandshakeModel.Status;

import java.util.ArrayList;

public class ListModel {
   private  ArrayList<Stocks> stocks;
   private  Status status;

    public ArrayList<Stocks> getStocks() {
        return stocks;
    }

    public void setStocks(ArrayList<Stocks> stocks) {
        this.stocks = stocks;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
