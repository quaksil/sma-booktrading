/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sma.booktrading.agents.seller;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sma.booktrading.agents.buyer.BuyerAgent;

/**
 *
 * @author aksil
 */
public class SellerAgent extends GuiAgent{

    private SellerPortal gui;
    BookStore myStore = new BookStore();

    private ParallelBehaviour parallelBehaviour;

    @Override
    protected void setup() {

        gui = new SellerPortal();
        gui.setSellerAgent(this);

        gui.showMessage("[#] Initializing Seller Agent..");
        gui.showMessage("Agent: " + this.getAID().getName() + "deployed successfully.");
        gui.showMessage("Ready..\n");

        gui.showMessage("[#] Publishing services.. ");

        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(this.getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("book-selling");
        serviceDescription.setName("book-trading");

        agentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, agentDescription);
            gui.showMessage("[!] Successfully published services.\n");

        } catch (FIPAException ex) {
            Logger.getLogger(SellerAgent.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {

                MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                ACLMessage aclMessage = receive(messageTemplate);

                if (aclMessage != null) {

                    gui.showMessage("[!] CFP: " + aclMessage.getConversationId());
                    gui.showMessage("[!] From: " + aclMessage.getSender().getName() + "\n");

                    String bookName = aclMessage.getContent();
                    Book book = null;
                    Double price = new Double(0);

                    int quantity = 0;

                    if (!myStore.getBooks().isEmpty()) {
                        book = myStore.lookup(bookName);

                        if (book != null) {
                            price = myStore.lookup(bookName).getPrice();
                            quantity = myStore.lookup(bookName).getQuantity();
                        }
                    }

                    if (bookName == null || quantity == 0) {

                        try {
                            ACLMessage reply = aclMessage.createReply();
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent("Book unavailable or Out of Stock!");
                            gui.showMessage("[!] Book unavailable or out of stock!\n");
                            Thread.sleep(2000);
                            send(reply);

                        } catch (InterruptedException ex) {
                            Logger.getLogger(SellerAgent.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }

                    } else {
                        try {
                            ACLMessage reply = aclMessage.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(price.toString());
                            gui.showMessage("[#] Processing..\n");
                            Thread.sleep(2000);
                            send(reply);

                            parallelBehaviour.addSubBehaviour(new SellerBehaviour(myAgent, aclMessage.getConversationId(), gui, bookName, myStore));

                        } catch (InterruptedException ex) {
                            Logger.getLogger(SellerAgent.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    block();
                }

            }
        });
    }

    @Override
    protected void beforeMove() {
        gui.showMessage("Before migrating..");
    }

    @Override
    protected void afterMove() {
        gui.showMessage("After migrating..");
    }

    @Override
    protected void takeDown() {
        gui.showMessage("Before dying..");
        try {
            DFService.deregister(this);

        } catch (FIPAException ex) {
            Logger.getLogger(SellerAgent.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void onGuiEvent(GuiEvent ev) {
        switch (ev.getType()) {
            case 1:
                Map<String, Object> bookInfo = (Map<String, Object>) ev.getParameter(0);

                String bookName = (String) bookInfo.get("bookName");
                Double price = (Double) bookInfo.get("price");

                Double minPrice = (Double) ev.getParameter(1);
                int discount = (int) ev.getParameter(2);
                int quantity = (int) ev.getParameter(3);

                myStore.add(new Book(bookName, price, discount, minPrice, quantity));

                gui.showMessage("[#] Adding book..");
                gui.showMessage("Book successfully added to store.\n");

                break;
        }

    }

}
