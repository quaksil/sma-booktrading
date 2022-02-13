## Book-trading Multi-Agent System using TILAB's JADE

JADE is a framework that allows the development of `Multi-Agent Systems`.

It has three (03) main modules (necessary for FIPA standards):

<b>DF</b>: `Directory Facilitator` provides a yellow pages service to the platform.<br/>
<b>ACC</b>: `Agent Communication Channel` manages communication between agents.<br/>
<b>AMS</b>: `Agent Management System` supervises the registration of agents, their authentication, access and use of the system.<br/>

*These three modules are activated each time the platform is started.**

## Description

In this project, we will see the representation of how `agents` communicate and behave on a given platform.<br/>

The main actors for this application are:<br/>

- Consumer agent
- Buyer agent
- Seller agent

<br/>
<p align="center">
<kbd>
  <img src="https://user-images.githubusercontent.com/33737302/153768003-a0f8ea09-0c57-44d9-819e-3de26b489323.png">
</kbd>
</p>


In addition to the above mentioned agents, `ACC, DF and AMS` provide support for these agents to `behave (Behaviours)` and `communicate (ACLMessages)` following a `given logic`.

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
