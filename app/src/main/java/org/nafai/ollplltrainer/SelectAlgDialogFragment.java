package org.nafai.ollplltrainer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by thora_000 on 21/12/2017.
 */

public class SelectAlgDialogFragment extends DialogFragment {
    public static final String ARG_ID = "id";

    public static final String ARG_ENTRY = "entry";

    public static final String ARG_ALG_CLASS = "algCLass";

    public static final String ARG_ROTATION = "rotation";

    private String mId;

    private String mInitialEntry;

    private AlgClass mAlgClass;

    private EditText mEditText;

    private int mRotation;

    public static SelectAlgDialogFragment newInstance(String id, String entry, AlgClass algClass, int rotation) {
        SelectAlgDialogFragment dialogFragment = new SelectAlgDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_ENTRY, entry);
        args.putString(ARG_ALG_CLASS, algClass.name());
        args.putInt(ARG_ROTATION, rotation);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mId = getArguments().getString(ARG_ID);
            this.mInitialEntry = getArguments().getString(ARG_ENTRY);
            this.mAlgClass = AlgClass.valueOf(getArguments().getString(ARG_ALG_CLASS));
            this.mRotation = getArguments().getInt(ARG_ROTATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialogfragment_select_alg, container,
                false);
        getDialog().setTitle("Select Algorithm");

        final Spinner spinner = rootView.findViewById(R.id.spAlgorithms);

        Alg alg = AlgDb.Instance.findAlg(this.mAlgClass, this.mId);

        // rotate entries
        ArrayList<String> rotatedEntries = new ArrayList<String>();
        rotatedEntries.add("(custom)");
        for (String entry : alg.Entries) {
            rotatedEntries.add(EntryRotator.rotate(entry, this.mRotation));
        }

        // create an adapter from the list
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                //android.R.layout.simple_spinner_item,
                R.layout.select_alg_spinner_template,
                rotatedEntries
        );
        spinner.setAdapter(adapter);

        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.select_alg_spinner_dropdown_template);

        this.mEditText = rootView.findViewById(R.id.editText);

        // Initial value
        if (this.mInitialEntry != null && this.mInitialEntry != "") {
            this.mEditText.setText(this.mInitialEntry);

            int selectedValuePosition = adapter.getPosition(this.mInitialEntry);
            if (selectedValuePosition > -1) {
                spinner.setSelection(selectedValuePosition);
            }
            else {
                spinner.setSelection(0);
            }
        }
        else {
            spinner.setSelection(1);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (position > 0) {
                    SelectAlgDialogFragment.this.mEditText.setText((String)spinner.getItemAtPosition(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        Button buttonOk = rootView.findViewById(R.id.buttonOK);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String selectedEntry = SelectAlgDialogFragment.this.mEditText.getText().toString();

                Intent intent = new Intent();
                intent.putExtra(ARG_ID, SelectAlgDialogFragment.this.mId);
                intent.putExtra(ARG_ENTRY, selectedEntry);
                //SelectAlgDialogFragment.this.setResult(RESULT_OK, intent);
                //finish();

                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                dismiss();
            }
        });

        return rootView;
    }
}
