# Game

This is a multi-player Rock Paper Scissors game developed using [Bot Framework](https://dev.botframework.com).

This game is a Spring Boot app and uses the Azure CLI and azure-webapp Maven plugin to deploy to Azure.

## Prerequisites

- Java 1.8+
- Install [Maven](https://maven.apache.org/)
- An account on [Azure](https://azure.microsoft.com) if you want to deploy to Azure.

## To try this sample locally
- From the root of this project folder:
  - Build the sample using `mvn package`
  - Run it by using `java -jar .\target\Game-0.0.1-SNAPSHOT.jar`
  
  Eclpise IDE with M2Eclipse plugin and Apache Tomcat server can also be used to run this application locally.

- Test the bot using Bot Framework Emulator

  [Bot Framework Emulator](https://github.com/microsoft/botframework-emulator) is a desktop application that allows bot developers to test and debug their bots on localhost or running remotely through a tunnel.

  - Install the Bot Framework Emulator version 4.3.0 or greater from [here](https://github.com/Microsoft/BotFramework-Emulator/releases)

  - Connect to the bot using Bot Framework Emulator

    - Launch Bot Framework Emulator
    - File -> Open Bot
    - Enter a Bot URL of `http://localhost:8080/api/messages`

## Deploy the bot to Azure

As described on [Deploy your bot](https://docs.microsoft.com/en-us/azure/bot-service/bot-builder-deploy-az-cli), you will perform the first 4 steps to setup the Azure app, then deploy the code using the azure-webapp Maven plugin.

### 1. Login to Azure
From a command (or PowerShell) prompt in the root of the bot folder, execute:  
`az login`
  
### 2. Set the subscription
`az account set --subscription "<azure-subscription>"`

If you aren't sure which subscription to use for deploying the bot, you can view the list of subscriptions for your account by using `az account list` command. 

### 3. Create an App registration
`az ad app create --display-name "<botname>" --password "<appsecret>" --available-to-other-tenants`

Replace `<botname>` and `<appsecret>` with your own values.

`<botname>` is the unique name of your bot.  
`<appsecret>` is a minimum 16 character password for your bot. 

Record the `appid` from the returned JSON

### 4. Create the Azure resources
Replace the values for `<appid>`, `<appsecret>`, `<botname>`, and `<groupname>` in the following commands:

#### To a new Resource Group
`az deployment create --name "echoBotDeploy" --location "westus" --template-file ".\deploymentTemplates\template-with-new-rg.json" --parameters groupName="<groupname>" botId="<botname>" appId="<appid>" appSecret="<appsecret>"`

#### To an existing Resource Group
`az group deployment create --name "echoBotDeploy" --resource-group "<groupname>" --template-file ".\deploymentTemplates\template-with-preexisting-rg.json" --parameters botId="<botname>" appId="<appid>" appSecret="<appsecret>"`

### 5. Update the pom.xml
In pom.xml update the following nodes under azure-webapp-maven-plugin
- `resourceGroup` using the `<groupname>` used above
- `appName` using the `<botname>` used above

### 6. Update app id and password
In src/main/resources/application.properties update 
  - `MicrosoftAppPassword` with the botsecret value
  - `MicrosoftAppId` with the appid from the first step

### 7. Deploy the code
- Execute `mvn clean package` 
- Execute `mvn azure-webapp:deploy`

If the deployment is successful, you will be able to test it via "Test in Web Chat" from the Azure Portal using the "Bot Channel Registration" for the bot.

After the bot is deployed, you only need to execute #7 if you make changes to the bot.

## Steps to start the game:
Once the app is up and running, add the game to a team/channel and follow these steps -
- In the channel, enter command - @`<botname>` StartGame
- Everyone in the channel receives a card with three options - Rock, Paper, Scissors. Choose one.
- Once each of the team members reply with their choices, a score card is sent to each player.

Total score is calculated based on the following rule:

For each person who beat you, you score -1. For each person you beat, you score 1.

The GameBot reports the total score, and no one is eliminated.

The score dashboard displays cumulative score for each player of the team in a tab.
The app also has a messaging extension.
