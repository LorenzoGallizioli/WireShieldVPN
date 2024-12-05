package com.wireshield.localfileutils;

import com.wireshield.localfileutils.File;
import com.wireshield.enums.runningStates;

/**
 * The DownloadManager class is responsible for managing the downloaded files.
 */
public class DownloadManager {
    private String downloadPath;
    private File[] downloadedFiles;
    private runningStates monitorStatus;
    private Boolean newFileInBuffer;

    /**
     * Constructor for the DownloadManager class.
     */
    public DownloadManager(){

    }

    /**
     * Method to detect new files in the download directory.
     * 
     * @param file
     *   The file to be detected.
     */
    protected void fileDetector(File file){
        
    }

    /**
     * Method to get the downloaded files.
     * 
     * @return File[]
     *   The downloaded files.
     */
    protected File[] getFiles() {
        return downloadedFiles;
    }

    /**
     * Method to get the download manager status.
     * 
     * @return runningStates 
     *   The download manager status.
     */
    protected runningStates getStatus(){
        return monitorStatus;
    }

    /**
     * Method to get the file by name.
     * 
     * @param file_name
     *   The name of the file.
     * 
     * @return File
     *   The file.
     */
    protected File getFileByName(String file_name){
        return null;
    }

    /**
     * Method to delete a file.
     * 
     * @param file
     *   The file to be deleted.
     */
    protected void deleteFile(File file){

    }

}
