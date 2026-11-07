package tk.freaxsoftware.ribbon2.uix.rest.dummy;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import tk.freaxsoftware.ribbon2.core.data.GroupModel;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
import tk.freaxsoftware.ribbon2.uix.rest.GroupRestClient;

/** Dummy group lookup data for UI development. */
public class GroupRestClientDummy implements GroupRestClient {
    @Override
    public DefaultPage<GroupModel> getGroups(String jwtKey, int pageSize, int page) throws URISyntaxException, IOException {
        GroupModel admins = new GroupModel();
        admins.setName(GroupModel.ADMIN_GROUP);
        GroupModel users = new GroupModel();
        users.setName("Users");
        List<GroupModel> groups = List.of(admins, users);
        return new DefaultPage<>(groups, groups.size());
    }
}
