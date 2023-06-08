package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo;

import java.util.ArrayList;
import java.util.List;


public class ActionPartList implements ActionPart {

    private final List<ActionPart> actions = new ArrayList<>(5);

    public void addActionPart(ActionPart part) {
        if (part != null && part.hasContent()) actions.add(part);
    }

    public void undo() {
        for (int i = actions.size() - 1; i >= 0; i--) {
            undoAction(actions.get(i));
        }
    }

    public void redo() {
        for (ActionPart action : actions) {
            redoAction(action);
        }
    }

    protected void undoAction(ActionPart action) {
        action.undo();
    }

    protected void redoAction(ActionPart action) {
        action.redo();
    }

    @Override
    public boolean hasContent() {
        return !actions.isEmpty();
    }
}