package org.myx.processing;

import org.myx.fileIo.metadata.UserMetaData;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class UserManager {
    private List<UserMetaData> users;

    public UserManager() {
        this.users = new ArrayList<>();
    }

    // 初始化管理员用户
    public void initAdminUser(String username, String password) {
        UserMetaData admin = UserMetaData.createAdminUser(username, password);
        users.add(admin);
        System.out.println("Admin initialized: " + admin);
    }

    // 添加用户
    public void addUser(String username, String password) {
        UserMetaData newUser = new UserMetaData(username, password);
        users.add(newUser);
        System.out.println("User added: " + newUser.getUserName());
    }

    // 删除用户
    public void deleteUser(String username) {
        users.removeIf(user -> user.getUserName().equals(username));
        System.out.println("User deleted: " + username);
    }

    // 添加权限
    public void grantPrivilege(String username, UserMetaData.Privilege privilege) {
        UserMetaData user = findUser(username);
        if (user != null) {
            List <UserMetaData.Privilege> currentPrivileges = user.getPrivileges();
            if (currentPrivileges.add(privilege)) {
                System.out.println("Privilege " + privilege + " granted to " + username);
            } else {
                System.out.println("Privilege already exists for " + username);
            }
        } else {
            System.out.println("User not found: " + username);
        }
    }

    // 撤销权限
    public void revokePrivilege(String username, UserMetaData.Privilege privilege) {
        UserMetaData user = findUser(username);
        if (user != null) {
            List<UserMetaData.Privilege> currentPrivileges = user.getPrivileges();
            if (currentPrivileges.remove(privilege)) {
                System.out.println("Privilege " + privilege + " revoked from " + username);
            } else {
                System.out.println("Privilege not found for " + username);
            }
        } else {
            System.out.println("User not found: " + username);
        }
    }

    // 查找用户
    private UserMetaData findUser(String username) {
        return users.stream()
                .filter(user -> user.getUserName().equals(username))
                .findFirst()
                .orElse(null);
    }

    // 检查用户是否具有特定权限
    public boolean checkPrivilege(String username, UserMetaData.Privilege privilege) {
        UserMetaData user = findUser(username);
        if (user != null && user.hasPrivilege(privilege)) {
            System.out.println("User " + username + " has the privilege: " + privilege);
            return true;
        }
        System.out.println("User " + username + " does not have the privilege: " + privilege);
        return false;
    }
}
