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

import java.io.IOException;
import java.util.Date;

import android.os.AsyncTask;
import android.util.Log;
import de.bjusystems.vdrmanager.R;

/**
 * AsyncTask to restore data from the SD card.
 * 
 * @author Jimmy Shih
 */
public class RestoreAsyncTask extends AsyncTask<Void, Integer, Boolean> {

	private static final String TAG = RestoreAsyncTask.class.getSimpleName();

	private RestoreActivity restoreActivity;
	private final Date date;
	private final String path;
	private final ExternalFileBackup externalFileBackup;

	// true if the AsyncTask result is success
	private boolean success;

	// true if the AsyncTask has completed
	private boolean completed;

	// message id to return to the activity
	private int messageId;

	/**
	 * Creates an AsyncTask.
	 * 
	 * @param restoreActivity
	 *            the activity currently associated with this AsyncTask
	 * @param date
	 *            the date to retore from
	 */
	public RestoreAsyncTask(RestoreActivity restoreActivity, Date date) {
		this(restoreActivity, date, null);
	}

	/**
	 * Creates an AsyncTask.
	 * 
	 * @param restoreActivity
	 *            the activity currently associated with this AsyncTask
	 * @param date
	 *            the date to retore from
	 */
	public RestoreAsyncTask(RestoreActivity restoreActivity, String path) {
		this(restoreActivity, null, path);
	}

	/**
	 * Creates an AsyncTask.
	 * 
	 * @param restoreActivity
	 *            the activity currently associated with this AsyncTask
	 * @param date
	 *            the date to retore from
	 */
	public RestoreAsyncTask(RestoreActivity restoreActivity, Date date,
			String path) {
		this.restoreActivity = restoreActivity;
		this.date = date;
		this.path = path;
		this.externalFileBackup = new ExternalFileBackup(restoreActivity);
		success = false;
		completed = false;
		messageId = R.string.sd_card_import_error;
	}

	/**
	 * Sets the current {@link RestoreActivity} associated with this AyncTask.
	 * 
	 * @param activity
	 *            the current {@link RestoreActivity}, can be null
	 */
	public void setActivity(RestoreActivity activity) {
		this.restoreActivity = activity;
		if (completed && restoreActivity != null) {
			restoreActivity.onAsyncTaskCompleted(success, messageId);
		}
	}

	@Override
	protected void onPreExecute() {
		if (restoreActivity != null) {
			restoreActivity.showProgressDialog();
		}
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			if (path != null) {
				externalFileBackup.restoreFromFile(path);
			} else {
				externalFileBackup.restoreFromDate(date);
			}
			messageId = R.string.sd_card_import_success;
			return true;
		} catch (IOException e) {
			Log.d(TAG, "IO exception", e);
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		success = result;
		completed = true;
		if (restoreActivity != null) {
			restoreActivity.onAsyncTaskCompleted(success, messageId);
		}
	}
}
