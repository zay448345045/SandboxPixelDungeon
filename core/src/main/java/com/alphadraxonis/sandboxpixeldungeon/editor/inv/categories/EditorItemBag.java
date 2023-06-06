package com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories;

import com.alphadraxonis.sandboxpixeldungeon.editor.inv.WndEditorInv;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.Bag;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class EditorItemBag extends Bag {

    private final String name;

    public EditorItemBag(String name, int img) {
        this.name = name;
        image = img;
    }

    @Override
    public int capacity() {
        return items.size() + 1;
    }

    public  Image getCategoryImage(){
        return new ItemSprite(image);
    }

    @Override
    public String name() {
        return name;
    }

    public static final EditorItemBag mainBag = new EditorItemBag("main", 0){
        @Override
        public Image getCategoryImage() {
            return null;
        }
    };

    static {
        mainBag.items.add(Tiles.bag);
        mainBag.items.add(Mobs.bag);
        mainBag.items.add(Items.bag);
        mainBag.items.add(Traps.bag);
    }


    public static EditorItemBag getLastBag() {
        EditorItemBag lastBag = WndEditorInv.lastBag();
        if (lastBag != null) return lastBag;
        return getBag(EditorItemBag.class);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Bag> T getBag(Class<T> bagClass) {
        for (Item item : mainBag.items) {
            if (bagClass.isInstance(item)) {
                return (T) item;
            }
        }
        return null;
    }


    public static ArrayList<Bag> getBags() {
        return getBags(mainBag);
    }

    public static ArrayList<Bag> getBags(Bag bag) {
        ArrayList<Bag> list = new ArrayList<>();
        for (Item item : bag.items) {
            if (item instanceof Bag) list.add((Bag) item);
        }
        return list;
    }

    public static Item getFirstItem() {
        return getFirstItem(mainBag);
    }

    public static Item getFirstItem(Bag bag) {
        for (Item item : bag.items) {
            if (!(item instanceof Bag)) return item;
        }
        if (bag.items.size() > 0) return getFirstItem((Bag) bag.items.get(0));
        return null;
    }
}