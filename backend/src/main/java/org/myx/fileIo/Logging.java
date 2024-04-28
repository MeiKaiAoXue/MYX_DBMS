package org.myx.fileIo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logging {
    private static final Logger logger = Logger.getLogger(Logging.class.getName());

    public static void log(String message) throws IOException {
        FileHandler fh = new FileHandler("./mylog.log",true);

        // 指定日志文件的编码为UTF-8,写入字符的编码和文件的编码一致即可避免乱码
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("mylog.log", true), "UTF-8");
        fh.setEncoding("UTF-8");

        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        logger.info(message);
        fh.close();
    }
}
