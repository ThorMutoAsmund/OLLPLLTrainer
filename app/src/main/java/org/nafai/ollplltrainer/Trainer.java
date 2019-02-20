package org.nafai.ollplltrainer;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by thora_000 on 09/01/2018.
 */

public class Trainer {
    private static final String TAG = "Trainer";

    private HashMap<Integer,String> mLLs = new HashMap<>();

    private HashMap<String,String> mKnownAlgs = new HashMap<>();

    private ArrayList<Integer> mOLLHashesToPractice = new ArrayList<>();

    private ArrayList<String> mOLLIdsToPractice = new ArrayList<>();

    private HashMap<String, Integer> mOLLTimesPracticed = new HashMap<>();

    private HashMap<Integer,Integer> mOccurances = new HashMap<>();

    private HashMap<String,ArrayList<TrainingItem>> mTrainingItems = new HashMap<>();

    private int mNumberOfUncompletedOLLs = 0;

    public Trainer(Context context) {
        Cube33 c = new Cube33();
        Prefs prefs = new Prefs(context);

        // Get all OLL patterns
        for (Alg alg : AlgDb.Instance.OLLs) {
            c.init();
            c.performReverse(alg.Entries.get(0));
            int ll = c.getLLAsInt();
            this.mLLs.put(ll,alg.Id);
            //Log.d(TAG, "Alg " +alg.Id + " has hash: " + ll);
            int completion = prefs.getIsEntryCompleted(AlgClass.OLL, alg.Id);
            if (completion < 2) {
                this.mNumberOfUncompletedOLLs++;
                String entry = prefs.getEntry(AlgClass.OLL, alg.Id, null);
                if (!"".equals(entry)) {
                    mOLLHashesToPractice.add(ll);
                    mOLLIdsToPractice.add(alg.Id);
                }

                int timesPracticed = prefs.getTimesPracticed(AlgClass.OLL, alg.Id);
                this.mOLLTimesPracticed.put(alg.Id, timesPracticed);
            }
        }

        // Get known algs
        this.mKnownAlgs.put("","");
        for (Alg alg: AlgDb.Instance.OLLs) {
            int completion = prefs.getIsEntryCompleted(AlgClass.OLL, alg.Id);
            if (completion == 2) {
                String entry = prefs.getEntry(AlgClass.OLL, alg.Id, alg.Entries.get(0));
                if (!"".equals(entry)) {
                    this.mKnownAlgs.put(alg.Id, entry);
                }
            }
        }

        // Get training items
        for (String e1id : this.mKnownAlgs.keySet()) {
            String e1 = this.mKnownAlgs.get(e1id);
            for (String e2id : this.mKnownAlgs.keySet()) {
                String e2 = this.mKnownAlgs.get(e2id);
                if (e2.equals("")) {
                    continue;
                }
                c.init();
                c.perform(e1);
                c.perform(e2);
                int ll = c.getLLAsInt();
                if (this.mOccurances.containsKey(ll)) {
                    this.mOccurances.put(ll, this.mOccurances.get(ll)+1);
                }
                else {
                    this.mOccurances.put(ll,1);
                }

                String idOfResultingAlg = this.mLLs.get(ll);
                ArrayList<TrainingItem> trainingItemsForAlg;
                if (this.mTrainingItems.containsKey(idOfResultingAlg)) {
                    trainingItemsForAlg = (ArrayList)this.mTrainingItems.get(idOfResultingAlg);
                }
                else {
                    trainingItemsForAlg = new ArrayList<>();
                    this.mTrainingItems.put(idOfResultingAlg, trainingItemsForAlg);
                }

                TrainingItem newTrainingItem = new TrainingItem(idOfResultingAlg);
                if (!e1id.equals("")) {
                    newTrainingItem.IdsOfAlgsToPerform.add(e1id);
                }
                newTrainingItem.IdsOfAlgsToPerform.add(e2id);
                trainingItemsForAlg.add(newTrainingItem);
            }
        }
    }

    public TrainingItem createNewTrainingItem() {
        int lowestTimesPracticed = 1000000;
        ArrayList<String> availableOLLIds = new ArrayList<>();
        for (String id : this.mOLLIdsToPractice) {
            // Skip if this cannot be trained
            if (!this.mTrainingItems.keySet().contains(id)) {
                continue;
            }

            int timesPracticed = this.mOLLTimesPracticed.containsKey(id) ? this.mOLLTimesPracticed.get(id) : 0;
            if (timesPracticed == lowestTimesPracticed) {
                availableOLLIds.add(id);
            }
            else if (timesPracticed < lowestTimesPracticed) {
                availableOLLIds.clear();
                availableOLLIds.add(id);
                lowestTimesPracticed = timesPracticed;
            }
        }

        // Nothing to practice! Yeah
        if (availableOLLIds.size() == 0) {
            return null;
        }

        // Select random
        String idToPractice = availableOLLIds.get(new Random().nextInt(availableOLLIds.size()));

        ArrayList<TrainingItem> results = this.mTrainingItems.get(idToPractice);

        return results.get(new Random().nextInt(results.size()));
    }

    public int getPercentageOfUnknownOLLsThatCanBeTrained() {
        int ollsThatCanBeTrained = 0;
        for (int id : this.mOccurances.keySet()) {
            if (mOLLHashesToPractice.contains(id)){
                ollsThatCanBeTrained++;
            }
        }

        return this.mNumberOfUncompletedOLLs == 0 ? 0 : Math.round(100*((float)ollsThatCanBeTrained / this.mNumberOfUncompletedOLLs));
    }

}
