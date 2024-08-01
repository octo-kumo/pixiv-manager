package pixivj.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This implements an adapter for serializing and deserializing between a ISO local date
 * (e.g. "2020-01-01") and {@link LocalDate}.
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    /**
     * Serializes a {@link LocalDate} to a JSON string.
     *
     * @param date      Local date to be serialized.
     * @param typeOfSrc Type of the source.
     * @param context   Serialization context.
     * @return Serialized JSON string.
     */
    @Override
    public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    /**
     * Deserializes from a JSON string to a {@link LocalDate}.
     *
     * @param json    JSON element to be deserialized.
     * @param typeOfT Type of the JSON element.
     * @param context Deserialization context.
     * @return Deserialized {@link LocalDate}.
     * @throws JsonParseException Failed to parse the JSON element.
     */
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
