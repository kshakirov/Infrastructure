package com.turbointernational.analytics.auxillary;

/**
 * Created by kshakirov on 6/23/17.
 */
public class BoltUtils {
    public static boolean isCustomerLog(String type){
        return type.equalsIgnoreCase(CassandraTable.CUSTOMER_LOG.tableize());
    }

    public static boolean isVisitorLog(String type){
        return type.equalsIgnoreCase(CassandraTable.VISITOR_LOG.tableize());
    }
}
