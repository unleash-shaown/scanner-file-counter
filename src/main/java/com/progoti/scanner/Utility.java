/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progoti.scanner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Shaown
 */
public class Utility {
    public static final String DS_DIR_FILE_NAME = "dirFile" + File.separator + "dsdirInfo.txt";
    public static final String KYC_DIR_FILE_NAME = "dirFile" + File.separator + "kycdirInfo.txt";
    public static String latestDSFileName = "";
    public static String latestKYCFileName = "";

    public static List<String> getStringListFromFileName(){
        
        List<String> stringList = new ArrayList<>();
        if (new File(DS_DIR_FILE_NAME).exists()) {
            setValueToList(DS_DIR_FILE_NAME, stringList);
        }
        if (new File(KYC_DIR_FILE_NAME).exists()) {
            setValueToList(KYC_DIR_FILE_NAME, stringList);
        }
        return stringList;
    }
    
    public static String writeToFile(String value, boolean isDs, boolean isReset){
        String filePath = null;
        if(isDs){
            if(isReset){
                File file = getLatestFileFromDir(true);
                if(file != null){
                    value = String.valueOf(file.getAbsolutePath());
                }
                latestDSFileName = value;
            } else {
                filePath = DS_DIR_FILE_NAME;
            }
        } else {
            if(isReset){
                File file = getLatestFileFromDir(false);
                if(file != null){
                    value = String.valueOf(file.getAbsolutePath());
                }
                latestKYCFileName = value;
            } else {
                filePath = KYC_DIR_FILE_NAME;
            }
        }
        if(!isReset){
            File file = new File(filePath);
            if(!file.exists()){
                file.getParentFile().mkdirs();
            }
            try {
                Files.write(Paths.get(file.toURI()),
                        value.getBytes("utf-8"),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                //TODO: what to do
                return value;
            }

        }
        return value;
    }
    
    public static int getLatestCountFile(boolean isDs){
        String latestFileName = null;
        int count = 0;
        String dirPath;
        if(isDs){
            dirPath = DS_DIR_FILE_NAME;
            latestFileName = latestDSFileName;
        } else {
            dirPath = KYC_DIR_FILE_NAME;
            latestFileName = latestKYCFileName;
        }
        String value = getValueFromFilePath(dirPath);
        if(StringUtils.isBlank(value)){
            return count;
        } else {
            value = value.substring(value.indexOf(":")+1).trim();
        }
        File dir = new File(value);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return count;
        }
        Long lastModifiedTime = 0L;
        if(StringUtils.isNotBlank(latestFileName)){
            lastModifiedTime = getCreationTime(new File(latestFileName));
        }
        for (File file : files) {
            if (lastModifiedTime < getCreationTime(file)) {
                ++count;
            }
        }
        return count;
    }
    
    public static File getLatestFileFromDir(boolean isDs){
        String dirPath;
        if(isDs){
            dirPath = DS_DIR_FILE_NAME;
        } else {
            dirPath = KYC_DIR_FILE_NAME;
        }
        String value = getValueFromFilePath(dirPath);
        if(StringUtils.isBlank(value)){
            return null;
        } else {
            value = value.substring(value.indexOf(":")+1).trim();
        }
        File dir = new File(value);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
           if (getCreationTime(lastModifiedFile) < getCreationTime(files[i])) {
               lastModifiedFile = files[i];
           }
        }
        return lastModifiedFile;
    }
    
    private static void setValueToList(String filePath, List<String> stringList){
        if (new File(filePath).exists()) {
            try {
                List<String> readAllLines = Files.readAllLines(Paths.get(filePath), Charset.forName("ISO-8859-1"));
                for (String readAllLine : readAllLines) {
                    stringList.add(readAllLine.trim());
                }
            } catch (InvalidPathException | IOException ex) {
                //TODO: what to do 
            }
        }
    }

    public static String getValueFromFilePath(String filePath){
        List<String> stringList = new ArrayList<>();
        if (new File(filePath).exists()) {
            try {
                List<String> readAllLines = Files.readAllLines(Paths.get(filePath), Charset.forName("ISO-8859-1"));
                for (String readAllLine : readAllLines) {
                    stringList.add(readAllLine.trim());
                }
            } catch (InvalidPathException | IOException ex) {
                //TODO: what to do
            }
        }
        if(stringList.size() > 0){
            return stringList.get(0);
        }
        return null;
    }

    public static long getCreationTime(File path){
        Path filePath = Paths.get(path.getAbsolutePath());
        try {
            BasicFileAttributes attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
            FileTime fileTime = attributes.creationTime();
            return fileTime.toMillis();
        } catch (IOException e) {
            //TODO: what to do
        }
        return 0L;
    }
    
}
