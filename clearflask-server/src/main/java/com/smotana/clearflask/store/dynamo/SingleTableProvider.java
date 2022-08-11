package com.smotana.clearflask.store.dynamo;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.kik.config.ice.ConfigSystem;
import com.kik.config.ice.annotations.DefaultValue;
import com.smotana.clearflask.core.ManagedService;
import io.dataspray.singletable.SingleTable;

@Singleton
public class SingleTableProvider extends ManagedService implements Provider<SingleTable> {

    public interface Config {
        @DefaultValue("true")
        boolean createTables();

        @DefaultValue("clearflask")
        String tablePrefix();

        @DefaultValue("2")
        int gsiCount();

        @DefaultValue("0")
        int lsiCount();
    }

    @Inject
    private Config config;
    @Inject
    private Gson gson;
    @Inject
    private DynamoDB dynamoDoc;
    @Inject
    private Provider<SingleTable> singleTableProvider;

    @Override
    public SingleTable get() {
        return SingleTable.builder()
                .tablePrefix(config.tablePrefix())
                .dynamoDoc(dynamoDoc)
                .overrideGson(gson)
                .build();
    }

    @Override
    protected void serviceStart() throws Exception {
        if (config.createTables()) {
            singleTableProvider.get().createTableIfNotExists(
                    config.lsiCount(), config.gsiCount());
        }
    }

    public static Module module() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(SingleTable.class).toProvider(SingleTableProvider.class).asEagerSingleton();
                Multibinder.newSetBinder(binder(), ManagedService.class).addBinding().to(SingleTableProvider.class);
                install(ConfigSystem.configModule(Config.class));
            }
        };
    }
}