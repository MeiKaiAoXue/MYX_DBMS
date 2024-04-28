package org.myx.fileIo.metadata;

import java.util.List;

public class UserMetaData {
    public enum UserType {
        ADMIN,
        USER
    }
    public enum UserStatus {
        ACTIVE,
        INACTIVE
    }

    public enum privilege {
        READ,
        INSERT,
        UPDATE,
        DELETE
    }

    private String userName;
    private String password;
    private UserType userType;
    private UserStatus userStatus;
    private List<privilege> userPrivileges;

    public UserMetaData(String userName, String password, UserType userType, UserStatus userStatus, List<privilege> userPrivileges) {
        this.userName = userName;
        this.password = password;
        this.userType = userType;
        this.userStatus = userStatus;
        this.userPrivileges = userPrivileges;
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

    public List<privilege> getUserPrivileges() {
        return userPrivileges;
    }

    public void setUserPrivileges(List<privilege> userPrivileges) {
        this.userPrivileges = userPrivileges;
    }

    public void addPrivilege(privilege userPrivilege) {
        userPrivileges.add(userPrivilege);
    }

    public void removePrivilege(privilege userPrivilege) {
        userPrivileges.remove(userPrivilege);
    }

    public String toString() {
        return "UserMetaData{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", userType=" + userType +
                ", userStatus=" + userStatus +
                ", userPrivileges=" + userPrivileges +
                '}';
    }

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
