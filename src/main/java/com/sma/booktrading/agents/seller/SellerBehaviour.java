/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sma.booktrading.agents.seller;

/**
 *
 * @author aksil
 */
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.core.Agent;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SellerBehaviour extends CyclicBehaviour {

    private String conversationId;
    private SellerPortal gui;
    private String bookName;
    private BookStore myStore;

    private int negoTry = 0;

    Book bookResult = null;
    Double minPrice = new Double(0);
    int quantity = 0;
    int discount = 0;

    

    public SellerBehaviour(Agent agent, String conversationId, SellerPortal gui, String bookName, BookStore myStore) {

        super(agent);
        this.conversationId = conversationId;
        this.gui = gui;
        this.bookName = bookName;
        this.myStore = myStore;
    }

    @Override
    public void action() {

        MessageTemplate messageTemplate = MessageTemplate.and(
                MessageTemplate.MatchConversationId(conversationId),
                MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
                        MessageTemplate.MatchPerformative(ACLMessage.PROPOSE)));

        ACLMessage aclMessage = myAgent.receive(messageTemplate);

        if (aclMessage != null) {
            switch (aclMessage.getPerformative()) {

                case ACLMessage.ACCEPT_PROPOSAL:

                    Book bookResultAP = null;

                    int quantityAP = 0;
                    
                    if (!myStore.getBooks().isEmpty()) {
                        bookResultAP = myStore.lookup(bookName);
                        if (bookResultAP != null) {
                            quantityAP = bookResultAP.getQuantity();
                        }
                    }
                    
                    if (quantityAP > 0) {
                        
                        try {
                            
                            System.out.println(quantityAP);
                            
                            int quantityNew = myStore.lookup(bookName).getQuantity() - 1;
                            
                            myStore.lookup(bookName).setQuantity(quantityNew);
                            
                            gui.showMessage("[#] Finalizing transaction with: "+ aclMessage.getSender().getName());
                            gui.showMessage("[#] For object: " + aclMessage.getConversationId());
                            
                            ACLMessage reply = aclMessage.createReply();
                            
                            reply.setPerformative(ACLMessage.CONFIRM);
                            
                            HashMap<AID, Double> contentObject = new HashMap<>();
                            
                            contentObject = (HashMap<AID, Double>) aclMessage.getContentObject();
                            
                            Double price = 0.0;
                            for(Double finalPrice : contentObject.values())
                            {
                                price = finalPrice;
                            }
                            
                            reply.setContentObject(price);
                            
                            
                            gui.showMessage("[#] Success, sending confirmation..\n");
 
                                Thread.sleep(3000);

                                myAgent.send(reply);
                        } catch (IOException | UnreadableException | InterruptedException ex) {
                                Logger.getLogger(SellerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    } else {

                try {
                    gui.showMessage("[#] The last book was just sold, out of stock.");
                    gui.showMessage("[#] For object: " + aclMessage.getConversationId() + "\n");
                    
                    ACLMessage reponse = new ACLMessage(ACLMessage.INFORM);
                    reponse.addReceiver(aclMessage.getSender());
                    reponse.setConversationId(conversationId);
                    reponse.setContent("OutOfStock");
                    
                    Thread.sleep(3000);
                    
                    myAgent.send(reponse);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SellerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                }
                    }
                    break;

                case ACLMessage.PROPOSE: 

                    
                    HashMap<String, Integer> contentObject = new HashMap<>();
                try {
                    contentObject = (HashMap<String, Integer>) aclMessage.getContentObject();
                } catch (UnreadableException ex) {
                    Logger.getLogger(SellerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                }
             

                if (!myStore.getBooks().isEmpty()) {
                    bookResult = myStore.lookup(bookName);
                    if (bookResult != null) {

                        minPrice = bookResult.getMinPrice();
                        quantity = bookResult.getQuantity();
                        discount = bookResult.getDiscount();
                    }
                }

                if (contentObject.containsKey("FirstProposal")) {
                    int strategy = contentObject.get("FirstProposal");

                    if (strategy > discount) {

                        System.out.println(aclMessage.getContent());

                        gui.showMessage("[#] Received an offer from: " + aclMessage.getConversationId());
                        gui.showMessage("[#] Processing..");
                        
                    if (quantity > 0) {

                        gui.showMessage("[#] They offer: " + strategy + "%");
                        gui.showMessage("[#] Max. possible discount: " + discount + "%");
                        int offer = ThreadLocalRandom.current().nextInt(1, discount - 1);
                        gui.showMessage("[#] Offering: " + offer + "%\n");

                        ACLMessage aclMessageP = new ACLMessage(ACLMessage.INFORM);
                        aclMessageP.addReceiver(aclMessage.getSender());
                        aclMessageP.setConversationId(conversationId);

                        HashMap<String, Integer> contentObjectR = new HashMap<>();

                        contentObjectR.put("NewOffer", offer);
                        try {
                            aclMessageP.setContentObject(contentObjectR);
                        } catch (IOException ex) {
                            Logger.getLogger(SellerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                        }

                          try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(SellerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        myAgent.send(aclMessageP);

                    } else {

                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                        reply.addReceiver(aclMessage.getSender());
                        reply.setConversationId(conversationId);

                        reply.setContent("OutOfStock");
                        gui.showMessage("[!] Out of stock.\n");

  try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(SellerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        myAgent.send(reply);

                    }

                }}

                if (contentObject.containsKey("FinalProposal")) {

                    int finalOffer = contentObject.get("FinalProposal");
                

                    gui.showMessage("[#] Buyer wants: "+finalOffer + "%, considering final offer..");

                    if (bookResult.getPrice() - ((bookResult.getPrice() * finalOffer) / 100) < bookResult.getMinPrice()) {
                        
                        try {
                            gui.showMessage("[#] Denying order with offer: " + finalOffer + "%\n");
                            
                            ACLMessage aclMessageP = new ACLMessage(ACLMessage.REFUSE);
                            aclMessageP.addReceiver(aclMessage.getSender());
                            aclMessageP.setConversationId(conversationId);
                            
                            HashMap<String, AID> contentObjectR = new HashMap<>();
                            contentObjectR.put("OrderRefused", myAgent.getAID());
                            
                            aclMessageP.setContentObject(contentObjectR);
                            
                            myAgent.send(aclMessageP);
                            Thread.sleep(3000);
                            
                           
                        } catch (IOException | InterruptedException ex) {
                            Logger.getLogger(SellerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else {
                        gui.showMessage("[#] Accepting offer: " + finalOffer + "%\n");

                        ACLMessage aclMessageP = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        aclMessageP.addReceiver(aclMessage.getSender());
                        aclMessageP.setConversationId(conversationId);
     
                        //bookResult.setDiscount(finalOffer);
                        //bookResult.setPrice(bookResult.getPrice() - (bookResult.getPrice() * finalOffer) / 100);


                        HashMap<AID, Double> contentObjectR = new HashMap<>();
                        contentObjectR.put(myAgent.getAID(), bookResult.getPrice() - (bookResult.getPrice() * finalOffer) / 100);

                      
                        try {
                            aclMessageP.setContentObject(contentObjectR);
                        } catch (IOException ex) {
                            Logger.getLogger(SellerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    
  try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(SellerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            myAgent.send(aclMessageP);

                 

                    }
                }

                    if (contentObject.containsKey("No")) {

                        try {
                            
                            try {
                                contentObject = (HashMap<String, Integer>) aclMessage.getContentObject();
                            } catch (UnreadableException ex) {
                                Logger.getLogger(SellerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            int offer;
                            int lastOffer = contentObject.get("No");
                            
                            negoTry++;
                            
                            gui.showMessage("[#] Buyer is hesitant, considering a new offer..");
                            
                            if (lastOffer < discount / 2) {
                                
                                offer = ThreadLocalRandom.current().nextInt(discount / 2, discount - 1);
                                
                            } else {
                                
                                offer = discount;
                            }
                            
                            gui.showMessage("[#] Offering: " + offer + "%\n");
                            ACLMessage aclMessageP = new ACLMessage(ACLMessage.INFORM);
                            aclMessageP.addReceiver(aclMessage.getSender());
                            aclMessageP.setConversationId(conversationId);
                            
                            HashMap<String, Integer> contentObjectR = new HashMap<>();
                            
                            contentObjectR.put("LastOffer", offer);
                            
                            
                            aclMessageP.setContentObject(contentObjectR);
                            myAgent.send(aclMessageP);
                            
                            Thread.sleep(3000);
                            
                            
                        } catch (IOException | InterruptedException ex) {
                            Logger.getLogger(SellerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                        }
  
                        
                    }

                    break;

                
            default: break;

                    
                }

            }
         else {
            block();
                }

        }
    }
