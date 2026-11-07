package tk.freaxsoftware.ribbon2.uix.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import tk.freaxsoftware.ribbon2.core.data.GroupModel;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;

/** REST client for group lookups used by directory security administration. */
public interface GroupRestClient {
    DefaultPage<GroupModel> getGroups(String jwtKey, int pageSize, int page) throws URISyntaxException, IOException;
}
