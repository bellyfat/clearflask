package com.smotana.clearflask.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.OptionalLong;

@Slf4j
@Singleton
public class ConfigSchemaUpgrader {

    @Inject
    private Gson gson;

    public Optional<String> upgrade(OptionalLong schemaVersionOpt, String configJson) {
        long schemaVersion = schemaVersionOpt.orElse(1);
        if (schemaVersion >= 2L) {
            return Optional.empty();
        }

        JsonElement config = gson.fromJson(configJson, JsonElement.class);

        if (schemaVersion < 2L) {
            JsonElement emailEl = config.getAsJsonObject().get("users")
                    .getAsJsonObject().get("onboarding")
                    .getAsJsonObject().get("notificationMethods")
                    .getAsJsonObject().get("email");
            if (emailEl != null) {
                emailEl.getAsJsonObject().addProperty("mode", "SignupAndLogin");
            }
        }

        config.getAsJsonObject().addProperty("schemaVersion", 2L);

        return Optional.of(gson.toJson(config));
    }
}