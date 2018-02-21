package e2e.io.ticktok.broadcast;

import io.ticktok.broadcast.Application;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AppDriver {

    private static final String CLIENT_ID = "e2e-client";

    public AppDriver() {
        Application.main();
    }

    public void registeredFor(String timeExpr) throws IOException {
        HttpResponse response = Request.Post("http://localhost:8080/api/v1/clocks")
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

}
