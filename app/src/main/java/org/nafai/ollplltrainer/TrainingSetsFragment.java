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

public class TrainingSetsFragment extends Fragment {
    private WebView mWebView;

    private OnFragmentInteractionListener mListener;

    private Menu mOptionsMenu;

    public TrainingSetsFragment() {
        // Required empty public constructor
    }

    public static TrainingSetsFragment newInstance() {
        TrainingSetsFragment fragment = new TrainingSetsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training_sets, container, false);

        WebPageGenerator generator = new WebPageGenerator(this.getContext().getAssets());
        String pageData = generator.generateTrainingSets( this.getContext());

        this.mWebView = (WebView)view.findViewById(R.id.web_view);
        if (pageData != null) {
            //wv.loadUrl(url);
            this.mWebView.loadData(pageData, "text/html; charset=utf-8", "UTF-8");

            // Enable Javascript
            this.mWebView.getSettings().setJavaScriptEnabled(true);

            // Inject communicator
            /*
            this.mWebView.addJavascriptInterface(new IAlgWebViewCommunicator() {
                @Override
                @JavascriptInterface
                public void algImageClicked(final String id) {
                    if (!TrainingSetsFragment.this.mIsEditMode) {
                        algMarkCompleted(id);

                        return;
                    }

                    algRotate(id);
                }

                @Override
                @JavascriptInterface
                public void algClicked(String id) {
                    if (!TrainingSetsFragment.this.mIsEditMode) {
                        algMarkCompleted(id);

                        return;
                    }

                    algSelectEntry(id);
                }
            }, "android");
            */
        }

        // Disable selection
        /*
        this.mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
            // For final release of your app, comment the toast notification
            return true;
            }
        });
*/
        return view;
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
                TrainingSetsFragment.this.mWebView.loadUrl("javascript:(function() { " + script + " })()");
            }
        });
    }
}
