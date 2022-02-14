package com.sma.booktrading.agents.buyer;

import com.sma.booktrading.agents.seller.Book;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

class BuyerBehaviour extends CyclicBehaviour {

    private String conversationId;
    private AID requester;
    private String bookName;
    private Double price;

    private List<AID> sellersList = new ArrayList<>();

    private HashMap<AID, Double> offers = new HashMap<AID, Double>();
    private HashMap<AID, Double> bestOffers = new HashMap<AID, Double>();

    private BuyerPortal gui;

    HashMap<AID, Double> contentObject2 = new HashMap<>();
    private int sellerCount = 0;
    private int refusedCount = 0;

    ACLMessage aclMessageRefused;

    public int strategy;

    // private Double desiredPrice;
    public BuyerBehaviour(Agent agent, String bookName, AID requester, String conversationId, BuyerPortal gui, int strategy) throws InterruptedException {

        super(agent);

        this.bookName = bookName;
        this.gui = gui;
        this.requester = requester;
        this.conversationId = conversationId;
        this.strategy = strategy;

        gui.showMessage("[#] DF services lookup..\n");

        sellersList = lookupServices(myAgent, "book-selling");

        gui.showMessage("[#] Service providers: \n");

        for (AID aid : sellersList) {

            sellerCount = sellerCount + 1;
            gui.showMessage("[#] Seller " + sellerCount + ": " + aid.getName() + "\n");
        }

        // System.out.println("seller: " + sellerCount);

        gui.showMessage("[#] Processing book purchase request for: " + bookName);
        gui.showMessage("[#] From: " + requester.getName() + "\n");

        ACLMessage aclMessage = new ACLMessage(ACLMessage.CFP);

        aclMessage.setContent(bookName);
        aclMessage.setConversationId(conversationId);

        // aclMessage.addUserDefinedParameter("counter", String.valueOf(counter));
        
        for (AID aid : sellersList) {

            gui.showMessage("[#] Establishing contact with: " + aid.getName());
            gui.showMessage("[#] Sending request..\n");

            aclMessage.addReceiver(aid);

        }

        myAgent.send(aclMessage);
        Thread.sleep(3000);


    }

    public HashMap<AID, Double> findMinPrice(HashMap<AID, Double> contentObject) {

        HashMap<AID, Double> bestMinPrice = new HashMap<>();
        Double min = Collections.min(contentObject.values());

        // Set<AID> result = new HashSet<>();
        for (Entry<AID, Double> entry : contentObject.entrySet()) {
            if (entry.getValue().equals(min)) {

                bestMinPrice.put(entry.getKey(), min);

            }
        }
        

    return bestMinPrice ;
}
    
     public void notifyConsumer() {

        if (sellerCount > 0) {

            ACLMessage aclMessageInf = new ACLMessage(ACLMessage.INFORM);
            aclMessageInf.addReceiver(requester);
            aclMessageInf.setConversationId(conversationId);
            aclMessageInf.setContent("[!] We're hooking you up, please wait..");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            }
            myAgent.send(aclMessageInf);
        }
    }

