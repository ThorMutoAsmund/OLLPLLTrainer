package org.nafai.ollplltrainer;

import java.util.ArrayList;

/**
 * Created by thora_000 on 19/12/2017.
 */

public class Alg {
    public String Id;
    public String Names;
    public String UsedIn;
    public ArrayList<String> Entries;

    public Alg(String id, String names, String usedIn) {
        this.Id = id;
        this.Names = names;
        this.UsedIn = usedIn;

        this.Entries = new ArrayList<String>();
    }
}