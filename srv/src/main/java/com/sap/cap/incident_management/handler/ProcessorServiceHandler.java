package com.sap.cap.incident_management.handler;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import com.sap.cds.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.*;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.processorservice.Incidents;
import cds.gen.processorservice.Urgency;
import cds.gen.processorservice.ProcessorService_;
import cds.gen.processorservice.Incidents_;

@Component
@ServiceName(ProcessorService_.CDS_NAME)  
public class ProcessorServiceHandler implements EventHandler {

/*
 * Change the urgency of an incident to "high" if the title contains the word "urgent"
 */
	private static final Logger logger = LoggerFactory.getLogger(ProcessorServiceHandler.class);
	@Before(event = CqnService.EVENT_CREATE)  
	public void ensureHighUrgencyForIncidentsWithUrgentInTitle(List<Incidents> incidents) {  
		for (Incidents incident : incidents) { 
            System.out.println(incident.getTitle());
			if (incident.getTitle().toLowerCase(Locale.ENGLISH).contains("urgent") &&
            incident.getUrgencyCode() == null || !incident.getUrgencyCode().equals("H")) {  
                incident.setUrgencyCode("H");  
				logger.info("Adjusted Urgency for incident '{}' to 'HIGH'.", incident.getTitle());  
			}  

		}  
	}
/*
 * Handler to avoid updating a "closed" incident
 */
    @Before(event = CqnService.EVENT_UPDATE)  
	public void onUpdate(Incidents incident) { 
        
        if(incident.getStatusCode().equals("C")){
            throw new ServiceException(ErrorStatuses.CONFLICT, "Can't modify a closed incident");
        }
          
	}


    
    
	
}