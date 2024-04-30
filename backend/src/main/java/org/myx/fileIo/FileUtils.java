package org.myx.fileIo;

import org.myx.fileIo.metadata.DBMetaData;
import org.myx.fileIo.metadata.TableMetaData1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// 编写一个以对象类型读取与写入文件的工具类
public class FileUtils {
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

    public static void deleteDB(String filePath){
        File file = new File(filePath);
        if (file.delete()){
            System.out.println("d=Delete database");
        }else{
            System.out.println("Failed to delete the database");
        }
    }


}
