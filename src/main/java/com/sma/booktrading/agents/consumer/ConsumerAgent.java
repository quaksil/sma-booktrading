/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sma.booktrading.agents.consumer;

import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author aksil
 */
public class ConsumerAgent extends GuiAgent {

    ParallelBehaviour parallelBehaviour;
    private ConsumerPortal gui;
    int requesterCount;

    @Override
    protected void setup() {

        gui = new ConsumerPortal();
        gui.setConsumerAgent(this);

        gui.showMessage("[#] Initializing Consumer Agent..");
        gui.showMessage("[#] Agent " + this.getAID().getName() + " deployed successfully.");
        gui.showMessage("[#] Ready..\n");

        parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage aclMessage = receive(messageTemplate);

                if (aclMessage != null) {

                    //gui.showMessage("[!] Buyer's Notice:");
                    gui.showMessage(aclMessage.getContent());
                    //gui.showMessage("[#] Object: " + aclMessage.getConversationId() + "\n");

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
    public void onGuiEvent(GuiEvent ev) {

        switch (ev.getType()) {
            case 1:
                Map<String, Object> consumerOrder = (Map<String, Object>) ev.getParameter(0);

                String book = (String) consumerOrder.get("book");
                String buyer = (String) consumerOrder.get("buyer");

                ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);

                ++requesterCount;

                String conversationId = "transaction_" + book + "_" + this.getAID().getName() + "_" + requesterCount;
                aclMessage.setConversationId(conversationId);
                

                Order order = new Order(book);

                try {
                    aclMessage.setContentObject(order);
                } catch (IOException e) {
                    // TODO Auto-generated catch block

                }

                aclMessage.addReceiver(new AID(buyer, AID.ISLOCALNAME));

                gui.showMessage("[#] Forwarding order for buyer agent..");
                gui.showMessage("[#] Object: " + aclMessage.getConversationId() + "\n");

                send(aclMessage);

                break;
        }
    }

}
