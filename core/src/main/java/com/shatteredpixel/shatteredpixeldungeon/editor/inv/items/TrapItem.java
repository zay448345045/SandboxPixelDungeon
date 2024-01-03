package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditTrapComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.TrapActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class TrapItem extends EditorItem<Trap> {

    public TrapItem(){}
    public TrapItem(Trap trap) {
        this.obj = trap;
    }

    private static int imgCode(Trap trap) {
        if (trap != null)
            return (trap.active ? trap.color : Trap.BLACK) + (trap.shape * 16) + (trap.visible ? 0 : 128);
        else return -1;
    }

    @Override
    public Image getSprite() {
        return getTrapImage(getObject());
    }

    public static Image getTrapImage(Trap trap) {
        return EditorUtilies.getTerrainFeatureTexture(imgCode(trap));
    }

    public static String createTitle(Trap trap) {
        return Messages.titleCase((trap.visible ? trap.name() : Messages.get(TrapItem.class, "title_hidden", trap.name())));
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, createTitle(getObject()), getSprite()) {
            @Override
            public void onUpdate() {
                if (item == null || ((TrapItem) item).getObject() == null) return;
                Trap t = ((TrapItem) item).getObject();
                label.text(TrapItem.createTitle(t));

                if (icon != null) remove(icon);
                icon = TrapItem.getTrapImage(t);
                addToBack(icon);
                remove(bg);
                addToBack(bg);

                super.onUpdate();
            }
        };
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditTrapComp(this);
    }

    @Override
    public void place(int cell) {
        if (validPlacement(cell, EditorScene.customLevel()))
            Undo.addActionPart(place(getObject().getCopy(), cell));
    }

    public static boolean validPlacement(int cell, CustomLevel level) {
        return level.insideMap(cell);
    }

    @Override
    public String name() {
        return getObject().name();
    }

    @Override
    public void setObject(Trap obj) {
        Trap copy = obj.getCopy();
        copy.pos = -1;
        super.setObject(copy);
    }

    public static int getTerrain(Trap trap) {
        return trap.visible ? (trap.active ? Terrain.TRAP : Terrain.INACTIVE_TRAP) : Terrain.SECRET_TRAP;
    }


    public static TrapActionPart.Remove remove(Trap trap) {
        if (trap != null) {
            return new TrapActionPart.Remove(trap);
        }
        return null;
    }

    public static TrapActionPart.Place place(Trap trap) {
        if (trap != null && !EditTrapComp.areEqual(Dungeon.level.traps.get(trap.pos), trap))
            return new TrapActionPart.Place(trap);
        return null;
    }

    public static TrapActionPart.Place place(Trap trap, int cell) {
        if (trap != null && !EditTrapComp.areEqual(Dungeon.level.traps.get(cell), trap)) {
            trap.pos = cell;
            return new TrapActionPart.Place(trap);
        }
        return null;
    }

    private static final String TRAP = "trap";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TRAP, obj);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        obj = (Trap) bundle.get(TRAP);
    }
}