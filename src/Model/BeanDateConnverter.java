package Model;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

public class BeanDateConnverter implements Converter {
	protected static final Logger logger = Logger.getLogger(BeanDateConnverter.class);
	public static final String[] ACCEPT_DATE_FORMATS = {
		"yyyy-MM-dd HH:mm:ss",
		"yyyy-MM-dd"
	};

	public BeanDateConnverter() {
	}

	public Object convert(Class arg0, Object value) {
		logger.debug("conver " + value + " to date object");
		String dateStr=value.toString();
		dateStr=dateStr.replace("T", " ");
		try{
			return DateUtils.parseDate(dateStr, ACCEPT_DATE_FORMATS);
		}catch(Exception ex){
			logger.debug("parse date error:"+ex.getMessage());
		}
		return null;
	}
}