package org.nafai.ollplltrainer;

import java.util.ArrayList;

/**
 * Created by thora_000 on 19/12/2017.
 */

public class AlgGroup {
    public String Name;
    public ArrayList<String> Entries;

    public AlgGroup(String name) {
        this.Name = name;

        this.Entries = new ArrayList<String>();
    }
}