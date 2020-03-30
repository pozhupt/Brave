package com.rest.brave.view;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.rest.brave.R;

import com.rest.brave.adapter.NavListRVAdapter;
import com.rest.brave.adapter.ServerListRVAdapter;
import com.rest.brave.interfaces.ChangeServer;
import com.rest.brave.interfaces.NavItemClickListener;
import com.rest.brave.model.Server;

import java.util.ArrayList;
import java.util.Arrays;

import com.rest.brave.Utils;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;


public class MainActivity extends AppCompatActivity implements NavItemClickListener, RatingDialogListener {
    private FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    private Fragment fragment;
    private RecyclerView serverListRv;
    private ArrayList<Server> serverLists;
    private ArrayList<Server> itemLists;
    private NavListRVAdapter serverListRVAdapter;
    private DrawerLayout drawer;
    private ChangeServer changeServer;

    public static final String TAG = "BraveVPN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize all variable
        initializeAll();

        ImageButton menuRight = findViewById(R.id.navbar_right);
        ImageButton menuLeft = findViewById(R.id.navbar_left);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //init ads
        MobileAds.initialize(this,this.getString(R.string.admob_app_id));

/*
        //init interstitial ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(this.getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
*/




        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        menuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });

        menuLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                Intent i = new Intent(getBaseContext(), About.class);
                startActivity(i);*/

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Use Free and Fast android VPN: https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        transaction.add(R.id.container, fragment);
        transaction.commit();

        // Server List recycler view initialize
        if (itemLists != null) {
            serverListRVAdapter = new NavListRVAdapter(itemLists, this);
            serverListRv.setAdapter(serverListRVAdapter);
        }

    }

    /**
     * Initialize all object, listener etc
     */
    private void initializeAll() {
        drawer = findViewById(R.id.drawer_layout);

        fragment = new MainFragment();
        serverListRv = findViewById(R.id.serverListRv);
        //serverListRv = findViewById(R.id.rvs);
        serverListRv.setHasFixedSize(true);
        serverListRv.setLayoutManager(new LinearLayoutManager(this));

        serverLists = getServerList();
        itemLists = getitemList();
        changeServer = (ChangeServer) fragment;

    }

    /**
     * Close navigation drawer
     */
    public void closeDrawer(){

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    /**
     * Generate server array list
     */
    private ArrayList getitemList() {

        ArrayList<Server> servers = new ArrayList<>();

        servers.add(new Server("Rate Us",
                Utils.getImgURL(R.drawable.star),
                "us.ovpn",
                "vpn",
                "vpn"
        ));

        return servers;
    }

    private ArrayList getServerList() {

        ArrayList<Server> servers = new ArrayList<>();

        servers.add(new Server("Auto",
                Utils.getImgURL(R.drawable.earth),
                "japan.ovpn",
                "vpn",
                "vpn"
        ));
        servers.add(new Server("United States",
                Utils.getImgURL(R.drawable.usa_flag),
                "us.ovpn",
                "vpn",
                "vpn"
        ));
        servers.add(new Server("Japan 1",
                Utils.getImgURL(R.drawable.japan),
                "japan.ovpn",
                "lazycoder",
                "lazycoder"
        ));
        servers.add(new Server("Japan 2",
                Utils.getImgURL(R.drawable.japan),
                "japan2.ovpn",
                "lazycoder",
                "lazycoder"
        ));
        servers.add(new Server("Sweden",
                Utils.getImgURL(R.drawable.sweden),
                "sweden.ovpn",
                "vpn",
                "vpn"
        ));
        servers.add(new Server("Korea 1",
                Utils.getImgURL(R.drawable.korea),
                "korea.ovpn",
                "vpn",
                "vpn"
        ));
        servers.add(new Server("France",
                Utils.getImgURL(R.drawable.fr_flag),
                "korea.ovpn",
                "vpn",
                "vpn"
        ));
        servers.add(new Server("Korea 2",
                Utils.getImgURL(R.drawable.korea),
                "korea.ovpn",
                "vpn",
                "vpn"
        ));
        servers.add(new Server("England",
                Utils.getImgURL(R.drawable.uk_flag),
                "korea.ovpn",
                "vpn",
                "vpn"
        ));
        servers.add(new Server("Japan 3",
                Utils.getImgURL(R.drawable.japan),
                "japan.ovpn",
                "lazycoder",
                "lazycoder"
        ));

        servers.add(new Server("United States 2",
                Utils.getImgURL(R.drawable.usa_flag),
                "us.ovpn",
                "vpn",
                "vpn"
        ));

        return servers;
    }

    /**
     * On navigation item click, close drawer and change server
     * @param index: server index
     */
    @Override
    public void clickedItem(int index) {
        //closeDrawer();
        changeServer.newServer(serverLists.get(index));
    }

    public void showDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNeutralButtonText("Later")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(5)
                .setTitle("Rate this application")
                .setDescription("Please select some stars and give your feedback")
                .setStarColor(R.color.design_default_color_primary_dark)
                .setNoteDescriptionTextColor(R.color.design_default_color_primary_dark)
                .setTitleTextColor(R.color.colorPrimaryDark)
                .setDescriptionTextColor(R.color.colorPrimaryDark)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.colorPrimaryDark)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .setCommentInputEnabled(false)
                .create(MainActivity.this)
                // only if listener is implemented by fragment
                .show();
    }

    @Override
    public void onNegativeButtonClicked() {
    }

    @Override
    public void onNeutralButtonClicked() {
    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {

        if(i>3) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        }
        else
        {
            Toast.makeText(this, "Thanks for rating.", Toast.LENGTH_SHORT).show();
        }
    }
}
