package org.nafai.ollplltrainer;

import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/*
https://chrisrisner.com/Using-Fragments-with-the-Navigation-Drawer-Activity
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        IntroFragment.OnFragmentInteractionListener,
        AlgListFragment.OnFragmentInteractionListener,
        WebViewFragment.OnFragmentInteractionListener,
        TrainingIntroFragment.OnFragmentInteractionListener,
        TrainingFragment.OnFragmentInteractionListener{

    private static final String TAG = "MainActivity";

    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass = IntroFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
        // Set image
        try {
            View headerLayout = navigationView.getHeaderView(0); // 0-index header
            Bitmap bm = getBitmapFromAsset("logo.png");
            ImageView i = (ImageView)headerLayout.findViewById(R.id.logo);
            i.setImageBitmap(bm);
        } catch (IOException e) {
            // nothing
        }*/
    }

    private Bitmap getBitmapFromAsset(String strName) throws IOException
    {
        AssetManager assetManager = getAssets();
        InputStream istr = assetManager.open(strName);
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        CharSequence notImplementedText = "Not implemented yet";

        Fragment fragment = null;

        if (id == R.id.nav_notations) {
            fragment = WebViewFragment.newInstance("file:///android_asset/notations.html");
        } else if (id == R.id.nav_cfop) {
            fragment = WebViewFragment.newInstance("file:///android_asset/cfop.html");
        } else if (id == R.id.nav_oll) {
            fragment = AlgListFragment.newInstance(AlgClass.OLL);
        } else if (id == R.id.nav_pll) {
            fragment = AlgListFragment.newInstance(AlgClass.PLL);
        } else if (id == R.id.nav_training) {
            fragment = TrainingIntroFragment.newInstance();
        } else if (id == R.id.nav_training_sets) {
            fragment = TrainingSetsFragment.newInstance();
        } else if (id == R.id.nav_export) {
            //Toast toast = Toast.makeText(this, notImplementedText, duration);
            //toast.show();
            new AlertDialog.Builder(this).setTitle("Confirm Export")
                .setMessage("Are you sure you want to export the settings?")
                .setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            boolean result = new Prefs(MainActivity.this).exportDefault();
                            Toast toast = Toast.makeText(MainActivity.this, result ? "Successfully exported settings" : "Export failed", Toast.LENGTH_SHORT);
                            toast.show();
                            dialog.dismiss();
                        }
                    })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                })
                .show();
        } else if (id == R.id.nav_import) {
            //Toast toast = Toast.makeText(this, notImplementedText, duration);
            //toast.show();
            new AlertDialog.Builder(this).setTitle("Confirm Export")
                    .setMessage("Are you sure you want to import the settings? This will overwrite your current settings.")
                    .setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean result = new Prefs(MainActivity.this).importDefault();
                                    Toast toast = Toast.makeText(MainActivity.this, result ? "Successfully imported settings" : "Import failed", Toast.LENGTH_SHORT);
                                    toast.show();
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
