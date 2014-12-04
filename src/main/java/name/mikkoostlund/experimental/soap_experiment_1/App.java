package name.mikkoostlund.experimental.soap_experiment_1;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceException;

import name.mikkoostlund.ns.experimental.soap_experiment_1.MyEndpoint;
import name.mikkoostlund.ns.experimental.soap_experiment_1.MyService;

public class App 
{
    private static final String SERVICE_ADDRESS = "http://localhost:8080/svc/hello";
	private static final String MY_SERVICE_XSD = "/wsdl/MyService.xsd";

	public static void main( String[] args ) throws MalformedURLException
    {
		Endpoint ep = Endpoint.create(new MyEndpointImpl());

    	Source source = sourceFromResource(MY_SERVICE_XSD);
    	ep.setMetadata(Arrays.asList(source));
    	ep.publish(SERVICE_ADDRESS);
    	
    	URL wsdlLocation = new URL(SERVICE_ADDRESS + "?wsdl");
		MyService myService = new MyService(wsdlLocation);
		MyEndpoint myEndpoint = myService.getMyHttpBoundEndpointPort();
		String message = null;
		try {
			message = myEndpoint.sayHello();
		} catch (WebServiceException e) {
			message = "Operation \"sayHello\" failed. Details: "+ e.getClass().getSimpleName() +": "+ e.getMessage();
		}
		System.out.println(message);
    }

	private static Source sourceFromResource(String name) {
		URL resource = App.class.getResource(name);
    	String systemId = resource.toExternalForm();
    	InputStream inputStream;
    	try {
			inputStream = resource.openStream();
		} catch (IOException e) {
			throw new RuntimeException("Failed to create InputStream from resource \""+ name +"\"", e);
		}
    	return new StreamSource(inputStream, systemId);
	}
}
