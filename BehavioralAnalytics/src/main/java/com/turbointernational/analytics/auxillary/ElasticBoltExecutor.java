package com.turbointernational.analytics.auxillary;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Get;
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

    public ElasticBoltExecutor(String elasticHost, String elasticIndex) {
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
        settingsBuilder.put("number_of_shards", 5);
        settingsBuilder.put("number_of_replicas", 1);

        return client.execute(new CreateIndex.Builder(elasticIndex).settings(settingsBuilder.build().getAsMap()).build());
    }


    private <T extends ElasticIndex> T incrementProductRank(T t) {
        Get get = new Get.Builder(elasticIndex, t.declareElasticIndex())
                .type(t.getClass().getSimpleName()).build();
        JestResult result = null;
        try {
            result = client.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProductRank productRank = result.getSourceAsObject(ProductRank.class);
        if (productRank != null) {
            productRank.setTimes(productRank.getTimes() + 1);
            return (T) productRank;
        }
        return t;
    }

    public <T extends ElasticIndex> void execute(T t) {
        if (t.hasAccumulator()) {
            t = incrementProductRank(t);
        }
        Index index = new Index.Builder(t).index(elasticIndex).
                type(t.getClass().getSimpleName()).id(t.declareElasticIndex()).build();
        try {
            client.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
