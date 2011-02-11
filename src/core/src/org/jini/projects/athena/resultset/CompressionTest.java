package org.jini.projects.athena.resultset;

/**
 * @author calum
 *
 * org.jini.projects.athena
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class CompressionTest {
    private JarOutputStream jarfile = null;

    public CompressionTest(String jarname) throws Exception {
        jarfile = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(jarname)));
    }

    public void writeDir(File dir) throws Exception {
        if (dir.isDirectory()) {
            System.out.println("Storing directory " + dir.getName());
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory())
                    writeDir(files[i]);
                else {
                    JarEntry jet = new JarEntry(files[i].getPath());
                    jarfile.putNextEntry(jet);
                    BufferedInputStream strem = new BufferedInputStream(new FileInputStream(files[i]));
                    byte[] buffer = new byte[4096];
                    int offset = 0;
                    while (strem.available() != 0) {
                        int lengthread = strem.read(buffer);
                        offset += lengthread;
                        jarfile.write(buffer, 0, lengthread);
                        buffer = new byte[4096];
                    }
                    jarfile.flush();
                    jarfile.closeEntry();
                    System.out.println("Written " + files[i].getPath() + ": " + jet.getSize() + "=>" + jet.getCompressedSize());
                }
            }
        }
    }

    public void finish() throws Exception {
        jarfile.finish();
        jarfile.close();
    }

    public static void main(String[] args) throws Exception {
        CompressionTest app = new CompressionTest("/home/calum/testimg.jar");
        System.setProperty("user.dir", "/home/calum");
        app.writeDir(new File("images"));

    }
}
