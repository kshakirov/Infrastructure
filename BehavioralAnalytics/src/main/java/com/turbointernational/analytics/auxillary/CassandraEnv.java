package com.turbointernational.analytics.auxillary;

/**
 * Created by kshakirov on 6/27/17.
 */
public enum CassandraEnv {
    DEVELOPMENT,
    STAGING,
    PRODUCTION;


    public String keySpace() {
        return "turbo_" + super.toString().toLowerCase();
    }

    public String[] hosts() {
        switch (this) {
            case DEVELOPMENT:
                return new String[]{"10.1.3.15", "10.1.3.16", "10.1.3.17"};
            default:
                return new String[]{"10.8.0.6", "10.8.0.9", "10.8.0.16"};
        }
    }

    public String[] elsticHosts() {
        switch (this) {
            case DEVELOPMENT:
                return new String[]{"10.1.3.15", "10.1.3.16", "10.1.3.17"};
            default:
                return new String[]{"10.8.0.6", "10.8.0.9", "10.8.0.16"};
        }
    }

    public String elasticIndex(){
        return  super.toString().toLowerCase() + "_turbo_analytics";
    }

}
