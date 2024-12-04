package com.wireshield.localfileutils;

/**
 * The File class is a representation of a file.
 */
public class File {
    private String name;
    private long size;
    
    /**
     * The constructor of the File class.
     */
    public File(){
    }

    /**
     * Method to check if the file is valid.
     * 
     * @return Boolean
     *   True if the file is valid, false otherwise.
     */
    protected Boolean isValid(){
        return null;
    }

    /**
     * Method to get the name of the file.
     * 
     * @return String
     *   The name of the file.
     */
    protected String getName(){
        return name;
    }

    /**
     * Method to get the size of the file.
     * 
     * @return long
     *   The size of the file.
     */
    protected long getFile(){
        return size;
    }
}
