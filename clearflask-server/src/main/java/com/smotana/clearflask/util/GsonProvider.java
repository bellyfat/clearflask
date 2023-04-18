// SPDX-FileCopyrightText: 2019-2022 Matus Faro <matus@smotana.com>
// SPDX-License-Identifier: Apache-2.0
package com.smotana.clearflask.util;

import com.dampcake.gson.immutable.ImmutableAdapterFactory;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.smotana.clearflask.api.model.ConfigAdmin;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Singleton
public class GsonProvider implements Provider<Gson> {
    @Inject
    private ConfigSchemaUpgrader configSchemaUpgrader;

    private Gson gson;

    @Override
    public Gson get() {
        if (gson == null) {
            gson = create(true);
        }
        return gson;
    }

    private Gson create(boolean useConfigAdminUpgrader) {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .disableHtmlEscaping()
                .registerTypeAdapterFactory(ImmutableAdapterFactory.forGuava())
                .registerTypeAdapterFactory(new GsonNonNullAdapterFactory())
                .registerTypeAdapter(Instant.class, new InstantTypeConverter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeConverter())
                .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                .registerTypeAdapterFactory(ExplicitNull.get());
        if (useConfigAdminUpgrader) {
            gsonBuilder.registerTypeAdapter(ConfigAdmin.class, new ConfigAdminUpgrader());
        }
        return gsonBuilder.create();
    }

    private static class InstantTypeConverter
            implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
        @Override
        public JsonElement serialize(Instant src, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return Instant.parse(json.getAsString());
        }
    }

    private static class LocalDateTypeConverter
            implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate src, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    private static class DateTimeTypeConverter
            implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
        @Override
        public JsonElement serialize(DateTime src, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return DateTime.parse(json.getAsString());
        }
    }

    private class ConfigAdminUpgrader
            implements JsonDeserializer<ConfigAdmin> {
        private Gson gsonWithoutConfigAdminUpgrader = create(false);

        @Override
        public ConfigAdmin deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            Optional<String> configUpgradedOpt = configSchemaUpgrader.upgrade(json);
            if (configUpgradedOpt.isPresent()) {
                return gsonWithoutConfigAdminUpgrader.fromJson(configUpgradedOpt.get(), ConfigAdmin.class);
            } else {
                return gsonWithoutConfigAdminUpgrader.fromJson(json, ConfigAdmin.class);
            }
        }
    }

    public static Module module() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(Gson.class).toProvider(GsonProvider.class).asEagerSingleton();
            }
        };
    }
}
