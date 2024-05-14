using ProcessorService as service from '../../srv/processor-service';
using from '../../db/schema';

annotate service.Incidents with @(UI.SelectionFields: [
  status_code,
  urgency_code,
]);

annotate service.Incidents with {
  status @Common.Label: '{i18n>Status}'
};

annotate service.Incidents with {
  urgency @Common.Label: '{i18n>Urgency}'
};

annotate service.Incidents with {
  urgency @Common.ValueListWithFixedValues: true
};

annotate service.Urgency with {
  code @Common.Text: descr
};

annotate service.Incidents with @(UI.LineItem: [
  {
    $Type      : 'UI.DataField',
    Value      : status.descr,
    Label      : '{i18n>Status}',
    Criticality: status.criticality,
  },
  {
    $Type: 'UI.DataField',
    Value: urgency.descr,
    Label: '{i18n>Urgency}',
  },
  {
    $Type: 'UI.DataField',
    Value: customer.name,
    Label: '{i18n>Customer}',
  },
  {
    $Type: 'UI.DataField',
    Value: title,
    Label: '{i18n>Title}',
  },
]);

annotate service.Incidents with @(UI.HeaderInfo: {
  Title         : {
    $Type: 'UI.DataField',
    Value: title,
  },
  TypeName      : '',
  TypeNamePlural: '',
  Description   : {
    $Type: 'UI.DataField',
    Value: customer.name,
  },
  TypeImageUrl  : 'sap-icon://alert',
});

annotate service.Incidents with @(UI.Facets: [
  {
    $Type : 'UI.CollectionFacet',
    Label : '{i18n>Overview}',
    ID    : 'Overview',
    Facets: [
      {
        $Type : 'UI.ReferenceFacet',
        Label : '{i18n>GeneralInformation}',
        ID    : 'i18nGeneralInformation',
        Target: '@UI.FieldGroup#i18nGeneralInformation',
      },
      {
        $Type : 'UI.ReferenceFacet',
        Label : '{i18n>Details}',
        ID    : 'i18nDetails',
        Target: '@UI.FieldGroup#i18nDetails',
      },
    ],
  },
  {
    $Type : 'UI.ReferenceFacet',
    Label : 'Conversations',
    ID    : 'Conversations',
    Target: 'conversation/@UI.LineItem#Conversations',
  },
  {
    $Type : 'UI.ReferenceFacet',
    ID    : 'AttachmentsFacet',
    Label : '{i18n>attachmentsAndLinks}',
    Target: 'attachments/@UI.LineItem'
  }
]);

annotate service.Incidents with @(UI.FieldGroup #i18nDetails: {
  $Type: 'UI.FieldGroupType',
  Data : [
    {
      $Type: 'UI.DataField',
      Value: status_code,
    },
    {
      $Type: 'UI.DataField',
      Value: urgency_code,
    },
  ],
});

annotate service.Incidents with @(UI.FieldGroup #i18nGeneralInformation: {
  $Type: 'UI.FieldGroupType',
  Data : [
    {
      $Type: 'UI.DataField',
      Value: title,
    },
    {
      $Type: 'UI.DataField',
      Value: customer_ID,
      Label: '{i18n>Customer}',
    },
  ],
});

annotate service.Incidents with {
  customer @Common.Text: {
    $value                : customer.name,
    ![@UI.TextArrangement]: #TextOnly,
  }
};

annotate service.Incidents with {
  customer @(
    Common.ValueList               : {
      $Type         : 'Common.ValueListType',
      CollectionPath: 'Customers',
      Parameters    : [{
        $Type            : 'Common.ValueListParameterInOut',
        LocalDataProperty: customer_ID,
        ValueListProperty: 'ID',
      }, ],
    },
    Common.ValueListWithFixedValues: false
  )
};

annotate service.Customers with {
  ID @Common.Text: {
    $value                : name,
    ![@UI.TextArrangement]: #TextOnly,
  }
};

annotate service.Incidents with {
  urgency @Common.Text: urgency.descr
};

annotate service.Incidents with {
  status @Common.Text: status.descr
};

annotate service.Incidents with {
  status @Common.ValueListWithFixedValues: true
};

annotate service.Status with {
  code @Common.Text: descr
};

annotate service.Incidents.conversation with @(
  title                     : '{i18n>Conversation}',
  UI.LineItem #Conversations: [
    {
      $Type: 'UI.DataField',
      Value: author,
      Label: '{i18n>Author}',
    },
    {
      $Type: 'UI.DataField',
      Value: message,
      Label: '{i18n>Message}',
    },
    {
      $Type: 'UI.DataField',
      Value: timestamp,
      Label: '{i18n>ConversationDate}',
    },
  ]
);
