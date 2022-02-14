/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sma.booktrading.agents.buyer;

import com.sma.booktrading.agents.consumer.Order;
import com.sma.booktrading.agents.seller.Observer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aksil
 */
public class BuyerAgent extends GuiAgent {

    ParallelBehaviour parallelBehaviour;
    private BuyerPortal gui;

    int strategy;

    private List<Observer> observers = new ArrayList<>();

    @Override
    protected void setup() {

        gui = new BuyerPortal();

        gui.setBuyerAgent(this);

        Object[] args = getArguments();

        if (args.length == 1) {
            strategy = (int) args[0];
        }

        gui.showMessage("[#] Initializing Buyer Agent..");
        gui.showMessage("[#] Agent " + this.getAID().getName() + " deployed successfully.");
        gui.showMessage("[#] Ready..\n");

        parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {

            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

                ACLMessage aclMessage = receive(messageTemplate);

                if (aclMessage != null) {

                    try {

                        Order order = null;

                        try {
                            order = (Order) aclMessage.getContentObject();
                        } catch (UnreadableException ex) {
                            Logger.getLogger(BuyerAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        String book = order.getBook();

                        AID requester = aclMessage.getSender();
                        String conversationId = aclMessage.getConversationId();

                        gui.showMessage("[!] Received book purchase request for: " + book);
                        gui.showMessage("[!] Object: " + conversationId);
                        gui.showMessage("[!] From: " + requester.getName() + "\n");

                        ACLMessage replyMessage = new ACLMessage(ACLMessage.INFORM);

                        replyMessage.addReceiver(requester);
                        replyMessage.setConversationId(conversationId);
                        replyMessage.setContent("[#] Processing request by: " + myAgent.getAID().getName()+"\n");

                        Thread.sleep(3000);

                        myAgent.send(replyMessage);

                        parallelBehaviour.addSubBehaviour(new BuyerBehaviour(myAgent, book, requester, conversationId, gui, strategy));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(BuyerAgent.class.getName()).log(Level.SEVERE, null, ex);
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
    }

    @Override
    protected void onGuiEvent(GuiEvent arg0
    ) {

    }
}
