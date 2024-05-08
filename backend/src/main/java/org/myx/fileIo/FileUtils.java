package org.myx.fileIo;

import org.myx.fileIo.metadata.DBMetaData;
import org.myx.fileIo.metadata.TableMetaData1;
import org.myx.fileIo.metadata.UserMetaData;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 编写一个以对象类型读取与写入文件的工具类
public class FileUtils {

    private static final String DB_LIST = "./db_list.txt";
    //将数据库名称写入文件
    public static void saveDBName(String dbName) throws IOException {
        try (FileWriter fw = new FileWriter(DB_LIST, true);
        BufferedWriter bw = new BufferedWriter(fw)){
            bw.write(dbName + "\n");
        } catch (IOException e){
            System.out.println("写入数据库列表文件失败："+ e.getMessage());
        }
    }

    //从文件中读取数据库名称列表
    public static List<String> loadDBNames() {
        List<String> dbNames = new ArrayList<>();
        try (FileReader fr = new FileReader(DB_LIST);
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                dbNames.add(line);
            }
        } catch (IOException e) {
            System.out.println("无法读取数据库列表文件：" + e.getMessage());
        }
        return dbNames;
    }

    // 以对象形式写入文件
    public static void writeObjectToFile(Object obj, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 以对象形式从文件中读取所有数据
    public static Object readObjectFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static void initDB(String filePath) {
        DBMetaData db = new DBMetaData("DB1");
        TableMetaData1 initTable = new TableMetaData1("INIT_TABLE");
        db.addTable(initTable);
        writeObjectToFile(db, filePath);
    }

    public static void deleteDB(String dbName){
        File file = new File("./" + dbName + ".txt");
        if (file.delete()) {
            System.out.println("数据库文件已删除：" + dbName);
        } else {
            System.out.println("无法删除数据库文件：" + dbName);
        }
    }

    public static void initAdminUser(String adminFilePath, String username, String password) {
        List<UserMetaData.Privilege> privileges = Arrays.asList(UserMetaData.Privilege.values());
        UserMetaData admin = new UserMetaData(username, password, privileges);
        writeObjectToFile(admin, adminFilePath); // 将管理员用户信息写入文件
        System.out.println("Admin initialized");
    }

    // 添加用户
    public static void addUser(String filePath, UserMetaData user) {
        try {
            List<UserMetaData> users = (List<UserMetaData>) readObjectFromFile(filePath);
            users.add(user);
            writeObjectToFile(users, filePath);
        } catch (RuntimeException e) {
            List<UserMetaData> users = new ArrayList<>();
            users.add(user);
            writeObjectToFile(users, filePath);
        }
        System.out.println("User added: " + user.getUserName());
    }

    // 删除用户
    public static void deleteUser(String filePath, String username) {
        try {
            List<UserMetaData> users = (List<UserMetaData>) readObjectFromFile(filePath);
            users.removeIf(user -> user.getUserName().equals(username));
            writeObjectToFile(users, filePath);
            System.out.println("User deleted: " + username);
        } catch (RuntimeException e) {
            System.out.println("Failed to delete user: " + username);
        }
    }


}
