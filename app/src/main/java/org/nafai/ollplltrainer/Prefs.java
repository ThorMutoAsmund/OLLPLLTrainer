package org.nafai.ollplltrainer;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by thora_000 on 29/12/2017.
 */

public class Prefs {
    private SharedPreferences mSharedPrefs;

    private Context mContext;

    private String mDefaultFileName = "ollplltrainer";

    public Prefs(Context context) {
        this.mContext = context;
        this.mSharedPrefs = context.getSharedPreferences(
                MainActivity.PACKAGE_NAME, MODE_PRIVATE);
    }

    private boolean exportToFile(File dst) {
        boolean res = false;
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(dst));
            output.writeObject(this.mSharedPrefs.getAll());

            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    // @SuppressWarnings({ "unchecked" })
    private boolean importFromFile(File src) {
        boolean res = false;
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new FileInputStream(src));
            SharedPreferences.Editor prefEdit = this.mSharedPrefs.edit();
            prefEdit.clear();
            Map<String, ?> entries = (Map<String, ?>) input.readObject();
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                Object v = entry.getValue();
                String key = entry.getKey();

                if (v instanceof Boolean)
                    prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
                else if (v instanceof Float)
                    prefEdit.putFloat(key, ((Float) v).floatValue());
                else if (v instanceof Integer)
                    prefEdit.putInt(key, ((Integer) v).intValue());
                else if (v instanceof Long)
                    prefEdit.putLong(key, ((Long) v).longValue());
                else if (v instanceof String)
                    prefEdit.putString(key, ((String) v));
            }
            prefEdit.commit();
            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    public boolean exportDefault() {
        File file = new File(this.mContext.getFilesDir(), this.mDefaultFileName);
        return exportToFile(file);
    }

    public boolean importDefault() {
        File file = new File(this.mContext.getFilesDir(), this.mDefaultFileName);
        return importFromFile(file);
    }

    public int getRotation(AlgClass algClass, String id) {
        return this.mSharedPrefs.getInt(algClass.toString() + "_rot_" + id, 0);
    }

    public String getEntry(AlgClass algClass, String id, String defaultValue) {
        return this.mSharedPrefs.getString(algClass.toString() + "_entry_" + id, defaultValue);
    }

    public int getIsEntryCompleted(AlgClass algClass, String id) {
        return this.mSharedPrefs.getInt(algClass.toString() + "_completedlevel_" + id, 0);
    }

    public int getTimesPracticed(AlgClass algClass, String id) {
        return this.mSharedPrefs.getInt(algClass.toString() + "_timespracticed_" + id, 0);
    }

    public void setRotation(AlgClass algClass, String id, int rot) {
        SharedPreferences.Editor editor = this.mSharedPrefs.edit();
        editor.putInt(algClass.toString() + "_rot_" + id, rot);
        editor.commit();
    }

    public void setEntry(AlgClass algClass, String id, String entry) {
        SharedPreferences.Editor editor = this.mSharedPrefs.edit();
        editor.putString(algClass.toString() + "_entry_" + id, entry);
        editor.commit();
    }

    public void setIsEntryCompleted(AlgClass algClass, String id, int level) {
        SharedPreferences.Editor editor = this.mSharedPrefs.edit();
        editor.putInt(algClass.toString() + "_completedlevel_" + id, level);
        editor.commit();
    }

    public void increaseTimesPracticed(AlgClass algClass, String id) {
        int timesPracticed = getTimesPracticed(algClass, id);
        SharedPreferences.Editor editor = this.mSharedPrefs.edit();
        editor.putInt(algClass.toString() + "_timespracticed_" + id, timesPracticed + 1);
        editor.commit();
    }
}