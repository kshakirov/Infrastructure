package com.turbointernational.analytics.auxillary;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import org.elasticsearch.common.settings.Settings;

import java.io.IOException;

/**
 * Created by kshakirov on 6/27/17.
 */
public class ElasticBoltExecutor {

    private JestClient client;
    private String elasticIndex;
    public ElasticBoltExecutor(String elasticHost, String elasticIndex){
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://" + elasticHost + ":9200")
                .multiThreaded(true)
                .defaultMaxTotalConnectionPerRoute(4)
                .maxTotalConnection(4)
                        .build());
        client = factory.getObject();
        this.elasticIndex = elasticIndex;
    }
    public JestResult createIndex() throws IOException {
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put("number_of_shards",5);
        settingsBuilder.put("number_of_replicas",1);

        return  client.execute(new CreateIndex.Builder(elasticIndex).settings(settingsBuilder.build().getAsMap()).build());
    }

    public void indexVisitorLog(VisitorLog visitorLog){
        Index index = new Index.Builder(visitorLog).index(elasticIndex).type("visitorLog").build();
        try {
            client.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
