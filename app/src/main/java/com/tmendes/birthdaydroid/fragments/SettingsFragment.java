/*
 * Copyright (C) 2015-2016 The Food Restriction Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tmendes.birthdaydroid.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tmendes.birthdaydroid.MainActivity;
import com.tmendes.birthdaydroid.R;
import com.tmendes.birthdaydroid.receivers.AlarmReceiver;

import java.util.Calendar;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings,
                container, false);

        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.setting_frame, new PrefFragment())
                .commit();

        DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return v;
    }

    public static class PrefFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

            boolean dailyNotification = prefs.getBoolean("daily_notifications", false);

            Calendar defaultToRingAt = Calendar.getInstance();
            defaultToRingAt.set(Calendar.HOUR_OF_DAY, MainActivity.DEFAULT_ALARM_TIME);
            defaultToRingAt.set(Calendar.MINUTE, 0);
            defaultToRingAt.set(Calendar.SECOND, 0);

            long toRingAt = prefs.getLong("scan_daily_interval",
                    defaultToRingAt.getTimeInMillis());

            if (dailyNotification) {
                Toast.makeText(getContext(), "ettings daily on!", Toast.LENGTH_SHORT).show();//Do what you want when the broadcast is received...
                Log.i("birthday: ", "settings daily on!");
                new AlarmReceiver().setAlarm(getContext(), toRingAt);
            } else {
                Toast.makeText(getContext(), "ettings daily off!", Toast.LENGTH_SHORT).show();//Do what you want when the broadcast is received...
                Log.i("birthday: ", "settings daily off!");
                new AlarmReceiver().cancelAlarm(getContext());
            }
        }
    }
}
