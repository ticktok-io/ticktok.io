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
    private static final String ACCESS_TOKEN = "1234";
    private HttpResponse lastResponse;

    public void start() {
        Application.main();
    }

    public void startClocking(String timeExpr) throws IOException {
        HttpResponse response = Request.Post(createAuthenticatedUrlFor("/api/v1/clocks"))
                .bodyString(createClockRequestFor(timeExpr), ContentType.APPLICATION_JSON)
                .execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_CREATED));
    }

    private String createAuthenticatedUrlFor(String slag) {
        return String.format("%s/%s?access_token=%s", APP_URL, slag, ACCESS_TOKEN);
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

    public void isAccessedWithoutAToken() throws IOException {
        lastResponse = Request.Post(createAuthenticatedUrlFor("/api/v1/clocks"))
                .bodyString(createClockRequestFor("in.1.minute"), ContentType.APPLICATION_JSON)
                .execute().returnResponse();
    }

    public void retrieveAUserError() {
        assertThat(lastResponse.getStatusLine().getStatusCode(), is(400));
    }
}
