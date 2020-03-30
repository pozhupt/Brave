package com.rest.brave.view;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.rest.brave.CheckInternetConnection;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rest.brave.R;
import com.rest.brave.SharedPreference;
import com.rest.brave.Utils;
import com.rest.brave.adapter.ServerListRVAdapter;
import com.rest.brave.databinding.FragmentMainBinding;
import com.rest.brave.interfaces.ChangeServer;
import com.rest.brave.model.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;
import de.blinkt.openvpn.core.VpnStatus;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment implements View.OnClickListener, ChangeServer {

    private Server server;
    private CheckInternetConnection connection;
    private AdView mAdView;

    private OpenVPNThread vpnThread = new OpenVPNThread();
    private OpenVPNService vpnService = new OpenVPNService();
    boolean vpnStart = false;
    private SharedPreference preference;

    private RecyclerView serverListRv;
    private ArrayList<Server> serverLists;
    private ServerListRVAdapter serverListRVAdapter;

    private ChangeServer changeServer;
    private LinearLayout changeserverbtn;
    private int backnum;
    private FragmentMainBinding binding;

    boolean allowint;
/*
    private InterstitialAd mInterstitialAd;
*/

    public void showintsingle()
    {
        try {
            final InterstitialAd mInterstitialAd1;
            mInterstitialAd1 = new InterstitialAd(getActivity());
            mInterstitialAd1.setAdUnitId("ca-app-pub-2038038715841690/3291132035");
            mInterstitialAd1.loadAd(new AdRequest.Builder().build());

            ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(myProcess);
            boolean isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;

            if(!isInBackground && mInterstitialAd1.isLoaded()) {
                if(allowint) mInterstitialAd1.show();
            }
        }
        catch(Exception E)
        {
        }
    }
    public void showint()
    {
        final InterstitialAd mInterstitialAd1;
        mInterstitialAd1 = new InterstitialAd(getActivity());
        mInterstitialAd1.setAdUnitId("ca-app-pub-2038038715841690/3291132035");
        mInterstitialAd1.loadAd(new AdRequest.Builder().build());

        mInterstitialAd1.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
                ActivityManager.getMyMemoryState(myProcess);
                boolean isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                if (!isInBackground) {
                    mInterstitialAd1.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        allowint=true;
        backnum=0;
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        View view = binding.getRoot();
        initializeAll();

        //init interstitial ads
  /*      mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(this.getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
*/

        //banner ads
/*
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
*/

        MobileAds.initialize(getActivity(), "ca-app-pub-2038038715841690~4712310327");


        //binding.vpnBtn.setOnClickListener(this);
        binding.vpnImgBtn.setOnClickListener(this);
        binding.changebtn.setOnClickListener(this);
/*        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }*/


//You need to add the following line for this solution to work; thanks skayred
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    if(binding.rvs.getVisibility()==View.VISIBLE)
                    {
                        binding.rvs.setVisibility(View.GONE);
                        binding.vpnImgBtn.setVisibility(View.VISIBLE);
                        binding.changebtn.setVisibility(View.VISIBLE);
                        binding.statbar.setVisibility(View.VISIBLE);
                        binding.selectedServerIcon.setVisibility(View.VISIBLE);
                        binding.selectdServerName.setVisibility(View.VISIBLE);
                        binding.vpnBtn.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        if(backnum==0)
                        {
                         backnum=1;
                        }
                        else {
                            getActivity().finish();
                            allowint=false;
                        }
                    }

                    return true;
                }
                return false;
            }
        } );


        return view;
    }

    /**
     * Initialize all variable and object
     */
    private void initializeAll() {
        preference = new SharedPreference(getContext());
        server = preference.getServer();

        // Update current selected server icon and name
        updateCurrentServerIcon(server.getFlagUrl());
        updateCurrentServerName(server.getCountry());

        connection = new CheckInternetConnection();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));

        binding.rvs.setHasFixedSize(true);

        binding.rvs.setLayoutManager(new LinearLayoutManager(getContext()));

        serverLists = getServerList();
        if (serverLists != null) {
            serverListRVAdapter = new ServerListRVAdapter(serverLists, getContext());
            binding.rvs.setAdapter(serverListRVAdapter);
        }


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Checking is vpn already running or not
        try {
            isServiceRunning();
            VpnStatus.initLogCache(getActivity().getCacheDir());
        }
        catch (Exception E)
        {

        }
    }

    /**
     * @param v: click listener view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vpnBtn:
                prepareVpn();
                return;
            case R.id.vpnImgBtn:
                prepareVpn();
                return;
            case R.id.changebtn:
                binding.rvs.setVisibility(View.VISIBLE);
                binding.vpnImgBtn.setVisibility(View.GONE);
                binding.changebtn.setVisibility(View.GONE);
                binding.statbar.setVisibility(View.GONE);
                binding.selectedServerIcon.setVisibility(View.GONE);
                binding.selectdServerName.setVisibility(View.GONE);
                binding.vpnBtn.setVisibility(View.GONE);
                Log.e("-------->", "onClick: ");
                return;
        }
    }

    /**
     * Prepare for vpn connect with required permission
     */
    private void prepareVpn() {
        status("connecting");
        if (!vpnStart) {
            if (getInternetStatus()) {

                // Checking permission for network monitor
                Intent intent = VpnService.prepare(getContext());

                if (intent != null) {
                    startActivityForResult(intent, 1);
                } else startVpn();//have already permission


                // Update confection status
                status("connecting");
                showint();
                Animation animation = new AlphaAnimation((float) 1.0, (float) 0.65); // Change alpha from fully visible to invisible
                animation.setDuration(800); // duration - half a second
                animation.setInterpolator(new LinearInterpolator()); // do not alter
                // animation
                // rate
                animation.setRepeatCount(Animation.INFINITE); // Repeat animation
                // infinitely
                animation.setRepeatMode(Animation.REVERSE);

                binding.vpnImgBtn.startAnimation(animation);

            } else {

                // No internet connection available
                showToast("you have no internet connection !!");
            }

        } else if (stopVpn()) {

            showint();
            // VPN is stopped, show a Toast message.
            showToast("Disconnected Successfully");
        }
    }

    /**
     * Stop vpn
     * @return boolean: VPN status
     */
    public boolean stopVpn() {
        try {
            vpnThread.stop();

            status("Disconnected");
            vpnStart = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Taking permission for network access
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            //Permission granted, start the VPN
            startVpn();
        } else {
            showToast("Permission Deny !! ");
        }
    }

    /**
     * Internet connection status.
     */
    public boolean getInternetStatus() {
        return connection.netCheck(getContext());
    }

    /**
     * Get service status
     */
    public void isServiceRunning() {
        setStatus(vpnService.getStatus());
    }

    /**
     * Start the VPN
     */
    private void startVpn() {
        try {
            // .ovpn file
            InputStream conf = getActivity().getAssets().open(server.getOvpn());
            InputStreamReader isr = new InputStreamReader(conf);
            BufferedReader br = new BufferedReader(isr);
            String config = "";
            String line;

            while (true) {
                line = br.readLine();
                if (line == null) break;
                config += line + "\n";
            }

            br.readLine();
            OpenVpnApi.startVpn(getContext(), config, server.getOvpnUserName(), server.getOvpnUserPassword());

            // Update log
            binding.logTv.setText("Connecting...");

        } catch (IOException | RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Status change with corresponding vpn connection status
     * @param connectionState
     */
    public void setStatus(String connectionState) {
        switch (connectionState) {
            case "DISCONNECTED":
                status("DISCONNECTED");
                vpnStart = false;
                vpnService.setDefaultStatus();
                binding.logTv.setText("");
                binding.vpnBtn.setText("DISCONNECTED");
                binding.vpnImgBtn.setBackgroundResource(R.drawable.ic_connect);
                break;
            case "CONNECTED":
                showint();
                vpnStart = true;// it will use after restart this activity
                status("connected");
                binding.logTv.setText("");
                binding.vpnImgBtn.getAnimation().cancel();
                break;
            case "WAIT":
                binding.logTv.setText("waiting for server connection!!");
                break;
            case "AUTH":
                binding.logTv.setText("server authenticating!!");
                break;
            case "RECONNECTING":
                binding.logTv.setText("Reconnecting...");
                break;
            case "NONETWORK":
                binding.logTv.setText("No network connection");
                break;
        }

    }

    /**
     * Change button background color and text
     * @param status: VPN current status
     */
    public void status(String status) {

        if (status.equals("connect")) {
            binding.vpnBtn.setText(getString(R.string.connect));
        } else if (status.equals("connecting")) {
            binding.vpnBtn.setText(getString(R.string.connecting));
        } else if (status.equals("connected")) {

            binding.vpnBtn.setText("CONNECTED");
            binding.vpnImgBtn.setBackgroundResource(R.drawable.ic_connected);

        } else if (status.equals("tryDifferentServer")) {

            binding.vpnBtn.setBackgroundResource(R.drawable.button_connected);
            binding.vpnBtn.setText("Try Different\nServer");
        } else if (status.equals("loading")) {
            binding.vpnBtn.setBackgroundResource(R.drawable.button);
            binding.vpnBtn.setText("Loading Server..");
        } else if (status.equals("invalidDevice")) {
            binding.vpnBtn.setBackgroundResource(R.drawable.button_connected);
            binding.vpnBtn.setText("Invalid Device");
        } else if (status.equals("authenticationCheck")) {
            binding.vpnBtn.setBackgroundResource(R.drawable.button_connecting);
            binding.vpnBtn.setText("Authentication \n Checking...");
        }

    }

    /**
     * Receive broadcast message
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                setStatus(intent.getStringExtra("state"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                String duration = intent.getStringExtra("duration");
                String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
                String byteIn = intent.getStringExtra("byteIn");
                String byteOut = intent.getStringExtra("byteOut");

                if (duration.equals(null)) duration = "00:00:00";
                if (lastPacketReceive.equals(null)) lastPacketReceive = "0";
                if (byteIn.equals(null)) byteIn = " ";
                if (byteOut.equals(null)) byteOut = " ";
                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * Update status UI
     * @param duration: running time
     * @param lastPacketReceive: last packet receive time
     * @param byteIn: incoming data
     * @param byteOut: outgoing data
     */
    public void updateConnectionStatus(String duration, String lastPacketReceive, String byteIn, String byteOut) {
        binding.durationTv.setText("Duration: " + duration);
        binding.lastPacketReceiveTv.setText("Packet Received: " + lastPacketReceive + " second ago");
/*        binding.byteInTv.setText("Bytes In: " + byteIn);
        binding.byteOutTv.setText("Bytes Out: " + byteOut);*/
        binding.byteInTv.setText( byteIn.split("-")[1]);
        binding.byteOutTv.setText( byteOut.split("-")[1]);
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
     * Show toast message
     * @param message: toast message
     */
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * VPN server country icon change
     * @param serverIcon: icon URL
     */
    public void updateCurrentServerIcon(String serverIcon) {
        Glide.with(getContext())
                .load(serverIcon)
                .into(binding.selectedServerIcon);
    }


    /**
     * VPN server country icon change
     * @param servername: icon URL
     */
    public void updateCurrentServerName(String servername) {
        binding.selectdServerName.setText(servername);
    }


    /**
     * Change server when user select new server
     * @param server ovpn server details
     */
    @Override
    public void newServer(Server server) {
        this.server = server;
        updateCurrentServerIcon(server.getFlagUrl());
        updateCurrentServerName(server.getCountry());
        binding.rvs.setVisibility(View.GONE);
        binding.vpnImgBtn.setVisibility(View.VISIBLE);
        binding.changebtn.setVisibility(View.VISIBLE);
        binding.statbar.setVisibility(View.VISIBLE);
        binding.selectedServerIcon.setVisibility(View.VISIBLE);
        binding.selectdServerName.setVisibility(View.VISIBLE);
        binding.vpnBtn.setVisibility(View.VISIBLE);
        // Stop previous connection
        if (vpnStart) {
            stopVpn();
        }
        prepareVpn();
        binding.vpnBtn.setText("CONNECTING");
    }

    @Override
    public void onResume() {
        if (server == null) {
            server = preference.getServer();
        }
        super.onResume();
    }

    /**
     * Save current selected server on local shared preference
     */
    @Override
    public void onStop() {
        if (server != null) {
            preference.saveServer(server);
        }

        super.onStop();
    }
}
