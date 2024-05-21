package com.sap.cap.incident_management.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.services.EventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.sap.changelog.Changes;

@Component
@ServiceName("ChangeTrackingService$Default")
public class ChangeLogHandler implements EventHandler {

	private static final Logger logger = LoggerFactory.getLogger(ChangeLogHandler.class);

	@After(event = "createChanges")
	void afterCreate(EventContext context) {
		var result = (Result) context.get("result");
		result.listOf(Changes.class).forEach(c ->
			logger.info("Change: {} at {} by {}", c.getId(), c.getCreatedAt(), c.getCreatedBy()));
	}
}
