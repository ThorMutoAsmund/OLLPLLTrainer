package org.nafai.ollplltrainer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class AlgListFragment extends Fragment {
    private static final String ARG_ALG_CLASS = "algClass";

    private static final int SELECT_ALG_REQUEST_CODE = 1;

    private WebView mWebView;

    private AlgClass mAlgClass;

    private boolean mIsEditMode;

    private int mRenderSize = 0;

    private boolean mDisableOptionsItemSelected;

    private Prefs mPrefs;

    private OnFragmentInteractionListener mListener;

    private Menu mOptionsMenu;

    public AlgListFragment() {
        // Required empty public constructor
        this.mAlgClass = AlgClass.OLL; // default
    }

    public static AlgListFragment newInstance(AlgClass algClass) {
        AlgListFragment fragment = new AlgListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ALG_CLASS, algClass.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mAlgClass = AlgClass.valueOf(getArguments().getString(ARG_ALG_CLASS));
        }

        this.mPrefs = new Prefs(this.getContext());

        if (savedInstanceState != null) {
            this.mIsEditMode = savedInstanceState.getBoolean("isEditMode");
            this.mRenderSize = savedInstanceState.getInt("renderSize");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isEditMode", this.mIsEditMode);
        outState.putInt("renderSize", this.mRenderSize);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alg_list, container, false);

        WebPageGenerator generator = new WebPageGenerator(this.getContext().getAssets());
        String pageData = generator.generate(this.mAlgClass, this.mIsEditMode, this.mRenderSize, this.getContext());

        this.mWebView = (WebView)view.findViewById(R.id.web_view);
        if (pageData != null) {
            //wv.loadUrl(url);
            this.mWebView.loadData(pageData, "text/html; charset=utf-8", "UTF-8");

            // Enable Javascript
            this.mWebView.getSettings().setJavaScriptEnabled(true);

            // Inject communicator
            this.mWebView.addJavascriptInterface(new IAlgWebViewCommunicator() {
                @Override
                @JavascriptInterface
                public void algImageClicked(final String id) {
                    if (!AlgListFragment.this.mIsEditMode) {
                        algMarkCompleted(id);

                        return;
                    }

                    algRotate(id);
                }

                @Override
                @JavascriptInterface
                public void algClicked(String id) {
                    if (!AlgListFragment.this.mIsEditMode) {
                        algMarkCompleted(id);

                        return;
                    }

                    algSelectEntry(id);
                }
            }, "android");
        }

        // Disable selection
        this.mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
            // For final release of your app, comment the toast notification
            return true;
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    */

    private void algMarkCompleted(final String id) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                which = (-which)-1;
                AlgListFragment.this.mPrefs.setIsEntryCompleted(AlgListFragment.this.mAlgClass, id, which);

                String theClass = "row level" + which;
                executeInWebView("document.getElementById('algRow_" + id + "').className = '"+theClass+"';");

                dialog.dismiss();
            }
        };

        final int isCompleted = this.mPrefs.getIsEntryCompleted(this.mAlgClass, id);
        new AlertDialog.Builder(this.getContext()).setTitle("Mark Completed")
            .setMessage("Set completeness level?")
            .setPositiveButton("HARD",onClickListener)
            .setNegativeButton("MEDIUM",onClickListener)
            .setNeutralButton("EASY",onClickListener)
            .show();
    }

    private void algRotate(String id) {
        int rotation = this.mPrefs.getRotation(this.mAlgClass, id);
        rotation = (rotation  + 90) % 360;

        this.mPrefs.setRotation(this.mAlgClass, id, rotation);

        executeInWebView("document.getElementById('algImage_" + id + "').style.transform = 'rotate(" + rotation + "deg)';");

        // Get current alg and rotate it
        Alg alg = AlgDb.Instance.findAlg(this.mAlgClass, id);

        if (alg != null) {
            String entry = this.mPrefs.getEntry(this.mAlgClass, id, alg.Entries.get(0));
            entry = EntryRotator.rotate(entry, 90);
            setNewEntry(id, entry);
        }
    }

    private void algSelectEntry(String id) {
        FragmentManager fragmentManager = this.getFragmentManager();

        String entry = this.mPrefs.getEntry(this.mAlgClass, id, "");
        int rotation = this.mPrefs.getRotation(this.mAlgClass, id);
        SelectAlgDialogFragment dFragment = SelectAlgDialogFragment.newInstance(id,
                entry, this.mAlgClass, rotation);
        dFragment.setTargetFragment(this, SELECT_ALG_REQUEST_CODE);

        dFragment.show(fragmentManager, "Select Algorithm");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        this.mOptionsMenu = menu;

        MenuItem menuItem = this.mOptionsMenu.findItem(R.id.action_editmode);

        this.mDisableOptionsItemSelected = true;
        menuItem.setChecked (false);
        this.mDisableOptionsItemSelected = false;
        menuItem.setChecked(this.mIsEditMode);

        menuItem = this.mOptionsMenu.findItem(R.id.action_changesize);
        menuItem.setTitle("Change size ("+this.mRenderSize+")");

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!this.mDisableOptionsItemSelected) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_editmode) {
                this.mIsEditMode = !item.isChecked();
                item.setChecked(this.mIsEditMode);

                if (this.mIsEditMode) {
                    executeInWebView("document.getElementById('algTable').style.backgroundColor = '" + WebPageGenerator.EditColor + "';");
                } else {
                    executeInWebView("document.getElementById('algTable').style.backgroundColor = '';");
                }

                return true;
            }
            else if (id == R.id.action_changesize) {
                this.mRenderSize = (this.mRenderSize + 1) % 3;
                MenuItem menuItem = this.mOptionsMenu.findItem(R.id.action_changesize);
                menuItem.setTitle("Change size ("+this.mRenderSize+")");
                executeInWebView("document.getElementById('algTable').className = 'rendersize"+this.mRenderSize+"';");
            }
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch(requestCode) {
            case SELECT_ALG_REQUEST_CODE:

                String id = intent.getStringExtra(SelectAlgDialogFragment.ARG_ID);
                String entry = intent.getStringExtra(SelectAlgDialogFragment.ARG_ENTRY);
                if (resultCode == Activity.RESULT_OK) {
                    if (entry != null) {
                        setNewEntry(id, entry);
                    }
                } else if (resultCode == Activity.RESULT_CANCELED){
                    // After Cancel code.
                }

                break;
        }
    }

    private void setNewEntry(String id, String entry) {
        this.mPrefs.setEntry(this.mAlgClass, id, entry);

        String entryEncoded = entry.replace("\\", "\\\\'").replace("'", "\\'");
        executeInWebView("document.getElementById('algEntry_" + id + "').textContent = '" + entryEncoded + "';");
    }

    /**
     * Execute the script in the current webview
     * @param script
     */
    private void executeInWebView(final String script) {
        this.mWebView.post(new Runnable() {
            @Override
            public void run() {
                AlgListFragment.this.mWebView.loadUrl("javascript:(function() { " + script + " })()");
            }
        });
    }
}
