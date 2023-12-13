# Incidents-app-java

CAP Java based Incident Management Application.

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
5. Test the following endpoint: `http://localhost:8080/odata/v4/AdminService/Customers`
6. When you are prompted to authenticate, use the following credentials:
       - Username: admin
       - Password: admin
7. When you are testing `ProcessorService` endpoints, use the following credentials:
       - Username: alice
       - password:
## Deploy and Test Incidents Management Application on BTP CF

1. In the root folder do:
   ```sh
   mbt build
   ```
2. Login to your BTP Subaccount space where you want to deploy your application, and run the following command:
   ```sh
   cf deploy mta_archives/incident-management_1.0.0-SNAPSHOT.mtar   
   ```
This will deploy your application, create and bind the required services to your application. Now you can test your application from and http client tool e.g Insomnia.