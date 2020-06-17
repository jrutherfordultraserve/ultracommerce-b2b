package com.mycompany.core.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.RequestWriter;
import org.broadleafcommerce.core.search.service.SearchService;
import org.broadleafcommerce.core.search.service.solr.SolrConfiguration;
import org.broadleafcommerce.core.search.service.solr.SolrSearchServiceImpl;
import org.broadleafcommerce.core.search.service.solr.BroadleafCloudSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ApplicationSolrConfiguration {

    @Value("${solr.cloud.defaultNumShards}")
    protected int defaultNumShards;

    @Value("${solr.cloud.zookeeperUrl}")
    protected String zookeeperUrl;

    protected SolrClient getNewSolrClient(String collection) {
        CloudSolrClient solrClient = new BroadleafCloudSolrClient(zookeeperUrl);
        solrClient.setDefaultCollection(collection);
        solrClient.setRequestWriter(new RequestWriter());
        return solrClient;
    }

    @Bean
    public SolrClient primaryCatalogSolrClient() {
        return getNewSolrClient("catalog");
    }

    @Bean
    public SolrClient reindexCatalogSolrClient() {
        return getNewSolrClient("catalog-reindex");
    }
   
    @Bean
    public SolrClient adminCatalogSolrClient() {
        return getNewSolrClient("catalog-admin");
    }

    @Bean
    public SolrClient primaryOrderSolrClient() {
        return getNewSolrClient("order");
    }

    @Bean
    public SolrClient reindexOrderSolrClient() {
        return getNewSolrClient("order-reindex");
    }

    @Bean
    public SolrClient adminOrderSolrClient() {
        return getNewSolrClient("order-admin");
    }

    @Bean
    public SolrClient primaryCustomerSolrClient() {
        return getNewSolrClient("customer");
    }

    @Bean
    public SolrClient reindexCustomerSolrClient() {
        return getNewSolrClient("customer-reindex");
    }

    @Bean
    public SolrClient adminCustomerSolrClient() {
        return getNewSolrClient("customer-admin");
    }

    @Bean
    public SolrClient primaryFulfillmentOrderSolrClient() {
        return getNewSolrClient("fulfillment");
    }

    @Bean
    public SolrClient reindexFulfillmentOrderSolrClient() {
        return getNewSolrClient("fulfillment-reindex");
    }

    @Bean
    public SolrClient adminFulfillmentOrderSolrClient() {
        return getNewSolrClient("fulfillment-admin");
    }

    @Bean
    public SolrConfiguration blCatalogSolrConfiguration() throws IllegalStateException {
        SolrConfiguration solrConfiguration = new SolrConfiguration(primaryCatalogSolrClient(), reindexCatalogSolrClient(), adminCatalogSolrClient());
        solrConfiguration.setSolrCloudNumShards(defaultNumShards);
        solrConfiguration.setSolrCloudConfigName("catalog");
        return solrConfiguration;
    }

    @Bean
    public SolrConfiguration blOrderSolrConfiguration() throws IllegalStateException {
        SolrConfiguration solrConfiguration = new SolrConfiguration(primaryOrderSolrClient(), reindexOrderSolrClient(), adminOrderSolrClient());
        solrConfiguration.setSolrCloudNumShards(defaultNumShards);
        solrConfiguration.setSolrCloudConfigName("order");
        return solrConfiguration;
    }

    @Bean
    public SolrConfiguration blCustomerSolrConfiguration() throws IllegalStateException {
        SolrConfiguration solrConfiguration = new SolrConfiguration(primaryCustomerSolrClient(), reindexCustomerSolrClient(),
                adminCustomerSolrClient());
        solrConfiguration.setSolrCloudNumShards(defaultNumShards);
        solrConfiguration.setSolrCloudConfigName("customer");
               return solrConfiguration;
    }

    @Bean
    public SolrConfiguration blFulfillmentOrderSolrConfiguration() throws IllegalStateException {
        SolrConfiguration solrConfiguration = new SolrConfiguration(primaryFulfillmentOrderSolrClient(), reindexFulfillmentOrderSolrClient(),
                adminFulfillmentOrderSolrClient());
        solrConfiguration.setSolrCloudNumShards(defaultNumShards);
        solrConfiguration.setSolrCloudConfigName("fulfillment_order");
        return solrConfiguration;
    }

    @Bean
    protected SearchService blSearchService() {
        return new SolrSearchServiceImpl();
    }
}
