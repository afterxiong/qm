package com.shareshow.airpc.socket.common;

import java.util.ArrayList;

/**
 * Created by TEST on 2017/9/18.
 */

public class HelpItem {

    private String itemName;

    private ArrayList<String> childNames;

    public HelpItem(String itemName, ArrayList<String> childNames) {
        this.itemName = itemName;
        this.childNames = childNames;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public ArrayList<String> getChildNames() {
        return childNames;
    }

    public void setChildNames(ArrayList<String> childNames) {
        this.childNames = childNames;
    }
}
