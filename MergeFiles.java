package com.company;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Created by Administrator on 5/8/2018.
 */
public class MergeFiles {
    private String fileName1 = "merge1.txt";
    private String fileName2 = "merge2.txt";
    private String outputFileName = "merged.txt";

    public void prepareData() throws IOException {
        List<String> sortedList1 = Arrays.asList("A", "BAC" , "C" , "IBM", "RTN" );
        List<String> sortedList2 = Arrays.asList("AAPL", "BA" , "TRI" , "SNAP");
        writeToFile(sortedList1,fileName1);
        writeToFile(sortedList2,fileName2);
    }
    public void prepareDataWithDuplicate() throws IOException {
        List<String> sortedList1 = Arrays.asList("A", "BAC" , "C" , "IBM", "RTN" ,"SNAP");
        List<String> sortedList2 = Arrays.asList("AAPL", "BA" , "TRI" , "SNAP");
        writeToFile(sortedList1,fileName1);
        writeToFile(sortedList2,fileName2);
    }

    @Test
    public  void testMergeSortedFiles() {

        MergeFiles mergeFiles= new MergeFiles();
        try {
            mergeFiles.prepareData();
            mergeFiles.process();
            List<String> list=mergeFiles.getOutput();
            assertEquals(list.size(), 9);
            assertEquals(list.get(0), "A");
            assertEquals(list.get(1), "AAPL");
            list.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public  void testMergeSortedFilesDuplicate() {

        MergeFiles mergeFiles= new MergeFiles();
        try {
            mergeFiles.prepareDataWithDuplicate();
            mergeFiles.process();
            List<String> list=mergeFiles.getOutput();
            assertEquals(list.size(), 10);
            assertEquals(list.get(0), "A");
            assertEquals(list.get(1),"AAPL");
            assertEquals(list.get(8),"SNAP");
            list.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private List<String> getOutput() throws IOException {
        Stream<String> stream=null;
        List<String> list=new ArrayList<>();
        try {
            stream = Files.lines(Paths.get(outputFileName));

            stream.forEach(list::add);
        } finally {
            if(stream!=null)
                stream.close();
        }
        return list;
    }



    private void writeToFile(List<String> sortList,String fileName) throws IOException {
        Path path = Paths.get(fileName);
        Files.write(path, sortList);
    }

    private void process() throws Exception {
        BufferedWriter writer=null;
        Scanner scanner1=null;
        Scanner scanner2=null;
        try {
            writer = Files.newBufferedWriter(Paths.get(outputFileName));
            scanner1 = new Scanner(new File(fileName1));
            scanner2 = new Scanner(new File(fileName2));
            boolean bStop=false;
            String line1 = scanner1.nextLine();
            String line2 = scanner2.nextLine();
            while (!bStop) {

                if(line1!=null && line2 !=null){
                    if(line1.compareTo(line2)<0){
                        line1 = getString(writer, scanner1, line1);
                    }else{
                        line2 = getString(writer, scanner2, line2);
                    }
                }else if(line1!=null){
                    line1 = getString(writer, scanner1, line1);
                }else if(line2!=null){
                    line2 = getString(writer, scanner2, line2);
                }else{
                    bStop=true;
                }
            }
        } finally {
            if(scanner1!=null)scanner1.close();
            if(scanner2!=null)scanner2.close();
            if(writer!=null){
                writer.flush();
                writer.close();
            }
        }
    }

    private String getString(BufferedWriter writer, Scanner scanner, String line) throws IOException {
        writer.write(line);
        writer.write("\n");
        if(scanner.hasNext()) {
            line = scanner.nextLine();
        }else{
            line=null;
        }
        return line;
    }
}
