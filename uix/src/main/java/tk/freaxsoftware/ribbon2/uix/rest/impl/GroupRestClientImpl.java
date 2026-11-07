package tk.freaxsoftware.ribbon2.uix.rest.impl;

import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import tk.freaxsoftware.ribbon2.core.data.GroupModel;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
import tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest;
import tk.freaxsoftware.ribbon2.uix.rest.GroupRestClient;

/** HTTP implementation for the gateway group lookup API. */
public class GroupRestClientImpl implements GroupRestClient {
    private final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    private final String baseUrl;

    public GroupRestClientImpl(String gatewayUrl) {
        this.baseUrl = gatewayUrl + "/api/group";
    }

    @Override
    public DefaultPage<GroupModel> getGroups(String jwtKey, int pageSize, int page) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl)
                .addParameter(PaginationRequest.PARAM_PAGE, Integer.toString(page))
                .addParameter(PaginationRequest.PARAM_SIZE, Integer.toString(pageSize)).build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        return ResponseUtil.handleResponse(response, new TypeToken<DefaultPage<GroupModel>>() {});
    }
}
