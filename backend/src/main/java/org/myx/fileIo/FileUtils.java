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
    //filePath传入当前选择的数据库名称
    public static Object readObjectFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static void initDB(String filePath) {
        File dbDirectory = new File(filePath);
        if (!dbDirectory.exists()) {
            dbDirectory.mkdirs(); // 创建目录
        }

        String metaDataFilePath = filePath + "/db.txt"; // 在目录中创建 db.txt
        DBMetaData db = new DBMetaData("DB1");
        TableMetaData1 initTable = new TableMetaData1("INIT_TABLE");
        db.addTable(initTable);
        writeObjectToFile(db, metaDataFilePath);
    }


    public static void deleteDB(String dbName){
        if (dbName == null || dbName.trim().isEmpty()) {
            System.out.println("数据库名称无效，取消删除操作。");
            return;
        }

        File directory = new File("./" + dbName);  // 定位到数据库对应的目录

        // 确保不是在删除根目录或关键目录
        if (directory.exists() && directory.isDirectory() && !directory.getAbsolutePath().equals(new File(".").getAbsolutePath())) {
            if (deleteDirectory(directory)) {
                System.out.println("数据库目录及内容已删除：" + dbName);
            } else {
                System.out.println("无法删除数据库目录：" + dbName);
            }
        } else {
            System.out.println("要删除的目录不存在或路径错误：" + directory.getAbsolutePath());
        }
    }
    public static boolean deleteDirectory(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDirectory(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        }
        return dir != null && dir.delete();
    }

    public static void updateDBList(String dbName) {
        List<String> dbNames = loadDBNames(); // 读取现有的数据库名称列表
        dbNames.removeIf(name -> name.equals(dbName)); // 移除指定的数据库名称
        // 重写db_list.txt文件
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("./db_list.txt", false))) {
            for (String name : dbNames) {
                bw.write(name + "\n");
            }
        } catch (IOException e) {
            System.out.println("更新数据库列表文件失败：" + e.getMessage());
        }
    }

    public static void initAdminUser(String adminFilePath, String username, String password) {
        UserMetaData admin = new UserMetaData(username, password,"dba");
        List<UserMetaData> users = new ArrayList<>();
        users.add(admin);
        writeObjectToFile(users, adminFilePath); // 将管理员用户列表写入文件
        System.out.println("Admin initialized");
    }

    // 添加用户
    public static void addUser(String filePath, UserMetaData user) {
        List<UserMetaData> users;
        try {
            users = (List<UserMetaData>) readObjectFromFile(filePath);
        } catch (ClassCastException e) {
            System.out.println("文件格式不正确，ClassCastException: " + e.getMessage());
            return; // 或者决定是否需要创建新文件
        }

        if (users == null) {
            users = new ArrayList<>(); // 只在确实需要时创建新列表
        }
        users.add(user);
        writeObjectToFile(users, filePath);
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
