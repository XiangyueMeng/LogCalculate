package calculate;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GCProcessor {
    public static void main(String[] args){
        GCProcessor gcProcessor = new GCProcessor();
        gcProcessor.process(args[0], args[1], args[2]);
    }
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
}