@Override
public void action() {

        MessageTemplate messageTemplate = MessageTemplate.MatchConversationId(conversationId);
        
        ACLMessage aclMessage = myAgent.receive(messageTemplate);

        if (aclMessage != null) {

            switch (aclMessage.getPerformative()) {

                case ACLMessage.PROPOSE:

                    price = Double.parseDouble(aclMessage.getContent());

                    gui.showMessage("[#] Receiving proposal from: " + aclMessage.getSender().getName());
                    gui.showMessage("[#] Price: " + price);
                    gui.showMessage("[#] Negociating for: " + strategy + "%");
                   
                    // offers.put(aclMessage.getSender(), price);

                    // for (AID aid : offers.keySet()) {

                        ACLMessage aclMessagePropose = new ACLMessage(ACLMessage.PROPOSE);
                        
                        // gui.showMessage("[#] Sending proposal to: " + aclMessage.getSender().getName());

                        aclMessagePropose.addReceiver(aclMessage.getSender());
                        aclMessagePropose.setConversationId(conversationId);

                        HashMap<String, Integer> contentObject = new HashMap<>();
                        contentObject.put("FirstProposal", strategy);
                        
                           
                    
                {
                    try {
                        aclMessagePropose.setContentObject(contentObject);
                    } catch (IOException ex) {
                        Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                        myAgent.send(aclMessagePropose);
                {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                        gui.showMessage("[#] Processing..\n");

                    break;



                case ACLMessage.REFUSE:

                    HashMap<String, AID> contentObject1 = new HashMap<>();

                {
                    try {
                        contentObject1 = (HashMap<String, AID>) aclMessage.getContentObject();
                    } catch (UnreadableException ex) {
                        Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }


                     sellerCount = sellerCount-1;
                     refusedCount++;
                     
                       // System.out.println("seller:"+ sellerCount);
                    
                     if (contentObject1.containsKey("OrderRefused")) {

                        offers.remove(contentObject1.get("OrderRefused"));

                        gui.showMessage("[!] Offer refused by: " + aclMessage.getSender().getName());
                        gui.showMessage("[!] For object: " + aclMessage.getConversationId());
                        gui.showMessage("[!] Forwarding response to consumer.."+"\n");

                    aclMessageRefused = new ACLMessage(ACLMessage.INFORM);
                    aclMessageRefused.addReceiver(requester);
                    aclMessageRefused.setConversationId(conversationId);
                    
                       notifyConsumer();
                       
                  if (sellerCount == 0 && refusedCount < sellersList.size()) {

                            try {
                                AID winnerAID = null;
                                
                                HashMap<AID, Double> winner = findMinPrice(bestOffers);
                                for (AID aid : winner.keySet()) {
                                    
                                    winnerAID = aid;
                                }
                                
                                winner.get(winnerAID);
                                
                                gui.showMessage("[!] Offer approved, handshake with: " + winnerAID.getName());
                                gui.showMessage("[!] For object: " + aclMessage.getConversationId() );
                                
                                
                                ACLMessage aclMessageAP = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                aclMessageAP.addReceiver(winnerAID);
                                aclMessageAP.setConversationId(conversationId);
                                
                                gui.showMessage("[#] Processing.." + "\n");
                                aclMessageAP.setContentObject(contentObject2);
                                
                                
                                
                                myAgent.send(aclMessageAP);
                                Thread.sleep(2000);
                            } catch (IOException | InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            } 

                    } else if(refusedCount == sellersList.size()){
                     aclMessageRefused.setContent("[!] All sellers refused your order.\n");
                     
                                myAgent.send(aclMessageRefused);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }


                    
                    }
                    
                    }
                    
                    break;


                case ACLMessage.ACCEPT_PROPOSAL:

                    sellerCount = sellerCount-1;
                    
                {
                    try {
                        // System.out.println("seller: " + sellerCount);
                        
                        contentObject2 = (HashMap<AID, Double>) aclMessage.getContentObject();
                    } catch (UnreadableException ex) {
                        Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                     gui.showMessage("[#] One seller approved, waiting for other offers..");
                     gui.showMessage("[#] Seller: " + aclMessage.getSender().getName() + "\n");
                    
                     
                     notifyConsumer();
                     
                    

                    for (AID aid : contentObject2.keySet()) {

                        bestOffers.put(aid, contentObject2.get(aid));

                    }

                    if (sellerCount == 0 && refusedCount < sellersList.size()) {

                try {
                    AID winnerAID = null;
                    
                    HashMap<AID, Double> winner = findMinPrice(bestOffers);
                    for (AID aid : winner.keySet()) {
                        
                        winnerAID = aid;
                    }
                    
                    winner.get(winnerAID);
                    
                    gui.showMessage("[!] Offer approved, handshake with: " + winnerAID.getName());
                    gui.showMessage("[!] For object: " + aclMessage.getConversationId() );
                    
                    ACLMessage aclMessageAP = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    aclMessageAP.addReceiver(winnerAID);
                    aclMessageAP.setConversationId(conversationId);
                    
                    aclMessageAP.setContentObject(aclMessage.getContentObject());
                    
                    
                    gui.showMessage("[#] Processing..\n");
                            myAgent.send(aclMessageAP);
                            Thread.sleep(3000);
                } catch (UnreadableException | IOException | InterruptedException ex) {
                    Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                }


                       

                    } else if(refusedCount == sellersList.size()){
                try {
                    aclMessageRefused.setContent("[#] All sellers refused your order.\n");
                    
                    myAgent.send(aclMessageRefused);
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                }


                    }

                    break;

                case ACLMessage.CONFIRM:

                    gui.showMessage("[#] Receiving confirmation from: " + aclMessage.getSender().getName());
                    gui.showMessage("[#] Forwarding confirmation to consumer.\n");
                    ACLMessage aclMessageInf = new ACLMessage(ACLMessage.INFORM);

                    
                    aclMessageInf.addReceiver(requester);
                    aclMessageInf.setConversationId(conversationId);
                     {
                        try {
                            aclMessageInf.setContent("[!] Yay! we got you the book for: " + aclMessage.getContentObject());

} catch (UnreadableException ex) {
                            Logger.getLogger(BuyerBehaviour.class  

.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                     try {
                                Thread.sleep(3000);

} catch (InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class  

.getName()).log(Level.SEVERE, null, ex);
                            }
                    myAgent.send(aclMessageInf);

                    break;

                case ACLMessage.INFORM:

                    
                    String outOfStock = aclMessage.getContent();
                    
                    if(outOfStock.equals("OutOfStock")){
                        
                        ACLMessage aclMessageOutOfStock = new ACLMessage(ACLMessage.INFORM);
                        aclMessageOutOfStock.addReceiver(requester);
                        aclMessageOutOfStock.setConversationId(conversationId);
                        aclMessageOutOfStock.setContent("[#] No books for you, the last book was just sold.\n");
                        
                        myAgent.send(aclMessageOutOfStock);
                    
                    break;
                    }else{
                    
                    
                    HashMap<String, Integer> contentObject3 = new HashMap<>();

                     {
                        try {
                            contentObject3 = (HashMap<String, Integer>) aclMessage.getContentObject();

} catch (UnreadableException ex) {
                            Logger.getLogger(BuyerBehaviour.class  

.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    if ("Book unavailable or Out of Stock!".equals(aclMessage.getContent())) {

                        gui.showMessage("[!] Book unavailable, or out of stock!\n");
                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                        reply.setConversationId(conversationId);
                        reply.addReceiver(requester);
                        reply.setContent("[!] Book unavailable, or out of stock!");
                        try {
                                Thread.sleep(3000);

} catch (InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class  

.getName()).log(Level.SEVERE, null, ex);
                            }
                        myAgent.send(reply);
                        
                        sellerCount = sellerCount-1;
                        
                        System.out.println("book univ" +sellerCount);

                    }
                    if (contentObject3.containsKey("NewOffer")) {

                        gui.showMessage("[#] Received a reply from seller:" + aclMessage.getSender().getName());
                        
                        int sellerOffer = contentObject3.get("NewOffer");
                        
                        gui.showMessage("[#] They offer: " + sellerOffer + "%");

                        if (sellerOffer <= strategy) {

                            

                            try {
                                gui.showMessage("[#] Low discount, negotiating again..\n");
                                
                                ACLMessage aclMessageP = new ACLMessage(ACLMessage.PROPOSE);
                                aclMessageP.addReceiver(aclMessage.getSender());
                                aclMessageP.setConversationId(conversationId);
                                
                                HashMap<String, Integer> contentObjectR = new HashMap<>();
                                contentObjectR.put("No", sellerOffer);
                                
                                
                                aclMessageP.setContentObject(contentObjectR);
                                myAgent.send(aclMessageP);
                                Thread.sleep(3000);
                                
                                
                            } catch (IOException | InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                         
                        } else {

                        }
                    }

                    if (contentObject3.containsKey("LastOffer")) {

                        gui.showMessage("[#] New offer from: " + aclMessage.getSender().getName());
                        int sellerOffer = contentObject3.get("LastOffer");
                        gui.showMessage("[#] They offer: " + sellerOffer + "%");

                        if (sellerOffer <= strategy) {

                            try {
                                ACLMessage aclMessageP = new ACLMessage(ACLMessage.PROPOSE);
                                aclMessageP.addReceiver(aclMessage.getSender());
                                aclMessageP.setConversationId(conversationId);
                                
                                HashMap<String, Integer> contentObjectR = new HashMap<>();
                                
                                int finalOffer = 0;
                                
                                if (sellerOffer == strategy){
                                    finalOffer = ThreadLocalRandom.current().nextInt(sellerOffer + 1, strategy);}
                                else {finalOffer = ThreadLocalRandom.current().nextInt(sellerOffer, strategy);}
                                
                                gui.showMessage("[#] Better discount, but not enough, negociating again..");
                                gui.showMessage("[#] Making final offer of: " + finalOffer + "%\n");
                                
                                contentObjectR.put("FinalProposal", finalOffer);
                                
                                aclMessageP.setContentObject(contentObjectR);
                                
                                
                                myAgent.send(aclMessageP);
                                Thread.sleep(3000);
                            } catch (IOException | InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            
                        }

                    }
                    notifyConsumer();
                    break;
                    }
                default: break;

            }
        } else {
            block();
        }
    }

    public final List<AID> lookupServices(Agent agent, String type) {

        List<AID> sellers = new ArrayList<>();

        DFAgentDescription agentDescription = new DFAgentDescription();

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(type);

        agentDescription.addServices(serviceDescription);

        DFAgentDescription[] descriptions;
        try {
            descriptions = DFService.search(agent, agentDescription);

            for (DFAgentDescription dfd : descriptions) {
                sellers.add(dfd.getName());

}
        } catch (FIPAException ex) {
            Logger.getLogger(BuyerBehaviour.class  

.getName()).log(Level.SEVERE, null, ex);
        }
        return sellers;
    }

}
