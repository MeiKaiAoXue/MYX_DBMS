package org.myx.fileIo.metadata;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class UserMetaData implements Serializable{
    public List<Privilege> getPrivileges() {
        return userPrivileges;
    }

    public enum UserType {
        ADMIN,
        USER
    }

    public enum UserStatus {
        ACTIVE,
        INACTIVE
    }

    public enum Privilege {
        READ("read"),
        INSERT("insert"),
        UPDATE("update"),
        DELETE("delete"),
        CREATE_USER("create_user"),
        DELETE_USER("delete_user"),
        DELETE_DATABASE("delete_database");

        private final String action;

        Privilege(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }
    }

    private String userName;
    private String password;
    private UserType userType;
    private UserStatus userStatus;
    private List<Privilege> userPrivileges;

    public UserMetaData(String userName, String password, List<Privilege> userPrivileges) {
        this.userName = userName;
        this.password = password;
        this.userPrivileges = userPrivileges;
    }

    public static UserMetaData createAdminUser(String userName, String password) {
        return new UserMetaData(userName, password, Arrays.asList(Privilege.values()));
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public List<Privilege> getUserPrivileges() {
        return userPrivileges;
    }

    public void setUserPrivileges(List<Privilege> userPrivileges) {
        this.userPrivileges = userPrivileges;
    }

    public void addPrivilege(Privilege userPrivilege) {
        userPrivileges.add(userPrivilege);
    }

    public void removePrivilege(Privilege userPrivilege) {
        userPrivileges.remove(userPrivilege);
    }

    public boolean hasPrivilege(Privilege userPrivilege) {
        return userPrivileges.contains(userPrivilege);
    }

    @Override
    public String toString() {
        return "UserMetaData{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", userType=" + userType +
                ", userStatus=" + userStatus +
                ", userPrivileges=" + userPrivileges +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserMetaData that = (UserMetaData) obj;
        return userName.equals(that.userName) && password.equals(that.password) && userType == that.userType && userStatus == that.userStatus && userPrivileges.equals(that.userPrivileges);
    }
}
