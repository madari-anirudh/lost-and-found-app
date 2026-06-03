package com.example.lostandfound;

import com.example.lostandfound.models.AppVersionModel;
import android.content.Intent;
import android.net.Uri;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.lostandfound.api.ApiService;
import com.example.lostandfound.api.RetrofitClient;
import com.example.lostandfound.fragments.HomeFragment;
import com.example.lostandfound.fragments.MyItemsFragment;
import com.example.lostandfound.fragments.ProfileFragment;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import org.json.JSONObject;
import retrofit2.Callback;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    ChipNavigationBar bottomNav;

    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNav = findViewById(R.id.bottomNav);

        swipeRefresh = findViewById(R.id.swipeRefresh);

        // LOAD HOME
        loadFragment(new HomeFragment());

        // SWIPE REFRESH
        swipeRefresh.setOnRefreshListener(() -> {

            loadFragment(new HomeFragment());

            swipeRefresh.setRefreshing(false);

        });

        // SOCKET CONNECTION
        SocketHandler.setSocket();
        SocketHandler.establishConnection();

        // BOTTOM NAVIGATION
        bottomNav.setOnItemSelectedListener(id -> {

            Fragment fragment = null;

            if (id == R.id.home) {

                fragment = new HomeFragment();

            } else if (id == R.id.myItems) {

                fragment = new MyItemsFragment();

            } else if (id == R.id.profile) {

                fragment = new ProfileFragment();

            } else if (id == R.id.report) {

                startActivity(
                        new Intent(
                                DashboardActivity.this,
                                ReportItemActivity.class
                        )
                );

                return;
            }

            if (fragment != null) {

                loadFragment(fragment);
            }
        });

        // SOCKET.IO
        Socket socket = SocketHandler.getSocket();

        socket.on("itemMatched", args -> {

            runOnUiThread(() -> {

                try {

                    JSONObject data =
                            (JSONObject) args[0];

                    String userId =
                            data.optString("userId");

                    String message =
                            data.optString("message");

                    String myUserId =
                            getSharedPreferences(
                                    "APP",
                                    MODE_PRIVATE
                            ).getString("USER_ID", "");

                    if (userId.equals(myUserId)) {

                        Toast.makeText(
                                DashboardActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
            });
        });
    }

    // AUTO REFRESH WHEN SCREEN RETURNS
    @Override
    protected void onResume() {

        super.onResume();

        loadFragment(new HomeFragment());
    }

    // DISCONNECT SOCKET
    @Override
    protected void onDestroy() {

        super.onDestroy();

        SocketHandler.closeConnection();
    }

    // LOAD FRAGMENT
    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
    private void checkForUpdate() {

        ApiService api =
                RetrofitClient.getInstance()
                        .create(ApiService.class);

        api.getLatestVersion()
                .enqueue(new Callback<AppVersionModel>() {

                    @Override
                    public void onResponse(
                            Call<AppVersionModel> call,
                            Response<AppVersionModel> response) {

                        if (!response.isSuccessful())
                            return;

                        AppVersionModel update =
                                response.body();

                        if (update == null)
                            return;

                        String currentVersion = "1.0.0";

                        if (!currentVersion.equals(
                                update.getVersion())) {

                            showUpdateDialog(update);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<AppVersionModel> call,
                            Throwable t) {
                    }
                });
    }
    private void showUpdateDialog(
            AppVersionModel update) {

        new AlertDialog.Builder(this)
                .setTitle("Update Available 🚀")
                .setMessage(update.getMessage())

                .setPositiveButton(
                        "Download Update",
                        (d, w) -> {

                            Intent intent =
                                    new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(
                                                    update.getApkUrl()
                                            ));

                            startActivity(intent);
                        })

                .setNegativeButton(
                        "Later",
                        null)

                .show();
    }

}