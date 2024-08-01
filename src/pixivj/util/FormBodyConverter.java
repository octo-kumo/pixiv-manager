package pixivj.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.FormBody;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;

public class FormBodyConverter {
    public static <T> void toQueryParams(@NonNull T obj, FormBody.Builder formBuilder) {
        JsonObject jsonObj = JsonUtils.GSON.toJsonTree(obj)
                .getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObj.entrySet()) {
            if (entry.getValue() != null) {
                formBuilder.addEncoded(entry.getKey(), entry.getValue().getAsString());
            }
        }
    }
}
