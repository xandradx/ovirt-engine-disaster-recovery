package extensions;

import play.Logger;
import play.templates.JavaExtensions;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringFormatExtensions extends JavaExtensions {
	
	public static String decimalFormat(Double number) {
		  DecimalFormat formatter = new DecimalFormat("#,###.00");
		  formatter.setMinimumIntegerDigits(1);
		  try {
			  return formatter.format(number);
		  } catch (Exception e) {
			  Logger.error(e, "Error formatting decimal");
		  }
		  
		  if (number==null) {
			  return "";
		  }
		  
		  return number.toString();
	}
}