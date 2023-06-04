package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.ui.Component;

import java.util.Locale;

public class MobStateSpinner extends Spinner {


    public MobStateSpinner(Mob mob) {
        super(new MobStateSpinnerModel(mob), " State:", 10);

        addChangeListener(() -> ((States) getValue()).applyChange(mob));
    }

    private enum States {
        SLEEPING,
        HUNTING,
        WANDERING,
        PASSIVE,
        FLEEING;

        public static int getIndex(Mob mob) {
            if (mob.state == mob.SLEEPING) return 0;
            if (mob.state == mob.HUNTING) return 1;
            if (mob.state == mob.PASSIVE) return 3;
            if (mob.state == mob.FLEEING) return 4;
            return 2;//Wandering is default
        }

        public void applyChange(Mob mob) {
            switch (this) {
                case SLEEPING:
                    mob.state = mob.SLEEPING;
                    break;
                case HUNTING:
                    mob.state = mob.HUNTING;
                    break;
                case WANDERING:
                    mob.state = mob.WANDERING;
                    break;
                case PASSIVE:
                    mob.state = mob.PASSIVE;
                    break;
                case FLEEING:
                    mob.state = mob.FLEEING;
                    break;
            }
        }
    }

    private static class MobStateSpinnerModel extends SpinnerTextModel {

        public MobStateSpinnerModel(Mob mob) {
            super(true, States.getIndex(mob), (Object[]) States.values());
        }

        @Override
        public Component createInputField(int fontSize) {
            inputField = new Spinner.SpinnerTextBlock(Chrome.get(getChromeType()), 9);
            return inputField;
        }

        @Override
        protected String getAsString(Object value) {
            States state = (States) value;
            return Messages.get(MobStateSpinner.class,state.name().toLowerCase(Locale.ENGLISH));
        }
    }
}