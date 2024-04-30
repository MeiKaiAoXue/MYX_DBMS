package org.myx.processing;

import org.myx.fileIo.Logging;
import org.myx.fileIo.metadata.UserMetaData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PermissionManager {

    private Map<String, UserMetaData> userMap;

    public PermissionManager(){
        this.userMap = new HashMap<>();
    }

    public void addUser(UserMetaData user){
        userMap.put(user.getUserName(), user);
    }

    /**
     * 用户授权
     * @param username
     * @param privilege
     */
    public void grantPrivilege(String username, UserMetaData.privilege privilege) throws IOException {
        UserMetaData user = userMap.get(username);
        if (user != null) {
            user.addPrivilege(privilege);
            System.out.println("Granted " + privilege + " to " + username);
        } else {
            System.out.println("User not found");
        };
        Logging.log("Granted " + privilege + " to " + username);
        throw new IllegalArgumentException("Grant " + privilege + "fail");

    }

    /**
     * 收回用户权限
     * @param username
     * @param privilege
     */
    public void revokePrivilege(String username, UserMetaData.privilege privilege) throws IOException {
        UserMetaData user = userMap.get(username);
        if (user != null) {
            user.removePrivilege(privilege);
            System.out.println("Revoked " + privilege + " from " + username);
        } else {
            System.out.println("User not found");
        }
        Logging.log("revoke " + privilege + " from " + username);
        throw new IllegalArgumentException("revoke " + privilege + "failed");
    }

    /**
     * 检查用户权限
     * @param username
     * @param privilege
     * @return 若用户存在，则返回拥有的权限，若不存在返回false
     */
    public boolean checkPrivilege(String username, UserMetaData.privilege privilege) {
        UserMetaData user = userMap.get(username);
        if (user != null) {
            return user.hasPrivilege(privilege);
        } else {
            System.out.println("User not found");
            return false;
        }

    }
}
