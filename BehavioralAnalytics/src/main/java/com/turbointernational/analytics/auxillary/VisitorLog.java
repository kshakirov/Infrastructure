package com.turbointernational.analytics.auxillary;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

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
    private Date date;
    private UUID id;


    public Long getVisitorId() {
        return visitorId;
    }

    public UUID getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }
}
