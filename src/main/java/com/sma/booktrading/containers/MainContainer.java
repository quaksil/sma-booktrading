/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sma.booktrading.containers;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aksil
 */
public class MainContainer {

    public static void main(String[] args) {

        try {
            Runtime runtime = Runtime.instance();
            Properties properties = new ExtendedProperties();
            properties.setProperty(Profile.GUI, "true");
            Profile profile = new ProfileImpl(properties);
            
            AgentContainer mainContainer = runtime.createMainContainer(profile);
            mainContainer.start();
        } catch (ControllerException ex) {
            Logger.getLogger(MainContainer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
