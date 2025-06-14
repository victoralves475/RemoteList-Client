package br.edu.ifpb.remotelist;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteList extends Remote {
    void append(String listId, int value) throws RemoteException;
    int get(String listId, int index) throws RemoteException;
    int remove(String listId) throws RemoteException;
    int size(String listId) throws RemoteException;
}
