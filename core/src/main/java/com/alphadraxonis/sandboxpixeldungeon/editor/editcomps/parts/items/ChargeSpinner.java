package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.items;

import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.artifacts.Artifact;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.Wand;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;

public class ChargeSpinner extends Spinner {


    public ChargeSpinner(Wand wand) {
        super(new LevelSpinner.LevelSpinnerModel(wand.curCharges, wand.maxCharges), " " + Messages.get(ChargeSpinner.class, "label") + ":", 10);
        addChangeListener(() -> {
            wand.curCharges = (int) getValue();
            onChange();
        });
    }

    public ChargeSpinner(Artifact artifact) {
        super(new LevelSpinner.LevelSpinnerModel(artifact.charge(), artifact.chargeCap()), " " + Messages.get(ChargeSpinner.class, "label") + ":", 10);
        addChangeListener(() -> {
            artifact.charge((int) getValue());
            onChange();
        });
    }

    protected void onChange() {
    }

    public void adjustMaximum(Item item) {
        SpinnerIntegerModel model = (SpinnerIntegerModel) getModel();
        int maxCharges;
        if (item instanceof Wand) {
            maxCharges = ((Wand) item).maxCharges;
        } else if (!(item instanceof Artifact)) throw new IllegalArgumentException("Error in line 37 hehehehe!");
        else maxCharges = ((Artifact) item).chargeCap();

        if (model.getValue() == model.getMaximum()) model.setValue(maxCharges);
        model.setMaximum(maxCharges);
    }
}