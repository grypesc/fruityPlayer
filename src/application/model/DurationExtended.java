package application.model;
import javafx.util.Duration;

public class DurationExtended extends javafx.util.Duration {

	private static final long serialVersionUID = 1L;

	public DurationExtended(double arg0) {
		super(arg0);
	}
	
	public static String toMinutesAndSeconds(Duration duration)
	{
	    int seconds = Math.round((float)duration.toSeconds());
	    int minutes=seconds/60;
	    String result;
	    if (seconds%60<=9)
	    	result = String.valueOf(minutes)+":0"+String.valueOf(seconds%60);
	    else
	    	result = String.valueOf(minutes)+":"+String.valueOf(seconds%60);
	    return result;
	}

}
