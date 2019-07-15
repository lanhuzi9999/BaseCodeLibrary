package com.example.basecodelibrary.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ApkSignature {

    /**
     * parse APK file, read content from signature file
     *
     * @param srcfilepath
     * @return content read from signature file
     * @throws ApkSignatureException
     * @throws ApkSignatureException
     */
    private static byte[] parsePackage(String srcfilepath) throws ApkSignatureException {
        File tempapk = new File(srcfilepath);
        InputStream ip = null;
        int errorcode = 0;

        try {
            ZipFile apkzip = new ZipFile(tempapk);
            // ZipEntry certentry = apkzip.getEntry("META-INF/CERT.SF");
            ZipEntry certentry = null;

            if (certentry == null) {

                long lastModified = 0;

                // find another SF file if CERT.SF not exists
                Enumeration<? extends ZipEntry> e = apkzip.entries();
                while (e.hasMoreElements()) {
                    ZipEntry sfentry = e.nextElement();
                    if (sfentry.getName().startsWith("META-INF/") && sfentry.getName().endsWith(".SF")) {

                        if (sfentry.getTime() > lastModified) {
                            certentry = sfentry;
                            lastModified = sfentry.getTime();
                        }

                        Log.d("ApkSignature", "sf entry: " + sfentry.getName() + " - " + sfentry.getTime());

                    }
                }

                if (certentry == null) {
                    throw new ApkSignatureException(1, "no signature file (cert.sf) found");
                }
            }

            ip = apkzip.getInputStream(certentry);
            byte[] buffer = new byte[(int) certentry.getSize()];
            int readCount = 0;
            while (readCount < certentry.getSize()) {
                readCount += ip.read(buffer, readCount, (int) (certentry.getSize() - readCount));
            }
            ip.close();
            return buffer;

        } catch (ZipException e) {
            e.printStackTrace();
            errorcode = 2;

        } catch (IOException e) {
            e.printStackTrace();
            errorcode = 3;
        }

        try {
            if (ip != null) {
                ip.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorcode = 3;
        }

        switch (errorcode) {
            case 2:
                throw new ApkSignatureException(2, "zip failure");
            case 3:
                throw new ApkSignatureException(3, "io operation failure(file may not exists)");
        }

        return null;
    }

    /**
     * create MD5 digest and convert to Hex String
     *
     * @param s : source string
     * @return md5 and hex string
     * @throws NoSuchAlgorithmException
     */
    private static String md5Hex(byte[] s) throws NoSuchAlgorithmException {

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(s);

        byte[] sig = digest.digest();

        char str[] = new char[16 * 2];

        int k = 0;
        for (int i = 0; i < 16; i++) {
            byte byte0 = sig[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }

        return new String(str);
    }

    public static String md5Hex(String file) throws NoSuchAlgorithmException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return md5Hex(fis);
        } catch (Exception e) {
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static String md5Hex(InputStream is) throws NoSuchAlgorithmException {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] bytes = new byte[1024 * 1024];
        int byteCount;
        try {
            while ((byteCount = is.read(bytes)) > 0) {
                digest.update(bytes, 0, byteCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] sig = digest.digest();

        char str[] = new char[16 * 2];

        int k = 0;
        for (int i = 0; i < 16; i++) {
            byte byte0 = sig[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

    //必须是存在classes.dex字段才取MD5,否则返回空串
    public static String getMD5(String apkfile) throws NoSuchAlgorithmException, ApkSignatureException {
        try {
            ZipFile apkzip = new ZipFile(apkfile);
            ZipEntry dexentry = apkzip.getEntry("classes.dex");
            if (dexentry == null) {
                return "";        // 不取MD5
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] content = parsePackage(apkfile);

        if (content == null) {
            return null;
        }

        return md5Hex(content);
    }

    /**
     * get APK Digest
     *
     * @param apkfile : apk file path
     * @return digest string ( null: error)
     * @throws NoSuchAlgorithmException
     * @throws ApkSignatureException
     * @throws ApkSignatureException
     */
    public static String getDigest(String apkfile) throws NoSuchAlgorithmException, ApkSignatureException {
        byte[] content = parsePackage(apkfile);

        if (content == null) {
            return null;
        }

        return md5Hex(content);
    }
//
//    /**
//     * main function: for test
//     *
//     * @param args
//     */
//    public static void main(String[] args) {
//
//        String srcfile = "e:\\temp\\shsishi.apk";
//        String dig = null;
//        try {
//            dig = ApkSignature.getDigest(srcfile);
//
//        } catch (ApkSignatureException e) {
//            System.out.println("errorcode: " + e.m_errorCode);
//            System.out.println("errormessage: " + e.m_errorString);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(dig.toCharArray());
//    }

}
