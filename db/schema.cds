using { User, cuid, managed, sap.common.CodeList } from '@sap/cds/common';
namespace sap.capire.incidents;

/**
 * Incidents created by Customers.
 */
@title: '{i18n>Incident}'
entity Incidents : cuid, managed {
   customer     : Association to Customers @title: '{i18n>Customer}';
   title        : String  @title : '{i18n>Title}';
   urgency        : Association to Urgency default 'M';
   status         : Association to Status default 'N';
   conversation  : Composition of many {
    key ID    : UUID;
    timestamp : type of managed:createdAt;
    author    : type of managed:createdBy;
    message   : String;
  };
}

/**
 * Customers entitled to create support Incidents.
 */
entity Customers : cuid, managed {
  firstName     : String;
  lastName      : String;
  email         : EMailAddress;
  phone         : PhoneNumber;
  incidents     : Association to many Incidents on incidents.customer = $self;
}

entity Status : CodeList {
  key code: String enum {
      new = 'N';
      assigned = 'A';
      in_process = 'I';
      on_hold = 'H';
      resolved = 'R';
      closed = 'C';
  };
  criticality : Integer;
}

entity Urgency : CodeList {
  key code: String enum {
      high = 'H';
      medium = 'M';
      low = 'L';
  };
}

type EMailAddress : String;
type PhoneNumber : String;