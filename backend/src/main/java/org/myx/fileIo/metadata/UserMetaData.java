package org.myx.fileIo.metadata;

import java.io.Serializable;
import java.util.List;

public class UserMetaData implements Serializable{
    private static final long serialVersionUID = 6746102291468956672L;
    public List<Privilege> getPrivileges() {
        return userPrivileges;
    }


    public enum Privilege {
        CONNECT("connect"),//只能select查看表
        RESOURCE("resource"),//可以创建删除
        DBA("dba");//拥有所有权限

        private final String privilege;

        Privilege(String privilege) {
            this.privilege = privilege;
        }

        public String getPrivilege() {
            return privilege;
        }
        public boolean isConnect(){
            if(getPrivilege() == "connect"){
                return true;
            }else{
                return false;
            }
        }
        public boolean isRecourse(){
            if(getPrivilege() == "resource"){
                return true;
            }else{
                return false;
            }
        }
        public boolean isDBA(){
            if(getPrivilege()=="dba"){
                return true;
            }else{
                return false;
            }
        }
    }

    private String userName;
    private String password;
    private String userPrivilege;
    private List<Privilege> userPrivileges;

    public UserMetaData(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public UserMetaData(String userName, String password, String userPrivilege){
        this.userName = userName;
        this.password = password;
        setUserPrivilege(userPrivilege);
    }

    public static UserMetaData createAdminUser(String userName, String password) {
        return new UserMetaData(userName, password);
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

    public String getUserPrivilege() {
        return userPrivilege;
    }

    public void setUserPrivilege(String userPrivilege) {
        this.userPrivilege = userPrivilege;
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
                ", userPrivilege=" + userPrivilege +
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
        return userName.equals(that.userName) && password.equals(that.password) && userPrivilege.equals(that.userPrivilege);
    }
}
