package e2e.org.ticktok.broadcast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TicktokClient {

    private static final String CLIENT_ID = "e2e-client";
    private JSONObject clockDetails; // queue, url, id, schdule, clientId

    public void registeredFor(String timeExpr) throws IOException {
        HttpResponse response = Request.Post("http://localhost:8080/api/v1/clocks")
                .bodyString(createClockRequestFor(timeExpr), ContentType.APPLICATION_JSON)
                .execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_CREATED));
        clockDetails = new JSONObject(EntityUtils.toString(response.getEntity()));
    }

    private String createClockRequestFor(String timeExpr) {
        return new JSONObject()
                .put("schedule", timeExpr)
                .put("clientId", CLIENT_ID)
                .toString();
    }

    public void receivedClock() {

    }
}
