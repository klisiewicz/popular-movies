package pl.karollisiewicz.cinema.app.data.source.web;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import org.joda.time.LocalDate;

import java.lang.reflect.Type;

import javax.inject.Inject;

import pl.karollisiewicz.common.log.Logger;

/**
 * Deserializer for {@link org.joda.time.LocalDate} objects.
 */
public final class DateJsonDeserializer implements JsonDeserializer<LocalDate> {
    private final Logger logger;

    @Inject
    DateJsonDeserializer(@NonNull final Logger logger) {
        this.logger = logger;
    }

    @Override
    @Nullable
    public LocalDate deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {
        try {
            return LocalDate.parse(json.getAsString());
        } catch (Exception e) {
            logger.error(DateJsonDeserializer.class, e);
            return null;
        }
    }
}
