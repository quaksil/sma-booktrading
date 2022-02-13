## Booktrading multi-agent system with tilab's JADE

This is a representation of how Agents communicate and behave on a given plateform.<br/>
For this application, the main actors are:<br/>

- Consumer Agent
- Buyer Agent
- Seller Agent

In addition the above mentioned Agents, there are other core Agents 'ACC, DF and AMS' that provide and manage communication messages integrated within JADE.

## Description

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
