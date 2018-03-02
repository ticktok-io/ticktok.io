package e2e.io.ticktok.broadcast.support;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.ticktok.broadcast.Application;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AppDriver {

    private static final String APP_URL = "http://localhost:8080";
    private static final String CLIENT_ID = "e2e-client";

    public void start() {
        Application.main();
    }

    public void registerFor(String timeExpr) throws IOException {
        HttpResponse response = Request.Post(APP_URL + "/api/v1/clocks")
                .bodyString(createClockRequestFor(timeExpr), ContentType.APPLICATION_JSON)
                .execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_CREATED));
    }

    private String createClockRequestFor(String timeExpr) {
        return new JSONObject()
                .put("schedule", timeExpr)
                .put("clientId", CLIENT_ID)
                .toString();
    }

    public void isHealthy() throws IOException {
        assertThat(getHealthStatus(), is("UP"));
    }

    private String getHealthStatus() throws IOException {
        String health = Request.Get("http://localhost:8081/health").execute().returnContent().asString();
        return new Gson().fromJson(health, JsonObject.class).get("status").getAsString();
    }
}
