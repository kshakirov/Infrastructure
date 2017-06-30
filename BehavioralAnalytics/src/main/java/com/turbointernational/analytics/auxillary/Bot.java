package com.turbointernational.analytics.auxillary;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

/**
 * Created by kshakirov on 6/30/17.
 */
@Table(name = "bots")
public class Bot {
    @PartitionKey
    @Column(name = "id")
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
