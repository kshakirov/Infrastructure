package com.turbointernational.behavioralAnalytics.auxillary;

/**
 * Created by kshakirov on 6/23/17.
 */
public class BoltUtils {
    public static boolean isCustomerLog(String streamId){
        return streamId.equalsIgnoreCase("customerLog");
    }
}
