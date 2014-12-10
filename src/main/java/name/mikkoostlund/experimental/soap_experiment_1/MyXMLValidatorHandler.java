package name.mikkoostlund.experimental.soap_experiment_1;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.xml.sax.SAXException;

public class MyXMLValidatorHandler implements LogicalHandler<LogicalMessageContext> {
	private URL schemaLocation;

	public MyXMLValidatorHandler(URL schemaLocation) {
		this.schemaLocation = schemaLocation;
	}
	
	@Override
	public boolean handleMessage(LogicalMessageContext context) {
		if (isOutboundMessage(context)) {
			return true;
		}
		try {
			Source xmlSchemaDoc = Util.sourceFromURL(schemaLocation);
			Source xmlDoc = context.getMessage().getPayload();
			xmlSchemaValidate(xmlSchemaDoc, xmlDoc);
			return true;
		} catch (SAXException e) {
			SOAPFactory factory;
			try {
				factory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
				SOAPFault fault = factory.createFault();
				fault.addFaultReasonText("Syntax error", Locale.ENGLISH);
				throw new SOAPFaultException(fault);
			} catch (SOAPException e1) {
				throw new RuntimeException(e1);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Boolean isOutboundMessage(LogicalMessageContext context) {
		return (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	}

	private void xmlSchemaValidate(Source xmlSchemaDoc, Source xmlDoc) throws SAXException, IOException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(xmlSchemaDoc);
		Validator validator = schema.newValidator();
		validator.validate(xmlDoc);
	}

	@Override
	public boolean handleFault(LogicalMessageContext context) {
		System.out.println("Whoha handleFault!");
		return true;
	}

	@Override
	public void close(MessageContext context) {
		System.out.println("Whoha close!");
	}
}
