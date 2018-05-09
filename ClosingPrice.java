package com.company;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ClosingPrice {
    private static final String newLine = System.getProperty("line.separator");
    private Pattern cusipPattern =Pattern.compile("^[a-zA-Z0-9]{8}$");
    private Pattern pricePattern =Pattern.compile("^[0-9.]{1,12}$");
    private String fileName="tickers.txt";

    public void prepareData() throws IOException {
        List<String> source = Arrays.asList("A0000000", "12.2", "13.2", "IBM00000", "200.02","198.4","A0000000","12.5","A0000000","12.25");
        source.forEach(line -> line.concat(newLine));
        Files.write(Paths.get(fileName), source);
    }
    public void prepareDataExtraTicker() throws IOException {
        List<String> source = Arrays.asList("A0000000", "12.2", "13.2", "IBM00000", "200.02","198.4","A0000000","12.5","A0000000","12.25","A0000000");
        source.forEach(line -> line.concat(newLine));
        Files.write(Paths.get(fileName), source);
    }

    public void prepareDataExtraLongPrice() throws IOException {
        List<String> source = Arrays.asList("A0000000", "12.200000011111", "13.2", "IBM00000", "200.02","198.4","A0000000","12.5","A0000000","12.25","A0000000");
        source.forEach(line -> line.concat(newLine));
        Files.write(Paths.get(fileName), source);
    }

    @Test
    public void testClosingPrice(){
        ClosingPrice closingPrice= new ClosingPrice();
        try {
            closingPrice.prepareData();
            Map<String,BigDecimal> map=closingPrice.process();
            assertTrue(map.containsKey("A0000000"));
            assertTrue(map.containsKey("IBM00000"));
            assertTrue( map.get("IBM00000").equals(new BigDecimal("198.4")));
            assertTrue( map.get("A0000000").equals(new BigDecimal("12.25")));
            closingPrice.printOutput(map);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testClosingPriceExtraTicker(){
        ClosingPrice closingPrice= new ClosingPrice();
        try {
            closingPrice.prepareDataExtraTicker();
            Map<String,BigDecimal> map=closingPrice.process();
            assertTrue(map.containsKey("A0000000"));
            assertTrue( map.containsKey("IBM00000"));
            assertTrue( map.get("IBM00000").equals(new BigDecimal("198.4")));
            assertTrue( map.get("A0000000").equals(new BigDecimal("12.25")));
            closingPrice.printOutput(map);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testClosingPriceExtraLongPrice(){
        ClosingPrice closingPrice= new ClosingPrice();
        try {
            closingPrice.prepareDataExtraLongPrice();
            Map<String,BigDecimal> map=closingPrice.process();
            assertTrue( "Failed to detect an extra long price format",false);
            //closingPrice.printOutput(map);
        } catch (NumberFormatException e) {
            //expected result;
        } catch (Exception e) {
            assertTrue("Failed to read input file", false);
        }
    }
    private void printOutput(Map<String, BigDecimal> map) {
        map.forEach((k, v) -> System.out.println("CUSIP : " + k + " Closing price : " + v));
    }

    private Map<String, BigDecimal> process() throws Exception {
        Map<String, BigDecimal> sortedTickers = new HashMap<>();
        Scanner scanner=null;
        String currentTicker=null;
        BigDecimal currentTickerPrice;
        try {
            scanner = new Scanner(new File(fileName));
            while ( scanner.hasNextLine() ) {
                String line = scanner.nextLine();
                if(line.length()==0) continue;
                if(isPrice(line)){
                    currentTickerPrice=new BigDecimal(line);
                    if(currentTicker==null) {
                        throw new Exception("Price must follow a ticker");
                    }
                    sortedTickers.put(currentTicker, currentTickerPrice);
                }else{
                    currentTicker=line;
                }

            }
            //return sortedTickers;
        }catch (NumberFormatException e){
            System.out.println("Invalid price format");
            throw  e;
        } finally {
            if(scanner!=null) {
                scanner.close();
            }
        }
        return sortedTickers;
    }

    private boolean isPrice(String line) {
        if(line!=null &&  cusipPattern.matcher(line).matches() )
            return false;
        else if(line!=null && pricePattern.matcher(line).matches()){
            return true;
        }else{
            throw new NumberFormatException();
        }

    }
}
