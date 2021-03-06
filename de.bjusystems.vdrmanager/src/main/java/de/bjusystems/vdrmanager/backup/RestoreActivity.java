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



import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import de.bjusystems.vdrmanager.R;
import de.bjusystems.vdrmanager.gui.PreferencesActivity;

/**
 * An activity to restore data from the SD card.
 * 
 * @author Jimmy Shih
 */
public class RestoreActivity extends Activity {

	public static final String EXTRA_DATE = "date";
	public static final String EXTRA_FILE = "file";
  
  private static final String TAG = RestoreActivity.class.getSimpleName();
  private static final int DIALOG_PROGRESS_ID = 0;
  
  private RestoreAsyncTask restoreAsyncTask;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Object retained = getLastNonConfigurationInstance();
    if (retained instanceof RestoreAsyncTask) {
      restoreAsyncTask = (RestoreAsyncTask) retained;
      restoreAsyncTask.setActivity(this);
    } else {
    
      long date = -1L;
      String file = getIntent().getStringExtra(EXTRA_FILE);
      if(file != null){
    	  restoreAsyncTask = new RestoreAsyncTask(this, file);
      } else if( (date = getIntent().getLongExtra(EXTRA_DATE, -1L)) != -1L) {
          restoreAsyncTask = new RestoreAsyncTask(this, new Date(date));
      } else {
          Log.d(TAG, "Invalid date or file");
          finish();
          return;
      }
      restoreAsyncTask.execute();
    }
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    restoreAsyncTask.setActivity(null);
    return restoreAsyncTask;
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    if (id != DIALOG_PROGRESS_ID) {
      return null;
    }
    return DialogUtils.createSpinnerProgressDialog(this,
        R.string.settings_backup_restore_progress_message, new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface dialog) {
            finish();
          }
        });
  }

  /**
   * Invokes when the associated AsyncTask completes.
   *
   * @param success true if the AsyncTask is successful
   * @param messageId message id to display to user
   */
  public void onAsyncTaskCompleted(boolean success, int messageId) {
    removeDialog(DIALOG_PROGRESS_ID);
    Toast.makeText(this, messageId, success ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    //Intent intent = IntentUtils.newIntent(this, FixUpJobs.class);
    //intent.putExtra(Extras.AFTER_RESTORE, Boolean.TRUE);
    Intent intent = IntentUtils.newIntent(this, PreferencesActivity.class);
    startActivity(intent);
  }

  /**
   * Shows the progress dialog.
   */
  public void showProgressDialog() {
    showDialog(DIALOG_PROGRESS_ID);
  }
}
