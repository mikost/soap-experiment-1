package name.mikkoostlund.experimental.soap_experiment_1;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import name.mikkoostlund.ns.experimental.soap_experiment_1.MyEndpoint;

public class App 
{
    private static final String SERVICE_ADDRESS = "http://localhost:8080/svc/hello";
	private static final String MY_SERVICE_XSD = "/wsdl/MyService.xsd";

	public static void main( String[] args ) throws MalformedURLException
    {
		// Publish web service
		Endpoint ep = Endpoint.create(new MyEndpointImpl());
    	Source source = sourceFromResource(MY_SERVICE_XSD);
    	ep.setMetadata(Arrays.asList(source));
    	ep.publish(SERVICE_ADDRESS);

    	// Send 'sayHello' message to web service and print the result/response.
    	URL wsdlDocumentLocation = new URL(SERVICE_ADDRESS + "?wsdl");
        QName serviceName = new QName("http://mikkoostlund.name/ns/experimental/soap-experiment-1", "MyService");
        QName portName = new QName("http://mikkoostlund.name/ns/experimental/soap-experiment-1", "MyHttpBoundEndpointPort");
        Service service = Service.create(wsdlDocumentLocation, serviceName);
        MyEndpoint myEndpoint = service.getPort(portName, MyEndpoint.class);
		String message;
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
