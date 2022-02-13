## Book-trading Multi-Agent System using TILAB's JADE

JADE is a framework that allows the development of `Multi-Agent Systems`

DF `Directory Facilitator` provides a `yellow pages` service to the platform.

ACC `Agent Communication Channel` manages communication between agents.

AMS `Agent Management System` supervises the registration of agents, their authentication, access and use of the system.

*These three modules are activated each time the platform is started.

## Description


This is a representation of how Agents communicate and behave on a given plateform.<br/>
For this application, the main actors are:<br/>

- Consumer agent
- Buyer agent
- Seller agent

In addition to the above mentioned agents, there are other core agents `ACC, DF, AMS`, they provide and manage communication messages between agents. They are integrated with the JADE plateform.

## Installation


## Getting Started

### Dependencies

### Usage

### Executing program

```
Runtime runtime = Runtime.instance();
Properties properties = new ExtendedProperties();
properties.setProperty(Profile.GUI, "true");
Profile profile = new ProfileImpl(properties);
            
AgentContainer mainContainer = runtime.createMainContainer(profile);
mainContainer.start();
```

## Help

## Authors
Tarik Haroun<br/>
Gmail: tarik.haroun@univ-constantine2.dz 

## Version History

* 0.1
    * Various bug fixes and optimizations
    * See [commit change]() or See [release history]()
    * Initial Release

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
