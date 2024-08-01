package pixivj.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.HttpUrl;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.Set;

/**
 * This converts the properties of an object to URL query parameters.
 */
public class QueryParamConverter {

    /**
     * Converts the properties of {@code obj} to query parameters, and add the query parameters to
     * the given {@code urlBuilder}. The values of the properties will be serialized as JSON strings.
     *
     * @param obj        The object to provide the query parameters values.
     * @param urlBuilder The URL builder to add the generated query parameters.
     * @param <T>        The type of the object.
     */
    public static <T> void toQueryParams(@NonNull T obj, HttpUrl.@NonNull Builder urlBuilder) {
        JsonObject jsonObj = JsonUtils.GSON.toJsonTree(obj)
                .getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObj.entrySet()) {
            if (entry.getValue() != null) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue().getAsString());
            }
        }
    }

    public static <T> T fromQueryParams(@NonNull String url, @NonNull Class<T> targetType) throws IllegalArgumentException {
        try {
            HttpUrl parsedUrl = HttpUrl.parse(url);
            if (parsedUrl == null) {
                throw new IllegalArgumentException("Failed to parse URL " + url);
            }
            Set<String> names = parsedUrl.queryParameterNames();
            JsonObject jsonObj = new JsonObject();
            for (String name : names) {
                jsonObj.addProperty(name, parsedUrl.queryParameter(name));
            }
            return JsonUtils.GSON.fromJson(jsonObj, targetType);
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to convert query parameters to the object", ex);
        }
    }
}
