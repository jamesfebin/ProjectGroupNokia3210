package usi.Nokia3210.gathering.gatheringServices.Notifications;

import android.content.Context;
import android.os.AsyncTask;

import usi.Nokia3210.gathering.gatheringServices.ApplicationLogs.ApplicationUsage;
import usi.Nokia3210.gathering.gatheringServices.Notifications.Utils.Log;
import usi.Nokia3210.local.database.tableHandlers.NotificationData;

/**
 * Created by abhinavmerotra
 */

public class NotificationResponseDetector extends AsyncTask<Void, Void, NotificationData>
{

	private final Log log = new Log();
	private final Context context;
	private final NotificationData n_data;

	public NotificationResponseDetector(Context context, NotificationData n_data) {
		this.context = context;
		this.n_data = n_data;
	}


	@Override
	protected NotificationData doInBackground(Void... params) 
	{
		log.v("Performing backgroud task.");
		try 
		{
			Thread.sleep(3000);
		} 
		catch (InterruptedException e) 
		{
			log.e(e.toString());
		}


		ApplicationUsage au = new ApplicationUsage(context);
		String package_name = n_data.getAppPackageName();
		long start_time = n_data.getRemovalTime() - 10000; // subtracted the threshold 10 seconds
		boolean clicked = au.isAppLaunched(package_name, start_time);
		if(clicked)
			n_data.setClicked(1);

		return n_data;
	}

}
