package com.gfan.sbbs.ui.main;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gfan.sbbs.bean.Board;
import com.gfan.sbbs.db.BoardDAO;
import com.gfan.sbbs.http.HttpException;
import com.gfan.sbbs.othercomponent.BBSOperator;
import com.gfan.sbbs.othercomponent.MyApplication;
import com.gfan.sbbs.othercomponent.SBBSConstants;
import com.gfan.sbbs.task.GenericTask;
import com.gfan.sbbs.task.TaskAdapter;
import com.gfan.sbbs.task.TaskListener;
import com.gfan.sbbs.task2.TaskResult;
import com.gfan.sbbs.ui.Abstract.BaseActivity;
import com.gfan.sbbs.ui.Adapter.SectionAdapter;
import com.umeng.analytics.MobclickAgent;

public class Sections extends BaseActivity {

	private boolean forceLoad = false;
	private List<List<Board>> boardList;
	private GenericTask mRetrieveTask;
	private SectionAdapter myAdapter;
	private ExpandableListView boardListView;
	private MyApplication myApplication;
	private String url,errorCause;
	private static final int MAX_SECTION = 12;
	private static final int MENU_REFRESH = 9;
	private TaskListener mRetrieveTaskListener = new TaskAdapter() {
		private ProgressDialog pdialog;

		@Override
		public String getName() {
			return "mRetrieveTaskListener";
		}

		@Override
		public void onPreExecute(GenericTask task) {
			super.onPreExecute(task);
			pdialog = new ProgressDialog(Sections.this);
			pdialog.setMessage(getResources().getString(R.string.loading));
			pdialog.show();
			pdialog.setCanceledOnTouchOutside(false);
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			super.onPostExecute(task, result);
			pdialog.dismiss();
			forceLoad = false;
			if (TaskResult.OK == result) {
				draw();
			} else {
				Toast.makeText(Sections.this, errorCause, Toast.LENGTH_SHORT)
						.show();
			}
		}

	};

	@Override
	protected void _onCreate(Bundle savedInstanceState) {
		super._onCreate(savedInstanceState);
		this.setContentView(R.layout.sections);
		myApplication = (MyApplication) getApplication();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initArgs();
		initEvents();
		doRetrieve();
	}

	private void initArgs() {
		url = SBBSConstants.BOARD_SECTIONS;
		if (isLogined) {
			url = url.concat("?token=" + token);
		}
		boardListView = (ExpandableListView) this.findViewById(R.id.sections);
		myAdapter = new SectionAdapter(this);
		boardListView.setAdapter(myAdapter);
	}

	private void initEvents() {
		boardListView.setCacheColorHint(Color.TRANSPARENT);
		boardListView
				.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

					@Override
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) {
						Board board = boardList.get(groupPosition).get(
								childPosition);
						int mode = 0;
						if (myApplication.isOne_topic()) {
							mode = 2;
						}
						Intent intent = new Intent(Sections.this,
								TopicList.class);
						intent.putExtra("boardID", board.getId());
						intent.putExtra("mode", mode);
						startActivity(intent);
						return false;
					}
				});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case MENU_REFRESH:
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle(R.string.alert_title);
			ab.setMessage(R.string.section_reload_tip);
			ab.setPositiveButton(R.string.alert_postitive, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					forceLoad = true;
					doRetrieve();
				}
			});
			ab.setNegativeButton(R.string.alert_negative, null);
			ab.create();
			ab.show();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_REFRESH, Menu.NONE, "refresh")
				.setIcon(R.drawable.ic_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return super.onCreateOptionsMenu(menu);
	}

	private void draw() {
		if (boardList != null) {
			myAdapter.refresh(boardList);
		}
	}

	@Override
	protected void onDestroy() {
		if (null != mRetrieveTask
				&& mRetrieveTask.getStatus() == GenericTask.Status.RUNNING) {
			mRetrieveTask.cancel(true);
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	private void doRetrieve() {
		mRetrieveTask = new DoRetrieveTask();
		mRetrieveTask.setListener(mRetrieveTaskListener);
		mRetrieveTask.execute(url);
	}

	/**
	 * 
	 * @author Nine
	 * 
	 */
	private class DoRetrieveTask extends GenericTask {

		@Override
		protected TaskResult _doInBackground(String... params) {
			BoardDAO boardDAO = new BoardDAO(MyApplication.mContext);
			List<List<Board>> list = boardDAO.fetchAllBoard(MAX_SECTION);
			if (list.size() != 0 && !forceLoad) {
				boardList = list;
			} else {
//					boardList = SBBSSupport.getAllBoard(params[0]);
				try {
					boardList = BBSOperator.getInstance().getAllBoards(params[0]);
				} catch (HttpException e) {
					e.printStackTrace();
					errorCause = e.getMessage();
					return TaskResult.Failed;
				}
				boardDAO.deleteAllList(MAX_SECTION);
				boardDAO.insertAllBoard(boardList);
			}
			return TaskResult.OK;
		}

	}

	@Override
	protected void processUnLogin() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setup() {
		// TODO Auto-generated method stub

	}
}
