package com.turbointernational.analytics.auxillary;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

/**
 * Created by kshakirov on 6/30/17.
 */
@Table(name="product_ranks")
public class ProductRank {
    @PartitionKey
    @Column(name="sku")
    private Long sku;
    @Column(name="times")
    private Long times;

    public Long getSku() {
        return sku;
    }

    public void setSku(Long sku) {
        this.sku = sku;
    }

    public Long getTimes() {
        return times;
    }

    public void setTimes(Long times) {
        this.times = times;
    }
}
