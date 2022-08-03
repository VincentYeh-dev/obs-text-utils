import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;

public class MainProgram {

    public static void main(String[] args) throws IOException, InterruptedException {
        String[] strings=args[0].split(":");
        File path=new File(args[1]);
        int[] numbers=new int[3];
        for(int i=0;i<3;i++){
            numbers[i]=Integer.parseInt(strings[i]);
        }

        Date goalTime=getGoalTime(numbers[0],numbers[1],numbers[2]);

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat format= new SimpleDateFormat(args[2]);

        while(goalTime.after(new Date())){
            calendar.setTime(goalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY,-calendar2.get(Calendar.HOUR_OF_DAY));
            calendar.add(Calendar.MINUTE,-calendar2.get(Calendar.MINUTE));
            calendar.add(Calendar.SECOND,-calendar2.get(Calendar.SECOND));
            String output=format.format(calendar.getTime());
            System.out.println(output);
            try(BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path,false), StandardCharsets.UTF_8))){
                writer.write(output);
            }

            Thread.sleep(1000);
        }
    }
    public static Date getGoalTime(int hours,int minutes,int seconds){
        Calendar calendar =Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR_OF_DAY,hours);
        calendar.add(Calendar.MINUTE,minutes);
        calendar.add(Calendar.SECOND,seconds);
        return calendar.getTime();
    }
}
