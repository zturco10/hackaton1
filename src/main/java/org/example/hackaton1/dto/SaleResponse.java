package org.example.hackaton1.dto;


import java.time.LocalDateTime;

public class SaleResponse {
    private String id;
    private String sku;
    private int units;
    private double price;
    private String branch;
    private LocalDateTime soldAt;
    private String createdBy;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public LocalDateTime getSoldAt() { return soldAt; }
    public void setSoldAt(LocalDateTime soldAt) { this.soldAt = soldAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}