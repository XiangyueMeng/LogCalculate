package calculate;

import com.csvreader.CsvWriter;

import java.io.*;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    public static void main(String[] args){
        Calculator calculator = new Calculator();
        calculator.calculate(args[0], Integer.valueOf(args[1]), Integer.valueOf(args[2]));

    }

    void calculate(String filename, int start, int end){
        long totalTime = 0;
        long totalTxs = 0;
        int num = 0;
        int numNotZero = 0;
        int totalTimeWithoutZero = 0;
        int totalTimeOver300 = 0;
        int numOver300 = 0;
        File csvFile = new File("./" + filename + ".csv");
        csvFile.delete();
        try{
            csvFile.createNewFile();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        CsvWriter csvWriter = new CsvWriter(csvFile.getAbsolutePath(),',', Charset.forName("UTF-8"));
        String[] csvHeaders = { "BlockNumber", "Time", "Txs" };
        Pattern pattern = Pattern.compile("pushBlock block number:(\\d+), cost/txs:(\\d+)/(\\d+)");
        try {
            csvWriter.writeRecord(csvHeaders);
            FileReader fileReader = new FileReader(filename);
            BufferedReader br = new BufferedReader(fileReader);
            String str;
            while((str = br.readLine())!= null){
                if(str.contains("pushBlock")){
                    Matcher matcher = pattern.matcher(str);
                    if(matcher.find()) {
                        int blockNumber = Integer.valueOf(matcher.group(1));
                        if(blockNumber >= start && blockNumber <= end){
                            int time = Integer.valueOf(matcher.group(2));
                            int txs = Integer.valueOf(matcher.group(3));
                            num++;
                            totalTime += time;
                            totalTxs += txs;
                            String[] csvContent = {String.valueOf(blockNumber),String.valueOf(time),String.valueOf(txs)};
                            csvWriter.writeRecord(csvContent);
                            if(txs != 0){
                                numNotZero++;
                                totalTimeWithoutZero += time;
                            }
                            if(txs >= 300){
                                numOver300++;
                                totalTimeOver300 += time;
                            }
                        }
                    }
                }
            }
            csvWriter.close();
            long avgtime = totalTime/num;
            long avgtxs = totalTxs/num;
            long avgTimeWithout = totalTimeWithoutZero/numNotZero;
//            long avgTimeForZero = (totalTime - totalTimeWithoutZero) / (num - numNotZero);
            long avgTimeOver300 = totalTimeOver300 / numOver300;
            long tps = totalTxs*1000/totalTime;
            System.out.println("Total block number: " + num);
            System.out.println("Total time: " + totalTime);
            System.out.println("Total txs: " + totalTxs);
            System.out.println("Avg time: " + avgtime);
            System.out.println("Avg txs: " + avgtxs);
            System.out.println("Number of block that has txs:" + numNotZero);
            System.out.println("Avg time of block has txs:" + avgTimeWithout);
//            System.out.println("Avg time of block has no txs:" + avgTimeForZero);
            System.out.println("Avg time of block has over 300 txs:" + avgTimeOver300);
            System.out.println("tps:" + tps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
