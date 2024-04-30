package org.myx.fileIo.metadata;

import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

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
        READ("read"),
        INSERT("insert"),
        UPDATE("update"),
        DELETE("delete"),
        CREATE_USER("create_user"),
        DELETE_USER("delete_user"),
        DELETE_DATABASE("delete_database");


        private final String action;

        privilege(String action){
            this.action = action;
        }

        public String getAction(){
            return action;
        }
    }

    private String userName;
    private String password;
    private UserType userType;
    private UserStatus userStatus;
    private List<privilege> userPrivileges;
    public static Set<privilege> privileges;

    public UserMetaData(String userName, String password,Set<privilege> privileges) {
        this.userName = userName;
        this.password = password;
//        this.userType = userType;
//        this.userStatus = userStatus;
        this.privileges = privileges;
    }
    public static UserMetaData createAdminUser(String userName, String password) {
        privileges = new HashSet<>(Arrays.asList(privilege.values()));
        return new UserMetaData(userName, password, privileges);
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
    public Set<privilege> getPrivileges() {
        return privileges;
    }

    public void setUserPrivileges(Set<privilege> privileges) {
        this.privileges = privileges;
    }

    public void addPrivilege(privilege userPrivilege) {
        userPrivileges.add(userPrivilege);
    }

    public void removePrivilege(privilege userPrivilege) {
        userPrivileges.remove(userPrivilege);
    }

    public boolean hasPrivilege(privilege userPrivilege) {
        return privileges.contains(userPrivilege);
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
