package com.gfan.sbbs.task;

import java.util.Observable;
import java.util.Observer;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gfan.sbbs.othercomponent.MyApplication;
import com.gfan.sbbs.task2.TaskResult;

public abstract class GenericTask extends
		AsyncTask<String, Object, TaskResult> implements Observer {
	private static final String TAG = "TaskManager";

	private TaskListener mListener = null;
	private Feedback mFeedback = null;
	private boolean isCancelable = true;

	abstract protected TaskResult _doInBackground(String... params);

	public void setListener(TaskListener taskListener) {
		mListener = taskListener;
	}

	public TaskListener getListener() {
		return mListener;
	}

	public void doPublishProgress(Object... values) {
		super.publishProgress(values);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		if (mListener != null) {
			mListener.onCancelled(this);
		}
		Log.d(TAG, "Task being canceled");
		Log.d(TAG, mListener.getName() + " has been Cancelled.");
		Toast.makeText(MyApplication.mContext, mListener.getName()
				+ " has been cancelled", Toast.LENGTH_SHORT);
	}

	@Override
	protected void onPostExecute(TaskResult result) {
		super.onPostExecute(result);

		if (mListener != null) {
			mListener.onPostExecute(this, result);
		}

		if (mFeedback != null) {
			mFeedback.success("");
		}

		/*
		 * Toast.makeText(TwitterApplication.mContext, mListener.getName() +
		 * " completed", Toast.LENGTH_SHORT);
		 */
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (mListener != null) {
			mListener.onPreExecute(this);
		}

		if (mFeedback != null) {
			mFeedback.start("");
		}
	}

	@Override
	protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);

		if (mListener != null) {
			if (values != null && values.length > 0) {
				mListener.onProgressUpdate(this, values[0]);
			}
		}

		if (mFeedback != null) {
			mFeedback.update(values[0]);
		}
	}

	@Override
	protected TaskResult doInBackground(String... params) {
		TaskResult result = _doInBackground(params);
		if (mFeedback != null) {
			mFeedback.update(99);
		}
		return result;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (TaskManager.CANCEL_ALL == (Integer) arg && isCancelable) {
			if (getStatus() == GenericTask.Status.RUNNING) {
				cancel(true);
			}
		}
	}

	public void setCancelable(boolean flag) {
		isCancelable = flag;
	}

	public void setFeedback(Feedback feedback) {
		mFeedback = feedback;
	}
}
