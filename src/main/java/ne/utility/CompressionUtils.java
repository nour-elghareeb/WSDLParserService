package ne.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author nour
 */
public class CompressionUtils {
    public static boolean Unzip(File zipFile, File destination, boolean overwrite) {
        byte[] buffer = new byte[1024];
        try {
            if (!isValidZipFile(zipFile)) return false;
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {

                File newFile = newFile(destination, zipEntry);
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zipEntry = zis.getNextEntry();

            }
            zis.closeEntry();
            zis.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }   
    }
    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
         
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
         
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
         
        return destFile;
    }
    static boolean isValidZipFile(final File file) {
    ZipFile zipfile = null;
    try {
        zipfile = new ZipFile(file);
        return true;
    } catch (IOException e) {
        return false;
    } finally {
        try {
            if (zipfile != null) {
                zipfile.close();
                zipfile = null;
            }
        } catch (IOException e) {
        }
    }
}
}
