package se.skl.skltpservices.adapter.fk.recmedcertquestion;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.mule.api.ExceptionPayload;
import org.mule.api.MuleMessage;
import org.mule.api.routing.ResponseTimeoutException;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;
import org.mule.transport.NullPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.fk.vardgivare.sjukvard.taemotfragaresponder.v1.TaEmotFragaResponseType;
import se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificatequestionresponder.v1.ReceiveMedicalCertificateQuestionResponseType;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultCodeEnum;

public class VardResponse2FkTransformer extends AbstractMessageAwareTransformer
{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public VardResponse2FkTransformer()
    {
        super();
        registerSourceType(Object.class);
        setReturnClass(Object.class); 
    }
    
	public Object transform(MuleMessage message, String outputEncoding) throws TransformerException {
		
		logger.info("Entering vard2fk receive medical certificate question transform");

		boolean faultDetected = false;
		
    	Object src = message.getPayload();
		try {						
			// Take care of any error message and send it back as a SOAP Fault!
			if (src instanceof NullPayload) {
			    // We got a null-payload, let's see if there is an exception-payload instead...
		        ExceptionPayload ep = message.getExceptionPayload();
		        if (ep != null) {
		            String errorMessage = ep.getCode() + ": " + ep.getMessage();

		            Throwable t = ep.getException();
		            if (t instanceof ResponseTimeoutException) {
		            	src = errorMessage + ". Unknown error.";
		            } else {
		                src = errorMessage + ". Timeout";
		            }
		            
		            // Remove the ExceptionPayload!
		            message.setExceptionPayload(null);
		        }				
	            faultDetected = true;
			} else if(!(src instanceof ReceiveMedicalCertificateQuestionResponseType)) {
				src = "Payload type not supported: "+ message.getPayload().getClass();
				faultDetected = true;
			}
			
			StringBuffer result = new StringBuffer();
			
			// First create the content in the body, either a fault or the response
			if (faultDetected) {
				// Strip off xml processing instructions if any
				String payload = (String)src;
				if (payload.startsWith("<?")) {
					int pos = payload.indexOf("?>");
					payload = payload.substring(pos + 2);
				}

				createSoapFault(payload, result);
			} else {
				ReceiveMedicalCertificateQuestionResponseType inResponse = (ReceiveMedicalCertificateQuestionResponseType)src;

	            // Create new JAXB object for the outgoing data
				TaEmotFragaResponseType outResponse = new TaEmotFragaResponseType();
				
				// Handle error if ResultCode == ERROR!
				if (inResponse.getResult().getResultCode().equals(ResultCodeEnum.ERROR)) {
					// Get Error text if any
					String errorText ="";
					if (inResponse.getResult().getErrorText() != null && inResponse.getResult().getErrorText().length() > 0) {
						errorText = inResponse.getResult().getErrorText();
					}
					createSoapFault("Error: " + errorText, result);
					logger.debug("Return SOAP Envelope: {}", result.toString());
					return result.toString();					
				}
				
				// Transform the JAXB object into a XML payload
	            StringWriter writer = new StringWriter();
	        	Marshaller marshaller = JAXBContext.newInstance(TaEmotFragaResponseType.class).createMarshaller();
	        	marshaller.marshal(new JAXBElement(new QName("urn:riv:fk:vardgivare:sjukvard:TaEmotFraga:1:rivtabp20", "TaEmotFragaResponse"), TaEmotFragaResponseType.class, outResponse), writer);
				logger.debug("Extracted information: {}", writer.toString());
				String payload = (String)writer.toString();
				if (payload.startsWith("<?")) {
					int pos = payload.indexOf("?>");
					payload = payload.substring(pos + 2);
				}

				writer.close();
				result.append(payload);
			}

			// Done, return the string
			String resultStr = result.toString();
			logger.debug("Return SOAP Envelope: {}", resultStr);
			
			logger.info("Exiting vard2fk receive medical certificate question transform");
			
			return resultStr;

		} catch (Exception e) {
	        throw new TransformerException(this, e);
		}
	}
			
	private void createSoapFault(String errorText, StringBuffer result) {
		result.append("<soap:Fault xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>");
		result.append("<faultcode>soap:Server</faultcode>");
		result.append("<faultstring>VP009 Exception when calling the service producer: " + errorText + "</faultstring>");
		result.append("</soap:Fault>");
	}	
}