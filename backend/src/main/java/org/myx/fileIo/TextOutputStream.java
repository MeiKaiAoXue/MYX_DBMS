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
        if (b == '\r') return; // 忽略回车字符
        if (b == '\n') {
            final String text = sb.toString() + "\n";
            SwingUtilities.invokeLater(() -> {
                textArea.append(text);
            });
            sb.setLength(0); // 清空StringBuilder
        } else {
            sb.append((char) b);
        }
    }
}
