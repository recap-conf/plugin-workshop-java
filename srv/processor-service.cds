using {sap.capire.incidents as my} from '../db/schema';
using {sap.attachments.Attachments} from`com.sap.cds/cds-feature-attachments`;

extend my.Incidents with {
  attachments : Composition of many Attachments;
}

service ProcessorService {

  entity Incidents as projection on my.Incidents;

  @readonly
  entity Customers as projection on my.Customers;

}

annotate ProcessorService.Incidents.conversation with @title: '{i18n>Converstation}';

extend projection ProcessorService.Customers with {
  firstName || ' ' || lastName as name : String
}

annotate ProcessorService.Incidents with @odata.draft.enabled;
annotate ProcessorService with @(requires: ['support']);

