package org.nafai.ollplltrainer;

import java.util.ArrayList;

/**
 * Created by thora_000 on 09/01/2018.
 */

public class TrainingItem {
    public String IdOfAlgToPractice;

    public ArrayList<String> IdsOfAlgsToPerform = new ArrayList<>();

    public TrainingItem(String idOfAlgToPractice) {
        this.IdOfAlgToPractice = idOfAlgToPractice;
    }
}
