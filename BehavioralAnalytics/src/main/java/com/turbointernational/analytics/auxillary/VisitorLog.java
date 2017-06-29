package com.turbointernational.analytics.auxillary;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

/**
 * Created by kshakirov on 6/26/17.
 */

@Table(keyspace = "", name = "visitor_logs")
public class VisitorLog {
    @PartitionKey
    @Column(name = "visitor_id")
    private Long visitorId;
    @PartitionKey(1)
    private Date date;
    @PartitionKey(2)
    private UUID id;
    @PartitionKey(3)
    private InetAddress ip;
    private Long customer_id;
    private Long product;


    public Long getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(Long visitorId) {
        this.visitorId = visitorId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Long customer_id) {
        this.customer_id = customer_id;
    }

    public Long getProduct() {
        return product;
    }

    public void setProduct(Long product) {
        this.product = product;
    }
}
