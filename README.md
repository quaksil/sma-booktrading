## Multi-Agent System for a Book-Trading Application

## Description

## Installation


## Getting Started

### Dependencies

### Usage

### Executing program

```java
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