/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import controllerImpl.DinamoreControllerImpl;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kushantha
 */
public class ServerStart {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Registry dinamoreRegistry = LocateRegistry.createRegistry(5050);
            dinamoreRegistry.rebind("Dinamore", new DinamoreControllerImpl());
            System.out.println("Starting Server");
        } catch (RemoteException ex) {
            Logger.getLogger(ServerStart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
