package pixivj.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class implements an adapter for serializing/deserializing between an ISO offset date time
 * (e.g. "2011-12-03T10:15:30+01:00") and a {@link OffsetDateTime}.
 */
public class OffsetDateTimeAdapter implements JsonSerializer<OffsetDateTime>,
        JsonDeserializer<OffsetDateTime> {

    /**
     * Serializes a {@link OffsetDateTime} to a JSON string.
     *
     * @param dateTime  The {@link OffsetDateTime} to be serialized.
     * @param typeOfSrc Type of the value.
     * @param context   Serializing context.
     * @return Serialized JSON string.
     */
    @Override
    public JsonElement serialize(OffsetDateTime dateTime, Type typeOfSrc,
                                 JsonSerializationContext context) {
        return new JsonPrimitive(dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    /**
     * Deserializes a JSON element to a {@link OffsetDateTime}.
     *
     * @param json    JSON element to be deserialized.
     * @param typeOfT Type of the element.
     * @param context Deserialization context.
     * @return Deserialized {@link OffsetDateTime}.
     * @throws JsonParseException Failed to parse the JSON element.
     */
    @Override
    public OffsetDateTime deserialize(JsonElement json, Type typeOfT,
                                      JsonDeserializationContext context) throws JsonParseException {
        return OffsetDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
