using {sap.capire.incidents as model} from '../db/schema';
using {ProcessorService} from './processor-service';

annotate model.Customers with @PersonalData: {
  EntitySemantics: 'DataSubject',
  DataSubjectRole: 'Customer'
} {
  ID        @PersonalData.FieldSemantics   : 'DataSubjectID';
  firstName @PersonalData.IsPotentiallyPersonal;
  lastName  @PersonalData.IsPotentiallyPersonal;
  email     @PersonalData.IsPotentiallyPersonal;
  phone     @PersonalData.IsPotentiallyPersonal;
}

annotate ProcessorService.Customers with {
  name @PersonalData.IsPotentiallySensitive;
}
