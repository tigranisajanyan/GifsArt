package com.gifsart.studio.clipart.util;


import com.gifsart.studio.clipart.view.Item;

import java.util.ArrayList;

public class ItemContext {

    public static ItemContext context = null;

    private ArrayList<Item> itemCollection;
    private int selected = -1;
    private int previousSelected = -1;
    private int replacementIndex = -1;

    private ItemContext() {
        init();
    }

    public static ItemContext getContext() {
        if (context == null) {
            context = new ItemContext();
        }
        return context;
    }

    private void init() {
        itemCollection = new ArrayList<Item>();
        selected = -1;
        previousSelected = -1;
        replacementIndex = -1;
    }

    public void addToItemCollection(Item item, boolean isSelected) {
        itemCollection.add(item);
        if (isSelected) {
            selected = itemCollection.size() - 1;
        }
    }

    public void addToItemCollection(Item item, int replacePosition, boolean isSelected) {
        if (replacePosition > -1) {
            itemCollection.set(replacePosition, item);
        } else {
            itemCollection.add(item);
        }
        if (isSelected) {
            selected = replacePosition;
        }
    }

    public void bringToFront(int position) {
        if (position > -1) {
            if (position == selected) { return; }
            Item item = itemCollection.get(position);
            Item prevItem = (previousSelected > -1) ? itemCollection.get(previousSelected) : null;

            itemCollection.remove(position);
            itemCollection.add(item);

            selected = itemCollection.indexOf(item);
            if (prevItem != null) {
                previousSelected = itemCollection.indexOf(prevItem);
            }
        }
    }

    public boolean removeFromCollection(int position) {
        if (position > -1) { return removeFromCollection(itemCollection.get(position)); }
        return false;
    }

    public boolean removeFromCollection(Item item) {
        if (itemCollection.contains(item)) {
            item.clearData();
            Item prevItem = (previousSelected > -1) ? itemCollection.get(previousSelected) : null;
            itemCollection.remove(item);
            selected = itemCollection.size() - 1;
            previousSelected = (prevItem == null) ? -1 : itemCollection.indexOf(prevItem);
            return true;
        }
        return false;
    }

    public void setItemColletion(ArrayList<Item> itemCollection) {
        this.itemCollection = itemCollection;
    }

    public ArrayList<Item> getItemCollection() {
        return itemCollection;
    }

    public Item getItem(int pos) {
        if (pos > -1 && pos < itemCollection.size()) { return itemCollection.get(pos); }
        return null;
    }

    public Item getSelectedItem() {
        if (selected > -1) {
            return itemCollection.get(selected);
        } else {
            return null;
        }
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public int getSelected() {
        return selected;
    }

    public int getPreviousSelected() {
        return previousSelected;
    }

    public void setPreviousSelected(int previousSelected) {
        this.previousSelected = previousSelected;
    }

    public int getReplacementIndex() {
        return replacementIndex;
    }

    public void setReplacementIndex(int replacementIndex) {
        this.replacementIndex = replacementIndex;
    }

    public void clearData() {
        if (itemCollection != null) {
            for (Item item : itemCollection) {
                item.clearData();
            }
            itemCollection.clear();
            init();
        }
    }

}
