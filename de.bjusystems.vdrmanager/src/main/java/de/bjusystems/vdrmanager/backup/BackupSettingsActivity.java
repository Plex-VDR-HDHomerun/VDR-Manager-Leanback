/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package de.bjusystems.vdrmanager.backup;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import de.bjusystems.vdrmanager.R;
import de.bjusystems.vdrmanager.gui.Utils;

/**
 * An activity for accessing the backup settings.
 *
 * @author Jimmy Shih
 */
public class BackupSettingsActivity extends AbstractSettingsActivity {

    private static final int DIALOG_CONFIRM_RESTORE_ID = 0;


    /**
     * The Backup preference.
     */
    Preference backupPreference;
    /**
     * The Restore preference.
     */
    Preference restorePreference;

    /*
     * Note that sharedPreferenceChangeListenr cannot be an anonymous inner class.
     * Anonymous inner class will get garbage collected.
     */
    private final OnSharedPreferenceChangeListener
            sharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
            // Note that key can be null
            //if (PreferencesUtils.getKey(BackupSettingsActivity.this, R.string.recording_track_id_key)
            //  .equals(key)) {
            //updateUi();
            //}
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        final boolean permissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        addPreferencesFromResource(R.xml.backup_settings);
        backupPreference = findPreference(getString(R.string.settings_backup_now_key));
        backupPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (permissionGranted == false) {
                    Utils.say(getApplication(), R.string.permission_rationale, Toast.LENGTH_LONG);
                    return false;
                }
                Intent intent = IntentUtils.newIntent(BackupSettingsActivity.this, BackupActivity.class);
                startActivity(intent);
                return true;
            }
        });
        restorePreference = findPreference(getString(R.string.settings_backup_restore_key));
        restorePreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (permissionGranted == false) {
                    Utils.say(getApplication(), R.string.permission_rationale, Toast.LENGTH_LONG);
                    return false;
                }
                showDialog(DIALOG_CONFIRM_RESTORE_ID);
                return true;
            }
        });


    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id != DIALOG_CONFIRM_RESTORE_ID) {
            return null;
        }
        return DialogUtils.createConfirmationDialog(this,
                R.string.settings_backup_restore_confirm_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = IntentUtils.newIntent(
                                BackupSettingsActivity.this, RestoreChooserActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateUi();
    }

//  /**
//   * Updates the UI based on the recording state.
//   */
//  private void updateUi() {
//    boolean isRecording = PreferencesUtils.getLong(this, R.string.recording_track_id_key)
//        != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;
//    backupPreference.setEnabled(!isRecording);
//    restorePreference.setEnabled(!isRecording);
//    backupPreference.setSummary(isRecording ? R.string.settings_not_while_recording
//        : R.string.settings_backup_now_summary);
//    restorePreference.setSummary(isRecording ? R.string.settings_not_while_recording
//        : R.string.settings_backup_restore_summary);
//  }
}
