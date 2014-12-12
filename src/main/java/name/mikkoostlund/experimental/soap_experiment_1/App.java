package name.mikkoostlund.experimental.soap_experiment_1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import name.mikkoostlund.ns.experimental.soap_experiment_1.MyEndpoint;
import name.mikkoostlund.ns.experimental.soap_experiment_1.SayHelloFault;
import name.mikkoostlund.ns.experimental.soap_experiment_1.SayHelloFaultBean;

@SuppressWarnings("restriction")
public class App 
{
    private static final String MY_SERVICE_WSDL = "/wsdl/MyService.wsdl";
    private static final String MY_SERVICE_XSD = "/wsdl/MyService.xsd";
	private static final URL MY_SERVICE_XSD_URL = App.class.getResource(MY_SERVICE_XSD);

	public static void main( String[] args ) throws IOException
    {
		// Publish web service
		HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(8080), 5);
	    server.start();

	    HttpContext context = server.createContext("/svc/hello");
	    context.setAuthenticator(new com.sun.net.httpserver.BasicAuthenticator("test") {

			@Override
			public boolean checkCredentials(String username, String password) {
				if ("mikko".equals(username) && "test".equals(password)) {
					return true;
				}
				return false;
			}
	    	
	    });

	    // Publish web service
		Endpoint ep = Endpoint.create(new MyEndpointImpl());
    	final Source schemaSource = Util.sourceFromURL(MY_SERVICE_XSD_URL);
    	ep.setMetadata(Arrays.asList(schemaSource));

    	@SuppressWarnings("rawtypes")
		List<Handler> chain = ep.getBinding().getHandlerChain();
		chain.add(new MyXMLValidatorHandler(MY_SERVICE_XSD_URL));
		ep.getBinding().setHandlerChain(chain);

		ep.publish(context);

    	// Send 'sayHello' message to web service and print the result/response.
    	URL wsdlDocumentLocation = App.class.getResource(MY_SERVICE_WSDL);
        QName serviceName = new QName("http://mikkoostlund.name/ns/experimental/soap-experiment-1", "MyService");
        QName portName = new QName("http://mikkoostlund.name/ns/experimental/soap-experiment-1", "MyHttpBoundEndpointPort");
        Service service = Service.create(wsdlDocumentLocation, serviceName);
        MyEndpoint myEndpoint = service.getPort(portName, MyEndpoint.class);
        BindingProvider bp = ((BindingProvider)myEndpoint);
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "mikko");
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "test");
		String message;
		try {
			message = myEndpoint.sayHello(8);
		} catch (SayHelloFault e) {
			SayHelloFaultBean faultBean = e.getFaultInfo();
			message = "Fault returned: " + faultBean.getErrorMessage();
		}catch (WebServiceException e) {
			message = "Operation \"sayHello\" failed. Details: "+ e.getClass().getSimpleName() +": "+ e.getMessage();
		} 
		System.out.println(message);
		try {
			message = myEndpoint.sayHello(7);
		} catch (SayHelloFault e) {
			SayHelloFaultBean faultBean = e.getFaultInfo();
			message = "Fault returned: " + faultBean.getErrorMessage();
		}catch (WebServiceException e) {
			message = "Operation \"sayHello\" failed. Details: "+ e.getClass().getSimpleName() +": "+ e.getMessage();
		} 
		System.out.println(message);
    }
}
