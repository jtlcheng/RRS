import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @package: PACKAGE_NAME
 * @Author: cheng
 * @Date: 2022-09-27 15:08
 **/
public class test {
    public static void main(String[] args) {
        Date date=new Date();
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,1);
        date=calendar.getTime();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(date);
        Date parse;
        {
            try {
                parse = simpleDateFormat.parse(format);
                System.out.println(parse);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}
