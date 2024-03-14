package nl.vu.cs.softwaredesign;

import java.io.*;
import java.util.zip.ZipOutputStream;

class Zip extends Format{
    @Override
    void compress(File archiveName, File[] fileNames, Config config){
        try {
            FileOutputStream outputFile = new FileOutputStream(archiveName);
            ZipOutputStream zippedOutput = new ZipOutputStream(outputFile);
            //case compress folder
            //compress file
        }catch(IOException exception){

        }
    }

    @Override
    void decompress(File archiveName, File outputDir) {

    }

    @Override
    File[] getFileNames(File archiveName) {
        return new File[0];
    }
}
