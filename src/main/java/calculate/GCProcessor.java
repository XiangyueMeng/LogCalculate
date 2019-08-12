package calculate;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// this class are used to filter the gc log according to the time
// since we only want to check the gc process during the highest pressure, we need to screen out
// gc log of other time period
public class GCProcessor {
    public static void main(String[] args){
        GCProcessor gcProcessor = new GCProcessor();
//        gcProcessor.process(args[0], args[1], args[2]);
        gcProcessor.processWithNewFormat(args[0], args[1], args[2]);
    }

// this method are used to process the gc log of the original start.sh
// in this format, the head of the log file shows the gc parameters and the following
// logs will start with the time stamp, so we use the time to filter the log
    void process(String filename, String start, String end){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
        SimpleDateFormat formatForLog = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        //2019-08-05T13:44:24.765+0800
        try {
            Date startDate = format.parse(start);
            Date endDate = format.parse(end);
            File newfile = new File("./processed.log");
            FileWriter fileWriter = new FileWriter(newfile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            FileReader fileReader = new FileReader(filename);
            BufferedReader br = new BufferedReader(fileReader);
            String str;
            while((str = br.readLine())!= null){
                if(!str.startsWith("2019")){
                    bufferedWriter.write(str);
                    bufferedWriter.newLine();
                }
                else{
                    String dateStr = str.split(": ")[0];
                    Date logDate = formatForLog.parse(dateStr);
                    if(logDate.compareTo(startDate) > 0 && logDate.compareTo(endDate) < 0){
                        bufferedWriter.write(str);
                        bufferedWriter.newLine();
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

//  after add -XX:+PrintHeapAtGC -XX:+PrintTenuringDistribution parameter, the gc log will print more details
//  one log will separate into multiple lines, according to the format regulation, use this new method to filter the log
    void processWithNewFormat(String filename, String start, String end){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
        SimpleDateFormat formatForLog = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        //2019-08-05T13:44:24.765+0800
        try {
            Date startDate = format.parse(start);
            Date endDate = format.parse(end);
            File newfile = new File("./processed.log");
            FileWriter fileWriter = new FileWriter(newfile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            FileReader fileReader = new FileReader(filename);
            BufferedReader br = new BufferedReader(fileReader);
            String str;
            while((str = br.readLine())!= null){
                if(str.startsWith("{")){
                    String strInside;
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append("\n");
                    boolean flag = false;
                    while((strInside = br.readLine()) != null){
                        sb.append(strInside);
                        sb.append("\n");
                        if(strInside.startsWith("}")){
                            break;
                        }
                        else if(strInside.startsWith("2019")){
                            String dateStr = strInside.split(": ")[0];
                            Date logDate = formatForLog.parse(dateStr);
                            if(logDate.compareTo(startDate) > 0 && logDate.compareTo(endDate) < 0){
                                flag = true;
                            }
                        }
                    }
                    if(flag){
                        bufferedWriter.write(sb.toString());
//                        bufferedWriter.newLine();
                    }
                }
                else if(str.startsWith("2019")){
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append("\n");
                    String strInside;
                    if(str.endsWith(":")){
                        while((strInside = br.readLine()) != null){
                            sb.append(strInside);
                            sb.append("\n");
                            if(strInside.startsWith("}")){
                                break;
                            }
                        }
                    }
                    String dateStr = str.split(": ")[0];
                    Date logDate = formatForLog.parse(dateStr);
                    if(logDate.compareTo(startDate) > 0 && logDate.compareTo(endDate) < 0){
                        bufferedWriter.write(sb.toString());
//                        bufferedWriter.newLine();
                    }
                }
                else{
                    bufferedWriter.write(str);
                    bufferedWriter.newLine();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
