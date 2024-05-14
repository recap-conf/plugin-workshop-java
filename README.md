# Incidents-app-java

CAP Java based Incident Management Application.

## Prerquisites:
 
 Make sure you have Java 17+ version installed in your machine.
 Copy included [`settings.xml`](config/maven/settings.xml) in your `~/.m2` directory.

## Run and Test the Incidents Management Application Locally

1. Clone the Incident Management application repository:

    ```sh
    git clone https://github.tools.sap/refapps/incidents-app-java.git
    ```
2. Build the application:
    ```sh
    mvn clean install
    ```
3. Run the application locally:
    ```sh
    cd srv && mvn cds:watch
    ```
4. Go to `localhost:8080` and you will get the list of exposed endpoints.
5. Test the following endpoint: `http://localhost:8080/odata/v4/ProcessorService/Incidents`
6. When you are prompted to authenticate, use the following credentials:</br>
       - Username: alice</br>
       - Password:
7. To access the Incident Management Application WebApp, use the following url : `http://localhost:8080/incidents/webapp/index.html`
7. When you are testing `AdminService` endpoints, use the following credentials:</br>
       - Username: admin</br>
       - Password: admin
## Deploy and Test Incidents Management Application on BTP Cloud Foundry Runtime

1. In the root folder do:
   ```sh
   mbt build
   ```
2. Login to your BTP Subaccount space where you want to deploy your application, and run the following command:
   ```sh
   cf deploy mta_archives/incident-management_1.0.0-SNAPSHOT.mtar   
   ```
This will deploy your application, create and bind the required services to your application. Now you can test your application from and http client tool e.g Insomnia.

## Deploy and Test Incidents Management Application on BTP Kyma Runtime

Follow the following [tutorial](https://developers.sap.com/tutorials/deploy-to-kyma.html) till `step 8` to prepare your application for Kyma Runtime.

### Build images

Build the CAP Java Service and the database image
1. Create the productive CAP build for your application:
    ```sh
    cds build --production
    ```
2. Build the CAP Java Service image:
    ```sh
    pack build <your-container-registry>/incident-management-srv:<image-version>\                 
    --path srv/target/*-exec.jar \
    --builder paketobuildpacks/builder-jammy-base \
    --publish
    ```
3. Build the database image:
   ```sh
   pack build <your-container-registry>/incident-management-hana-deployer:<image-version> \
    --path db \
    --builder paketobuildpacks/builder-jammy-base \
    --publish
   ```
### Add Helm Charts
CAP provides a configurable Helm chart for Java applications.
1. Run the following command in your project root folder:
   ```sh
   cds add helm
   ```
2. Add your container image settings to your **chart/values.yaml** file:
   ```yaml
    ...
    srv:
    image:
        repository: <your-container-registry>/incident-management-srv
        tag: <incident-management-srv-image-version>
    ...
    hana-deployer:
    image:
        repository: <your-container-registry>/incident-management-hana-deployer
        tag: <incident-management-hana-deployer-image-version>
    ...

   ```
3. Run the following command to get the domain name of your Kyma cluster:
   ```sh
   kubectl get gateway -n kyma-system kyma-gateway \
        -o jsonpath='{.spec.servers[0].hosts[0]}'

   ```
   Result should look like this:
   ```sh
   *.<xyz123>.kyma.ondemand.com

   ```
4. Add the result without the leading *. in the domain property to the chart/values.yaml file so that the URL of your CAP service can be generated:
   ```yaml
    global:
        domain: <cluster domain>
    ...

   ```
5. Replace `<your-cluster-domain>` with your cluster domain in the xsuaa section of the **chart/values.yaml** file. In case the `oauth2-configuration:` section is missing from your **values.yaml** file, make sure to add it altogether like that::
   ```yaml
    xsuaa:
      serviceOfferingName: xsuaa
      servicePlanName: application
      parameters:
        xsappname: incidents
        tenant-mode: dedicated
        oauth2-configuration:
        redirect-uris:
            - https://*.<your-cluster-domain>/**


   ```
### Deploy CAP Helm chart
Follow `step 11` i.e `Deploy CAP Helm chart` of following [tutorial](https://developers.sap.com/tutorials/deploy-to-kyma.html)

### Assign the User Roles
Follow [user-role-assignment](https://developers.sap.com/tutorials/user-role-assignment.html) to assign roles to users.
- Assign `support` and `admin` role to access the application services.

### Access your service
1. Login to your kyma cluster and namespace where you deployed your application
2. Go to **Configurations** -> **Secrets** -> **incident-management-srv-auth** and under `data` section, click on decode icon.
3. Use the following credentials :</br>
    clientid</br>
    clientsecret</br>
    url</br>
Append `/oauth/token` to `url` and use these credentials to generate xsuaa token to access your service.

For details, refere `Test Your Application` section of following [documentation](https://github.com/SAP-samples/btp-developer-guide-cap/blob/main/documentation/auditlog/5-deploy-to-kyma.md).


