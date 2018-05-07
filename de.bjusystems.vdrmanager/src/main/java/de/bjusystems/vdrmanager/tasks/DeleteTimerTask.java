package de.bjusystems.vdrmanager.tasks;

import android.app.Activity;
import de.bjusystems.vdrmanager.R;
import de.bjusystems.vdrmanager.data.Timer;
import de.bjusystems.vdrmanager.gui.CertificateProblemDialog;
import de.bjusystems.vdrmanager.utils.svdrp.SetTimerClient;
import de.bjusystems.vdrmanager.utils.svdrp.SetTimerClient.TimerOperation;

public abstract class DeleteTimerTask extends AsyncProgressTask<Timer> {

  public DeleteTimerTask(final Activity activity, final Timer timer) {
    super(activity, new SetTimerClient(timer, TimerOperation.DELETE, new CertificateProblemDialog(activity)) {
      @Override
      public int getProgressTextId() {
        return R.string.progress_timer_delete;
      }
    });
  }
}
