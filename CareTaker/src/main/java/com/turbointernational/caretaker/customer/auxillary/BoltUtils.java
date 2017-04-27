package com.turbointernational.caretaker.customer.auxillary;

/**
 * Created by kshakirov on 4/13/17.
 */
public class BoltUtils {
    public static  boolean isForgottenPassword (String streamId){

        return streamId.equalsIgnoreCase("forgottenPassword");
    }
    public static boolean isNewUser(String streamId){
        return streamId.equalsIgnoreCase("newUser");
    }

    public static boolean isOrder(String streamId){
        return streamId.equalsIgnoreCase("order");
    }
}
