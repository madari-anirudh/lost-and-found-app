package com.example.lostandfound.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.lostandfound.MainActivity;
import com.example.lostandfound.MyItemsActivity;
import com.example.lostandfound.R;

public class ProfileFragment extends Fragment {

    private TextView userName, userEmail;

    // LinearLayouts in XML
    private View myItemsOption, helpOption, shareOption, aboutOption, logoutBtn;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //  Profile Info
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);

        //  Settings Options
        myItemsOption = view.findViewById(R.id.myItemsOption);

        helpOption = view.findViewById(R.id.helpOption);
        shareOption = view.findViewById(R.id.shareOption);
        aboutOption = view.findViewById(R.id.aboutOption);
        logoutBtn = view.findViewById(R.id.logoutBtn);

        // Load user data
        SharedPreferences prefs = getActivity().getSharedPreferences("APP", 0);
        String name = prefs.getString("NAME", "User");
        String email = prefs.getString("EMAIL", "email@gmail.com");

        userName.setText(name);
        userEmail.setText(email);

        //  APPLY ANIMATION TO ALL ITEMS

        // My Items
        applyClickAnimation(myItemsOption, () -> {
            startActivity(new Intent(getActivity(), MyItemsActivity.class));
        });



        //  Help (Email)
        applyClickAnimation(helpOption, () -> {

            new androidx.appcompat.app.AlertDialog.Builder(getContext())
                    .setTitle("Contact Support")
                    .setMessage("Do you want to send an email to support?")
                    .setPositiveButton("Continue", (dialog, which) -> {

                        try {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:lostandfoundtmu2026@gmail.com"));
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Help - Lost & Found App");

                            startActivity(intent);

                        } catch (Exception e) {
                            Toast.makeText(getContext(),
                                    "No email app found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        //  Share
        applyClickAnimation(shareOption, () -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Check out this Lost & Found App 📱");
            startActivity(Intent.createChooser(intent, "Share via"));
        });

        // About
        applyClickAnimation(aboutOption, () -> {

            View dialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_about, null);

            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .setPositiveButton("OK", null)
                    .create();

            dialog.show();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
            }
        });

        //  Logout
        applyClickAnimation(logoutBtn, this::showLogoutDialog);

        return view;
    }

    //  Animation Method (Reusable)
    private void applyClickAnimation(View view, Runnable action) {

        view.setOnClickListener(v -> {

            // Haptic feedback
            v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

            // Press animation
            v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {

                        // Bounce back
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();

                        // Execute action
                        if (action != null) action.run();
                    })
                    .start();
        });
    }

    //  Logout Dialog
    private void showLogoutDialog() {

        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    getActivity()
                            .getSharedPreferences("APP", 0)
                            .edit()
                            .clear()
                            .apply();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);

                    getActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}