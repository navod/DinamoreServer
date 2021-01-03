/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllerImpl;

import Controller.ChefController;
import Controller.TPOperatorController;
import DB.DBController;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Orders;
import observer.ChefObserver;
import observer.TPOperatorObserver;

/**
 *
 * @author kushantha
 */
public class DinamoreControllerImpl extends UnicastRemoteObject implements TPOperatorController, ChefController {

    public DinamoreControllerImpl() throws RemoteException {

    }
    private ArrayList<TPOperatorObserver> tPOperatorObservers = new ArrayList<>();
    private ArrayList<ChefObserver> chefObservers = new ArrayList<>();
    private LinkedList<Orders> orders = new LinkedList<>();

    @Override
    public void addOperator(TPOperatorObserver tPOperatorObserver) throws RemoteException {
        this.tPOperatorObservers.add(tPOperatorObserver);
    }

    @Override
    public void removeOperator(TPOperatorObserver tPOperatorObserver) throws RemoteException {
        this.tPOperatorObservers.remove(tPOperatorObserver);
    }

    @Override
    public void placeOrder(Orders order) throws RemoteException {
        this.orders.addLast(order);
        for (ChefObserver chefObserver : chefObservers) {
            chefObserver.set(orders.size());
            System.out.println("Size of list : " + chefObserver);
            if (!orders.isEmpty()) {
                chefObserver.setOrder(orders.getFirst());
            } else {
                chefObserver.noOrders();
            }
        }
        for (TPOperatorObserver tPOperatorObserver : tPOperatorObservers) {
            try {
                tPOperatorObserver.orderId(newOrderId());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DinamoreControllerImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(DinamoreControllerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void getOrderId(TPOperatorObserver tPOperatorObserver) throws RemoteException {

        try {
            tPOperatorObserver.orderId(newOrderId());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DinamoreControllerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DinamoreControllerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void addChef(ChefObserver chefObserver) throws RemoteException {
        this.chefObservers.add(chefObserver);
    }

    @Override
    public void removeChef(ChefObserver chefObserver) throws RemoteException {
        this.chefObservers.remove(chefObserver);
    }

    @Override
    public void getNoOfOrders() throws RemoteException {
        for (ChefObserver chefObserver : chefObservers) {
            chefObserver.set(orders.size());
        }
    }

//    @Override
//    public void getOrder() throws RemoteException {
//        if (!orders.isEmpty()) {
//            for (ChefObserver chefObserver : chefObservers) {
//                chefObserver.setOrder(orders.getFirst());
//            }
//        }
//    }
    @Override
    public void takeOrder(Orders order) throws RemoteException {
        orders.remove(order);
        for (ChefObserver chefObserver : chefObservers) {
            chefObserver.set(orders.size());
            if (!orders.isEmpty()) {
                chefObserver.setOrder(orders.getFirst());
            } else {
                chefObserver.noOrders();
            }
        }
    }

    @Override
    public void finishOrder(ChefObserver chefObserver, Orders order) throws RemoteException {

        try {
            boolean add = new DBController().addOrders(order);
            if (add) {
                boolean remove = orders.remove(order);
                if (remove) {
                    chefObserver.set(orders.size());
                    if (!orders.isEmpty()) {
                        chefObserver.setOrder(orders.getFirst());
                    } else {
                        chefObserver.noOrders();
                    }
                }

            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DinamoreControllerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DinamoreControllerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String newOrderId() throws ClassNotFoundException, SQLException, RemoteException {
        if (this.orders.isEmpty()) {

            String lastId = new DBController().getLastOrderId();
            if (lastId == null) {
                return "OD001";
            } else {
                String newId = "";
                String[] split = lastId.split("OD");
                int num = Integer.parseInt(split[1]);
                num++;
                if (num < 10) {
                    newId = "OD00" + num;
                } else if (num < 100) {
                    newId = "OD0" + num;
                } else if (num < 1000) {
                    newId = "OD" + num;
                }
                return newId;
            }
        } else {
            Orders last = this.orders.getLast();
            String newId = "";
            String[] split = last.getOrderId().split("OD");
            int num = Integer.parseInt(split[1]);
            num++;
            if (num < 10) {
                newId = "OD00" + num;
            } else if (num < 100) {
                newId = "OD0" + num;

            } else if (num < 1000) {
                newId = "OD" + num;
            }
            return newId;
        }
    }
}
