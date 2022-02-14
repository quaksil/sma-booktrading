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

## Scenario

The main scenario for the application is for a consumer to initiate an instance and login, input a desired book for purchase and a buyer (already present in the platform) will try to get that book for the lowest price possible among a single seller or multiple sellers for the matter.

Based on this simple scenario, the communications between these agents will be showcased on a textarea in each UI specific to the deployed agent.

### Consumer Agent

As mentioned above the consumer agent will input a book title and a buyer name (that is present in the platform), the specified buyer uses a certain strategy to negotiate with remote sellers (a discount percentage %, as you will see below), once the request has been issued, the buyer that was specified will recieve the consumer's order, inform them and then discover the available sellers that propose their services on the yellow-pages (Directory Facilitator).

Once the negotiation is complete, these scenarios can be expected:

- All sellers refused the order (all negotiations have failed).
- The book is not available for sale from any of the sellers or is out of stock.
- One of the sellers has been selected and the order is approved.

An extra scenario would be when there are multiple consumers (two for example) and  buyers (two as well), each buyer processes a consumer's order with a single seller but there is only one book left, if one of the buyers manages to get the book, the other consumer will get a message that there are no books left and the first consumer will get the book (if the negotiation is successful).

Handled ACLMessages: `conversationId`, `INFORM`.

Here's an example of a successful transaction:

![image](https://user-images.githubusercontent.com/33737302/153946384-b7a3e9af-d009-40a4-b81b-4fb6c9068995.png)


### Buyer Agent

The buyer agents (that were implemented in this application) can have two strategies, 10% discount strategy or a 20% strategy (for the wanted book).

Firstly, a buyer agent will: 

- Receive the order from the consumer and inform the consumer of the receival.
- Once the transaction has been set (with a conversation id), they will request the DF for services that have been published for a `book-selling` type service by sellers.
- After that, they will receive the book's price and send a proposal (10% or 20% discount from the original book price) to all the sellers.
- Sellers will receive the offer and process it.
- If the offer is denied, they will propose a lower discount.
- A buyer can accept or refuse.
- The seller will make a higher offer.
- When the buyer refuses the new offer (lower than 20% for example), the buyer will make a final proposal between the last offer and the 20%.
- The seller processes the offer and can refuse or approve.
- Once a seller approves, the buyer receives the approval and adds he seller to the waiting list until negotiation is done with other sellers.
- The buyer selects the lowest price possible, sends an approval to the best seller and receives a confirm then forwards it to the consumer.

Handled ACLMessages: `conversationId`, `INFORM`, `PROPOSE`, `ACCEPT_PROPOSAL`, `REFUSE`, `CONFIRM`.

Here's an example of a successful transaction:

![image](https://user-images.githubusercontent.com/33737302/153947230-e889e6cd-795e-45e7-9cf5-82c12bac8091.png)

### Seller Agent

Each seller agent has a bookstore that they can manage, add a book (book title, price, quantity, minimum price and a maximum discount). The seller agents publish their bookstores on the DF.

Once a seller agent is contacted, this scenario can be expected:

- A seller receives a CFP (Call for Proposal) and proposes their book for a certain price (if available quantity wise and availability wise).
- The seller receives an offer from a buyer (20% strategy for example), and processes the offer, if it is higer than their original maximum discount, they will ofer a percentage between 1% for example and their maximum discount 10% for example.
- The buyer will deny that offer and so, the seller offers another discount that can be between half of the maximum discount and the original maximum discount (if last offer was less than half of the original maximum discount) OR offer the maximum discount immediately if the last offer was more than half the maximum.
- A buyer will deny that offer again (10%<20%) for example so the buyer will send a final offer that is between the maximum discount (10%) and the strategy (20%).
- A seller can accept or refuse based on the new percentage (if 18% for example for a 1000 priced book, the offer is denied because the minimum price for that book was 850 and 18% discount from 1000 is less than 850).
- If a seller accepts (a 15% offer for example), they will send an approval and await an approval from the buyer, if not received that means a buyer found a better offer.

Other use cases:
- A seller can inform the buyer that the book is unavailabe, the last book was just sold or out of stock.

Handled ACLMessages: 
- Any kind of `conversationId` nature.

Example of a successful transaction: 

![image](https://user-images.githubusercontent.com/33737302/153947321-e527f308-e4e0-418a-b831-f657170e73ed.png)

### Final example of a communication between a consumer, a buyer and two sellers.

In this example we can see the communication, negotiation, different ACLMessages.
One seller denies the buyer's offer but another one accepts it, the final answer is forwarded to the consumer.

![image](https://user-images.githubusercontent.com/33737302/153949910-76f11428-083b-49f9-81a7-ea046ea833a1.png)

![image](https://user-images.githubusercontent.com/33737302/153949965-bd949b9f-dda7-4954-8d80-81e785721ac7.png)



## Client-Server architecture

We can split this application to a client-server application by simply running the buyer and the seller agents on a host main container and the client (consumer) on a another machine as a client, once logged in, the consumer agent will be deployed on the remote host main container.

### Multiple JADE platforms and containers

A client (consumer) can be deployed on a different main container than the server's, all we have to do is to link the two platforms' AMS agent (putting the client's main container AMS agent on a container in the host's main contaier for example).

### Usage

### Executing program

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
