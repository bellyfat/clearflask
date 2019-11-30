package com.smotana.clearflask.store.elastic;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.util.Optional;

@Log4j2
@Singleton
public class ProductionElasticSearchProvider extends AbstractIdleService implements Provider<RestClient> {

    private static String AES_ENDPOINT = "https://domain.us-west-1.es.amazonaws.com";

    @Inject
    private AWSCredentialsProvider AwsCredentialsProvider;

    private Optional<RestClient> restClientOpt = Optional.empty();

    @Override
    public RestClient get() {
        restClientOpt = Optional.of(RestClient.builder(HttpHost.create(AES_ENDPOINT)).build());
        return restClientOpt.get();
    }

    @Override
    protected void startUp() throws Exception {
    }

    @Override
    protected void shutDown() throws Exception {
        if (this.restClientOpt.isPresent()) {
            restClientOpt.get().close();
        }
    }

    public static Module module() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(RestClient.class).toProvider(ProductionElasticSearchProvider.class).asEagerSingleton();
                Multibinder.newSetBinder(binder(), Service.class).addBinding().to(ProductionElasticSearchProvider.class);
            }
        };
    }
}
