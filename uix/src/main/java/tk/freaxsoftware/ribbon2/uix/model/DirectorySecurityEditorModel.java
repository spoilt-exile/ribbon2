package tk.freaxsoftware.ribbon2.uix.model;

import java.util.List;
import tk.freaxsoftware.ribbon2.core.data.GroupModel;
import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionTaggedModel;
import tk.freaxsoftware.ribbon2.core.data.DirectorySecurityModel;
import tk.freaxsoftware.ribbon2.core.data.UserModel;

/** Complete typed data bundle for rendering a directory security editor. */
public class DirectorySecurityEditorModel {
    private DirectorySecurityModel security;
    private List<DirectoryPermissionTaggedModel> permissions;
    private List<UserModel> users;
    private List<GroupModel> groups;

    public DirectorySecurityModel getSecurity() { return security; }
    public void setSecurity(DirectorySecurityModel security) { this.security = security; }
    public List<DirectoryPermissionTaggedModel> getPermissions() { return permissions; }
    public void setPermissions(List<DirectoryPermissionTaggedModel> permissions) { this.permissions = permissions; }
    public List<UserModel> getUsers() { return users; }
    public void setUsers(List<UserModel> users) { this.users = users; }
    public List<GroupModel> getGroups() { return groups; }
    public void setGroups(List<GroupModel> groups) { this.groups = groups; }
}
