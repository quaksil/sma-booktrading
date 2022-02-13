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

    private int sellerCount = 0;
    private int refusedCount = 0;

    ACLMessage aclMessageRefused;
            
    public int strategy;

    // private Double desiredPrice;
    public BuyerBehaviour(Agent agent, String bookName, AID requester, String conversationId, BuyerPortal gui, int strategy) {

        super(agent);

        this.bookName = bookName;
        this.gui = gui;
        this.requester = requester;
        this.conversationId = conversationId;
        this.strategy = strategy;

        gui.showMessage("[#] DF services lookup..\n");

        sellersList = lookupServices(myAgent, "book-selling");

        gui.showMessage("[!] Service providers: \n");

        for (AID aid : sellersList) {

            sellerCount = sellerCount + 1;
            gui.showMessage("[!] Seller " + sellerCount + ": " + aid.getName() + "\n");
        }

        System.out.println("seller: " + sellerCount);

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
        
        try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
        

    }

    public HashMap<AID, Double> findMinPrice(HashMap<AID, Double> contentObject) {

        HashMap<AID, Double> bestMinPrice = new HashMap<>();
        Double min = Collections.min(contentObject.values());

        // Set<AID> result = new HashSet<>();
        
        for (Entry<AID, Double> entry : contentObject.entrySet()) {
        if (entry.getValue().equals(min)) {
            
            bestMinPrice.put(entry.getKey(),min);
            
        }
    }
 
    return bestMinPrice;

    /*bestOffer.put (
    (AID) result, min);

    return bestOffer ;

    /* HashMap<AID, Double> bookMinPrices = new HashMap<>();
        AID[] aidS = new AID[contentObject.size()];
        int pos = 0;

        for (AID aid : contentObject.keySet()) {

            aidS[pos] = aid;
            pos++;

        }

        pos = 0;
        for (Book book : contentObject.values()) {

            bookMinPrices.put(aidS[pos], book.getPrice());
            pos++;

        }
     */
 /*
     */
}

