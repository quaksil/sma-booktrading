package com.sma.booktrading.agents.buyer;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

class RequestBehaviour extends CyclicBehaviour {

    private String conversationID;
    private AID requester;
    private String livre;
    private double prix;
    private int compteur;
    private List<AID> vendeurs = new ArrayList<>();
    private ArrayList<AID> meilleureOffre = new ArrayList<>();
    private double meilleurPrix;
    private int index;
    int nombreNeg;
    private BuyerPortal gui;
    private int nombreVendeurs = 0;
    private Double budget;

    public RequestBehaviour(Agent agent, String livre, AID requester, String conversationID, BuyerPortal gui, Double budget) {
        
        super(agent);
        this.livre = livre;
        this.budget = budget;
        this.gui = gui;
        this.requester = requester;
        this.conversationID = conversationID;
        
        gui.showMessage("Recherche des services ... \n");
        
        vendeurs = chercherServices(myAgent, "book-selling");
        
        gui.showMessage("Liste des vendeurs trouvés : ");
        
        try {
            for (AID aid : vendeurs) {
                ++nombreVendeurs;
                gui.showMessage("** " + aid.getName());
            }
            ++compteur;
            gui.showMessage("\n");
            gui.showMessage("Requête d'achat du livre : " + livre);
            gui.showMessage("De : " + requester.getName());
            gui.showMessage("\n ------------------------ ");
            ACLMessage msg = new ACLMessage(ACLMessage.CFP);
            msg.setContent(livre);
            msg.setConversationId(conversationID);
            msg.addUserDefinedParameter("compteur", String.valueOf(compteur));
            
            for (AID aid : vendeurs) {
                gui.showMessage("\n A : " + aid.getName());
                gui.showMessage("Envoi de la requête en cours .... ");
                msg.addReceiver(aid);
            }
            Thread.sleep(5000);
            index = 0;
            nombreNeg = 0;
            gui.showMessage("\n ------------------------");
            myAgent.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void action() {
        try {
            MessageTemplate template = MessageTemplate.MatchConversationId(conversationID);
            /*MessageTemplate template=MessageTemplate.and(
					MessageTemplate.MatchConversationId(conversationID)
					MessageTemplate.or(
							MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
							MessageTemplate.MatchPerformative(ACLMessage.INFORM),
							MessageTemplate.MatchPerformative(ACLMessage.CONFIRM))
					);*/
            ACLMessage aclMessage = myAgent.receive(template);
            if (aclMessage != null) {
                switch (aclMessage.getPerformative()) {
                    case ACLMessage.PROPOSE:
                        prix = Double.parseDouble(aclMessage.getContent());
                        gui.showMessage("\n \n ***********************************");
                        gui.showMessage("Réception de l'offre :");
                        gui.showMessage("Conversation ID : " + aclMessage.getConversationId());
                        gui.showMessage("De : " + aclMessage.getSender().getName());
                        gui.showMessage("Prix = " + prix);
                        
                        if (meilleureOffre.isEmpty()) {
                            meilleurPrix = prix;
                            meilleureOffre.add(aclMessage.getSender());
                        } else {
                            if (prix <= meilleurPrix) {
                                meilleurPrix = prix;
                                meilleureOffre.add(aclMessage.getSender());
                            }
                        }
                        ++index;
                        
                        if (index == vendeurs.size()) {
                            index = 0;
                            //////////////////////
                            if (meilleurPrix <= budget) {
                                gui.showMessage("\n \n ********************************");
                                gui.showMessage("Conclusion de la transaction.......");
                                gui.showMessage("Avec : " + meilleureOffre.get(0).getName());
                                gui.showMessage("Conversation ID : " + aclMessage.getConversationId());
                                gui.showMessage("Meilleur prix : " + meilleurPrix);
                                ACLMessage aclMessage2 = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                aclMessage2.addReceiver(meilleureOffre.get(0));
                                aclMessage2.setConversationId(conversationID);
                                gui.showMessage("...... En cours");
                                Thread.sleep(5000);
                                myAgent.send(aclMessage2);
                            } else {
                                nombreNeg = meilleureOffre.size();
                                gui.showMessage("\n \n ********************************");
                                gui.showMessage("Négociation ... !!");
                                gui.showMessage("Conversation ID : " + aclMessage.getConversationId());
                                gui.showMessage("Prix proposé : " + meilleurPrix + "\n Prix voulu : " + budget);
                                for (AID ai : meilleureOffre) {
                                    gui.showMessage(" ------------ ");
                                    gui.showMessage("Avec : " + ai.getName());
                                    ACLMessage aclMessage2 = new ACLMessage(ACLMessage.PROPOSE);
                                    aclMessage2.addReceiver(ai);
                                    aclMessage2.setConversationId(conversationID);
                                    aclMessage2.setContentObject(budget);
                                    gui.showMessage("...... En cours");
                                    Thread.sleep(5000);
                                    myAgent.send(aclMessage2);
                                }
                            }
                        }
                        break;

                    case ACLMessage.ACCEPT_PROPOSAL:
                        meilleurPrix = budget;
                        gui.showMessage("\n \n ********************************");
                        gui.showMessage("Acceptation des conditions.......");
                        gui.showMessage("Conclusion de la transaction.......");
                        gui.showMessage("Avec : " + aclMessage.getSender().getName());
                        gui.showMessage("Conversation ID : " + aclMessage.getConversationId());
                        ACLMessage aclMessage2 = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        aclMessage2.addReceiver(aclMessage.getSender());
                        aclMessage2.setConversationId(conversationID);
                        gui.showMessage("...... En cours");
                        Thread.sleep(5000);
                        myAgent.send(aclMessage2);
                        break;

                    case ACLMessage.CONFIRM:
                        gui.showMessage("\n \n ********************************");
                        gui.showMessage("Reçu de la confirmation ...");
                        gui.showMessage("Conversation ID : " + aclMessage.getConversationId());
                        gui.showMessage("De : " + aclMessage.getSender().getName());

                        ACLMessage msg3 = new ACLMessage(ACLMessage.INFORM);
                        msg3.addReceiver(requester);
                        msg3.setConversationId(conversationID);
                        msg3.setContent("Reçu de la confirmation !! \n" + "Livre : " + livre + "\n Meilleur Prix : " + meilleurPrix + "\n Fournisseur : " + aclMessage.getSender().getName());
                        myAgent.send(msg3);
                        break;

                    case ACLMessage.INFORM:
                        gui.showMessage("\n \n ******************************");
                        gui.showMessage("Emetteur : " + aclMessage.getSender().getName());
                        gui.showMessage("Conversation ID : " + aclMessage.getConversationId());
                        gui.showMessage(aclMessage.getContent());
                        switch (aclMessage.getContent()) {
                            case "Livre non disponible !!":
                                --nombreVendeurs;
                                ++index;
                                if (index == vendeurs.size()) {
                                    ////////////////////
                                    if (index == vendeurs.size()) {
                                        index = 0;
                                        //////////////////////
                                        if (meilleurPrix <= budget) {
                                            gui.showMessage("\n \n ********************************");
                                            gui.showMessage("Conclusion de la transaction.......");
                                            gui.showMessage("Avec : " + meilleureOffre.get(0).getName());
                                            gui.showMessage("Conversation ID : " + aclMessage.getConversationId());
                                            gui.showMessage("Meilleur prix : " + meilleurPrix);
                                            ACLMessage aclMessage4 = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                            aclMessage4.addReceiver(meilleureOffre.get(0));
                                            aclMessage4.setConversationId(conversationID);
                                            gui.showMessage("...... En cours");
                                            Thread.sleep(5000);
                                            myAgent.send(aclMessage4);
                                        } else {
                                            nombreNeg = meilleureOffre.size();
                                            gui.showMessage("\n \n ********************************");
                                            gui.showMessage("Négociation ... !!");
                                            gui.showMessage("Conversation ID : " + aclMessage.getConversationId());
                                            gui.showMessage("Prix proposé : " + meilleurPrix + "\n Prix voulu : " + budget);
                                            for (AID ai : meilleureOffre) {
                                                gui.showMessage(" ------------ ");
                                                gui.showMessage("Avec : " + ai.getName());
                                                ACLMessage aclMessage3 = new ACLMessage(ACLMessage.PROPOSE);
                                                aclMessage3.addReceiver(ai);
                                                aclMessage3.setConversationId(conversationID);
                                                aclMessage3.setContentObject(budget);
                                                gui.showMessage("...... En cours");
                                                Thread.sleep(5000);
                                                myAgent.send(aclMessage3);
                                            }
                                        }
                                    }
                                }
                                if (nombreVendeurs == 0) {
                                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                                    reply.setConversationId(conversationID);
                                    reply.addReceiver(requester);
                                    reply.setContent("Livre non disponible !!");
                                    myAgent.send(reply);
                                }
                                break;
                            case "Quantité épuisée ... !!!":
                                ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                                reply.setConversationId(conversationID);
                                reply.addReceiver(requester);
                                reply.setContent("Quantité épuisée ... !!!");
                                myAgent.send(reply);
                                break;
                            default:
                                --nombreNeg;
                                if (nombreNeg == 0) {
                                    ACLMessage rep = new ACLMessage(ACLMessage.INFORM);
                                    rep.setConversationId(conversationID);
                                    rep.addReceiver(requester);
                                    rep.setContent("Budget dépassé !!");
                                    myAgent.send(rep);
                                }
                                break;
                        }
                        break;
                }
            } else {
                block();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<AID> chercherServices(Agent agent, String type) {
        List<AID> vendeurs = new ArrayList<>();
        DFAgentDescription agentDescription = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(type);
        agentDescription.addServices(serviceDescription);
        try {
            DFAgentDescription[] descriptions = DFService.search(agent, agentDescription);
            for (DFAgentDescription dfad : descriptions) {
                vendeurs.add(dfad.getName());
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return vendeurs;
    }
}
