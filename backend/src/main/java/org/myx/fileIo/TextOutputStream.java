package org.myx.fileIo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import javax.swing.*;

public class TextOutputStream extends OutputStream {
    private JTextArea textArea;
    private StringBuilder sb = new StringBuilder();

    public TextOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        // 处理单字节字符
        sb.append((char) b);
        if (b == '\n') {
            final String text = sb.toString();
            SwingUtilities.invokeLater(() -> {
                textArea.append(text);
            });
            sb.setLength(0); // 清空StringBuilder
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // 将字节数组转换为字符串，并按字符处理
        String str = new String(b, off, len, StandardCharsets.UTF_8);
        sb.append(str);
        if (str.contains("\n")) {
            final String text = sb.toString();
            SwingUtilities.invokeLater(() -> {
                textArea.append(text);
            });
            sb.setLength(0); // 清空StringBuilder
        }
    }

    @Override
    public void flush() throws IOException {
        if (sb.length() > 0) {
            final String text = sb.toString();
            SwingUtilities.invokeLater(() -> {
                textArea.append(text);
            });
            sb.setLength(0);
        }
    }

    @Override
    public void close() throws IOException {
        flush();
    }
}