@Override
public void action() {

        MessageTemplate messageTemplate = MessageTemplate.MatchConversationId(conversationId);
        
        ACLMessage aclMessage = myAgent.receive(messageTemplate);

        if (aclMessage != null) {

            switch (aclMessage.getPerformative()) {

                case ACLMessage.PROPOSE:

                    price = Double.parseDouble(aclMessage.getContent());

                    gui.showMessage("[!] Receiving proposal: ");
                    gui.showMessage("-- " + aclMessage.getConversationId());
                    gui.showMessage("From: " + aclMessage.getSender().getName());
                    gui.showMessage("Price: " + price + "\n");

                    gui.showMessage("Starting Negociation for best [price]..");
                    gui.showMessage("-- " + aclMessage.getConversationId());
                    gui.showMessage("Discount Strategy: " + strategy + "% \n");

                    // offers.put(aclMessage.getSender(), price);

                    // for (AID aid : offers.keySet()) {

                        ACLMessage aclMessagePropose = new ACLMessage(ACLMessage.PROPOSE);
                        gui.showMessage("[#] Sending proposal to: " + aclMessage.getSender().getName());

                        aclMessagePropose.addReceiver(aclMessage.getSender());
                        aclMessagePropose.setConversationId(conversationId);

                        HashMap<String, Integer> contentObject = new HashMap<>();
                        contentObject.put("FirstProposal", strategy);
                        try {
                            aclMessagePropose.setContentObject(contentObject);
                        } catch (IOException ex) {
                        }

                        //aclMessageP.setContent("FirstProposal");
                        
                        gui.showMessage("Processing..\n");
                        try {
                            Thread.sleep(2000);

} catch (InterruptedException ex) {
                            Logger.getLogger(BuyerBehaviour.class  

.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        myAgent.send(aclMessagePropose);
                    //}

                    break;

                case ACLMessage.REFUSE:

                    HashMap<String, AID> contentObject1 = new HashMap<>();

                     {
                        try {
                            contentObject1 = (HashMap<String, AID>) aclMessage.getContentObject();

} catch (UnreadableException ex) {
                            Logger.getLogger(BuyerBehaviour.class  

.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                     sellerCount = sellerCount-1;
                     refusedCount++;
                     
                        System.out.println("seller:"+ sellerCount);
                    if (contentObject1.containsKey("OrderRefused")) {

                        offers.remove(contentObject1.get("OrderRefused"));

                        gui.showMessage("[!] Offer refused.");
                        gui.showMessage("[!] Object: " + aclMessage.getConversationId());
                        gui.showMessage("[!] From: " + aclMessage.getSender().getName());

                    aclMessageRefused = new ACLMessage(ACLMessage.INFORM);
                    aclMessageRefused.addReceiver(requester);
                    aclMessageRefused.setConversationId(conversationId);
                    
                                
                 if(refusedCount == 3){
                     aclMessageRefused.setContent("All sellers refused your order.");
                         try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    myAgent.send(aclMessageRefused);}
                    
                    }
                    break;

                case ACLMessage.ACCEPT_PROPOSAL:

                    sellerCount = sellerCount-1;
                    
                    System.out.println("seller: " + sellerCount);
                    
                    HashMap<AID, Double> contentObject2 = new HashMap<>();
                     {
                        try {
                            contentObject2 = (HashMap<AID, Double>) aclMessage.getContentObject();

} catch (UnreadableException ex) {
                            Logger.getLogger(BuyerBehaviour.class  

.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    for (AID aid : contentObject2.keySet()) {

                        bestOffers.put(aid, contentObject2.get(aid));

                    }

                    if (sellerCount == 0 && refusedCount < sellersList.size()) {

                        AID winnerAID = null;

                        HashMap<AID, Double> winner = findMinPrice(bestOffers);
                        for (AID aid : winner.keySet()) {

                            winnerAID = aid;
                        }

                        winner.get(winnerAID);

                        gui.showMessage("[!] Proposal approved");
                        gui.showMessage("[!] Handshake with: " + winnerAID.getName());
                        gui.showMessage("[!]" + aclMessage.getConversationId());

                        ACLMessage aclMessageAP = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        aclMessageAP.addReceiver(winnerAID);
                        aclMessageAP.setConversationId(conversationId);
                        {
                            try {

                                aclMessageAP.setContentObject(aclMessage.getContentObject());

} catch (UnreadableException | IOException ex) {
                                Logger.getLogger(BuyerBehaviour.class  

.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        gui.showMessage("Processing..");
                        {
                            try {
                                Thread.sleep(2000);

} catch (InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class  

.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        myAgent.send(aclMessageAP);

                    } else if(refusedCount == sellersList.size()){
                     aclMessageRefused.setContent("All sellers refused your order.");
                         try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    myAgent.send(aclMessageRefused);}
                    
                    else if(sellerCount > 0){

                        ACLMessage aclMessageInf = new ACLMessage(ACLMessage.INFORM);
                        aclMessageInf.addReceiver(requester);
                        aclMessageInf.setConversationId(conversationId);
                        aclMessageInf.setContent("Please wait while we find the best offer for you..");
                        try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        myAgent.send(aclMessageInf);
                    }

                    break;

                case ACLMessage.CONFIRM:

                    gui.showMessage("[#] Receiving confirmation.. ");
                    gui.showMessage("-- " + aclMessage.getConversationId());
                    gui.showMessage("From: " + aclMessage.getSender().getName());

                    ACLMessage aclMessageInf = new ACLMessage(ACLMessage.INFORM);
                    aclMessageInf.addReceiver(requester);
                    aclMessageInf.setConversationId(conversationId);
                     {
                        try {
                            aclMessageInf.setContent("[!] Transaction receipt:\n" + "Book : " + bookName + "\n Best discount: " + aclMessage.getContentObject() + "\nSeller: " + aclMessage.getSender().getName());

} catch (UnreadableException ex) {
                            Logger.getLogger(BuyerBehaviour.class  

.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                     try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    myAgent.send(aclMessageInf);
                    break;

                case ACLMessage.INFORM:

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

                        gui.showMessage("[!] Book unavailable or out of stock!\n");
                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                        reply.setConversationId(conversationId);
                        reply.addReceiver(requester);
                        reply.setContent("Book unavailable or out of stock!");
                        try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        myAgent.send(reply);
                        
                        sellerCount = sellerCount-1;
                        
                        System.out.println("book univ" +sellerCount);

                    }
                    if (contentObject3.containsKey("NewOffer")) {

                        gui.showMessage("[!] Received a new proposal from seller:");
                        gui.showMessage("--" + aclMessage.getConversationId());
                        gui.showMessage("From: " + aclMessage.getSender().getName());

                        int sellerOffer = contentObject3.get("NewOffer");
                        gui.showMessage("New Offer: " + sellerOffer + "%");

                        if (sellerOffer <= strategy) {

                            ACLMessage aclMessageP = new ACLMessage(ACLMessage.PROPOSE);
                            aclMessageP.addReceiver(aclMessage.getSender());
                            aclMessageP.setConversationId(conversationId);

                            HashMap<String, Integer> contentObjectR = new HashMap<>();
                            contentObjectR.put("No", sellerOffer);

                            try {
                                aclMessageP.setContentObject(contentObjectR);

} catch (IOException ex) {
                                Logger.getLogger(BuyerBehaviour.class  

.getName()).log(Level.SEVERE, null, ex);
                            }

                            gui.showMessage("[!] Low discount, negociating again..\n");
                           
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            myAgent.send(aclMessageP);
                         
                        } else {

                        }
                    }

                    if (contentObject3.containsKey("LastOffer")) {

                        gui.showMessage("[!] Received a new proposal from seller:");
                        gui.showMessage("--" + aclMessage.getConversationId());
                        gui.showMessage("From: " + aclMessage.getSender().getName());

                        int sellerOffer = contentObject3.get("LastOffer");
                        gui.showMessage("Offer: " + sellerOffer + "%");

                        if (sellerOffer <= strategy) {

                            ACLMessage aclMessageP = new ACLMessage(ACLMessage.PROPOSE);
                            aclMessageP.addReceiver(aclMessage.getSender());
                            aclMessageP.setConversationId(conversationId);

                            HashMap<String, Integer> contentObjectR = new HashMap<>();
int finalOffer = 0;
                            if (sellerOffer == strategy){
                            finalOffer = ThreadLocalRandom.current().nextInt(sellerOffer + 1, strategy);}
                            else {finalOffer = ThreadLocalRandom.current().nextInt(sellerOffer, strategy);}
                            
                            gui.showMessage("[!] Higher discount, but not enough, negociating again..\n");
                            gui.showMessage("[!] Making final offer of: " + finalOffer + "%");

                            contentObjectR.put("FinalProposal", finalOffer);
                            try {
                                aclMessageP.setContentObject(contentObjectR);

} catch (IOException ex) {
                                Logger.getLogger(BuyerBehaviour.class  

.getName()).log(Level.SEVERE, null, ex);
                            }
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(BuyerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            myAgent.send(aclMessageP);
                        }

                    }
                    break;
                    
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
