package ir.nikatech.persiancalendar.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import ir.nikatech.persiancalendar.Constants;
import ir.nikatech.persiancalendar.R;
import ir.nikatech.persiancalendar.util.Utils;
import ir.nikatech.persiancalendar.view.preferences.AthanNumericDialog;
import ir.nikatech.persiancalendar.view.preferences.AthanNumericPreference;
import ir.nikatech.persiancalendar.view.preferences.AthanVolumeDialog;
import ir.nikatech.persiancalendar.view.preferences.AthanVolumePreference;
import ir.nikatech.persiancalendar.view.preferences.GPSLocationDialog;
import ir.nikatech.persiancalendar.view.preferences.GPSLocationPreference;
import ir.nikatech.persiancalendar.view.preferences.LocationPreference;
import ir.nikatech.persiancalendar.view.preferences.LocationPreferenceDialog;
import ir.nikatech.persiancalendar.view.preferences.PrayerSelectDialog;
import ir.nikatech.persiancalendar.view.preferences.PrayerSelectPreference;
import ir.nikatech.persiancalendar.view.preferences.ShapedListDialog;
import ir.nikatech.persiancalendar.view.preferences.ShapedListPreference;

public class ApplicationPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Preference categoryAthan;
    private Utils utils;

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        utils = Utils.getInstance(getContext());
        utils.setActivityTitleAndSubtitle(getActivity(), getString(R.string.settings), "");

        addPreferencesFromResource(R.xml.preferences);

        categoryAthan = findPreference(Constants.PREF_KEY_ATHAN);
        updateAthanPreferencesState();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(preferenceUpdateReceiver, new IntentFilter(Constants.LOCAL_INTENT_UPDATE_PREFERENCE));
    }

    private BroadcastReceiver preferenceUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateAthanPreferencesState();
        }
    };

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(preferenceUpdateReceiver);
        super.onDestroyView();
    }

    public void updateAthanPreferencesState() {
        boolean locationEmpty = utils.getCoordinate() == null;
        categoryAthan.setEnabled(!locationEmpty);
        if (locationEmpty) {
            categoryAthan.setSummary(R.string.athan_disabled_summary);
        } else {
            categoryAthan.setSummary("");
        }
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment fragment = null;
        if (preference instanceof PrayerSelectPreference) {
            fragment = new PrayerSelectDialog();
        } else if (preference instanceof AthanVolumePreference) {
            fragment = new AthanVolumeDialog();
        } else if (preference instanceof LocationPreference) {
            fragment = new LocationPreferenceDialog();
        } else if (preference instanceof AthanNumericPreference) {
            fragment = new AthanNumericDialog();
        } else if (preference instanceof GPSLocationPreference) {
            fragment = new GPSLocationDialog();
        } else if (preference instanceof ShapedListPreference) {
            fragment = new ShapedListDialog();
        } else {
            super.onDisplayPreferenceDialog(preference);
        }

        if (fragment != null) {
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            fragment.setArguments(bundle);
            fragment.setTargetFragment(this, 0);
            fragment.show(getFragmentManager(), fragment.getClass().getName());
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        updateAthanPreferencesState();
    }
}
