package name.mikkoostlund.experimental.soap_experiment_1;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class Util {
	public static Source sourceFromURL(URL url) {
    	String systemId = url.toExternalForm();
    	InputStream inputStream;
    	try {
			inputStream = url.openStream();
		} catch (IOException e) {
			throw new RuntimeException("Failed to create InputStream from URL \""+ url +"\"", e);
		}
    	return new StreamSource(inputStream, systemId);
	}
}
