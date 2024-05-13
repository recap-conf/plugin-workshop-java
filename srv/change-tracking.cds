using { ProcessorService.Incidents } from './processor-service';
using { sap.capire.incidents as model } from '../db/schema';
using { sap.changelog as changelog } from 'com.sap.cds/change-tracking';

extend model.Incidents with changelog.changeTracked;

annotate Incidents with {
  customer @changelog: [customer.name];
  title    @changelog;
  status   @changelog;
}

annotate Incidents.conversation with @changelog: [author, timestamp] {
  message  @changelog @Common.Label: 'Message';
}
