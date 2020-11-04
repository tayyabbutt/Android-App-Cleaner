package com.softronix.cleaner.Util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;

public class Storage {

    private static final String TAG = "Storage";

    private final Context mContext;
    // private EncryptConfiguration mConfiguration;

    public Storage(Context context) {
        mContext = context;
    }

   /* public void setEncryptConfiguration(EncryptConfiguration configuration) {
        mConfiguration = configuration;
    }*/

    public String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public String getExternalStorageDirectory(String publicDirectory) {
        return Environment.getExternalStoragePublicDirectory(publicDirectory).getAbsolutePath();
    }

    public String getInternalRootDirectory() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    public String getInternalFilesDirectory() {
        return mContext.getFilesDir().getAbsolutePath();
    }

    public String getInternalCacheDirectory() {
        return mContext.getCacheDir().getAbsolutePath();
    }

    public static boolean isExternalWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean createDirectory(String path) {
        File directory = new File(path);
        if (directory.exists()) {
            Log.w(TAG, "Directory '" + path + "' already exists");
            return false;
        }
        return directory.mkdirs();
    }

    public boolean createDirectory(String path, boolean override) {

        // Check if directory exists. If yes, then delete all directory
        if (override && isDirectoryExists(path)) {
            deleteDirectory(path);
        }

        // Create new directory
        return createDirectory(path);
    }

    public boolean deleteDirectory(String path) {
        return deleteDirectoryImpl(path);
    }

    public boolean isDirectoryExists(String path) {
        return new File(path).exists();
    }

    /*public boolean createFile(String path, String content) {
        return createFile(path, content.getBytes());
    }
*/

    public boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    public boolean isFileExist(String path) {
        return new File(path).exists();
    }

   /* public byte[] readFile(String path) {
        final FileInputStream stream;
        try {
            stream = new FileInputStream(new File(path));
            return readFile(stream);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed to read file to input stream", e);
            return null;
        }
    }*/

 /*   public String readTextFile(String path) {
        byte[] bytes = readFile(path);
        return new String(bytes);
    }*/

    public void appendFile(String path, String content) {
        appendFile(path, content.getBytes());
    }

    public void appendFile(String path, byte[] bytes) {
        if (!isFileExist(path)) {
            Log.w(TAG, "Impossible to append content, because such file doesn't exist");
            return;
        }

        try {
            FileOutputStream stream = new FileOutputStream(new File(path), true);
            stream.write(bytes);
            stream.write(System.getProperty("line.separator").getBytes());
            stream.flush();
            stream.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to append content to file", e);
        }
    }

    /*   public List<File> getNestedFiles(String path) {
           File file = new File(path);
           List<File> out = new ArrayList<File>();
           getDirectoryFilesImpl(file, out);
           return out;
       }
   */
    public List<File> getFiles(String dir) {
        return getFiles(dir, null);
    }

    public List<File> getFiles(String dir, final String matchRegex) {
        File file = new File(dir);
        File[] files = null;
        if (matchRegex != null) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String fileName) {
                    return fileName.matches(matchRegex);
                }
            };
            files = file.listFiles(filter);
        } else {
            files = file.listFiles();
        }
        return files != null ? Arrays.asList(files) : null;
    }

    public File getFile(String path) {
        return new File(path);
    }

    public boolean rename(String fromPath, String toPath) {
        File file = getFile(fromPath);
        File newFile = new File(toPath);
        return file.renameTo(newFile);
    }


  /*  public boolean copy(String fromPath, String toPath) {
        File file = getFile(fromPath);
        if (!file.isFile()) {
            return false;
        }

        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(file);
            outStream = new FileOutputStream(new File(toPath));
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (Exception e) {
            Log.e(TAG, "Failed copy", e);
            return false;
        } finally {
            closeSilently(inStream);
            closeSilently(outStream);
        }
        return true;
    }

    public boolean move(String fromPath, String toPath) {
        if (copy(fromPath, toPath)) {
            return getFile(fromPath).delete();
        }
        return false;
    }*/

  /*  protected byte[] readFile(final FileInputStream stream) {
        class Reader extends Thread {
            byte[] array = null;
        }


        *//**
     * Encrypt or Descrypt the content. <br>
     *
     * @param content        The content to encrypt or descrypt.
     * @param encryptionMode Use: {@link Cipher#ENCRYPT_MODE} or
     *                       {@link Cipher#DECRYPT_MODE}
     * @return
     *//*


     */

    /**
     * Delete the directory and all sub content.
     *
     * @param path The absolute directory path. For example:
     *             <i>mnt/sdcard/NewFolder/</i>.
     * @return <code>True</code> if the directory was deleted, otherwise return
     * <code>False</code>
     *//*
     */
    private boolean deleteDirectoryImpl(String path) {
        File directory = new File(path);

        // If the directory exists then delete
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files == null) {
                return true;
            }
            // Run on all sub files and folders and delete them
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectoryImpl(files[i].getAbsolutePath());
                } else {
                    files[i].delete();
                }
            }
        }
        return directory.delete();
    }/*

     *//**
     * Get all files under the directory
     *
     * @param directory
     * @param out
     * @return
     *//*
     *//*  private void getDirectoryFilesImpl (File directory, List < File > out){
            if (directory.exists()) {
                File[] files = directory.listFiles();
                if (files == null) {
                    return;
                } else {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isDirectory()) {
                            getDirectoryFilesImpl(files[i], out);
                        } else {
                            out.add(files[i]);
                        }
                    }
                }
            }
        }

        private void closeSilently (Closeable closeable){
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                }
            }
        }
    }*//*}*/
}
