package net.osmand.plus.mapmarkers;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import net.osmand.plus.MapMarkersHelper;
import net.osmand.plus.MapMarkersHelper.MarkersSyncGroup;
import net.osmand.plus.R;
import net.osmand.plus.base.MenuBottomSheetDialogFragment;
import net.osmand.plus.mapmarkers.adapters.GroupsAdapter;

public abstract class AddGroupBottomSheetDialogFragment extends MenuBottomSheetDialogFragment {

	public static final String TAG = "AddGroupBottomSheetDialogFragment";

	private AddGroupListener listener;
	protected View mainView;
	protected GroupsAdapter adapter;
	protected MapMarkersHelper mapMarkersHelper;

	public void setListener(AddGroupListener listener) {
		this.listener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapMarkersHelper = getMyApplication().getMapMarkersHelper();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final int themeRes = nightMode ? R.style.OsmandDarkTheme : R.style.OsmandLightTheme;
		mainView = View.inflate(new ContextThemeWrapper(getContext(), themeRes), R.layout.fragment_marker_add_group_bottom_sheet_dialog, container);

		setupHeightAndBackground(mainView, R.id.groups_recycler_view);

		return mainView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final RecyclerView recyclerView = mainView.findViewById(R.id.groups_recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		createAdapter();
		adapter.setAdapterListener(new GroupsAdapter.GroupsAdapterListener() {
			@Override
			public void onItemClick(View view) {
				int position = recyclerView.getChildAdapterPosition(view);
				if (position == RecyclerView.NO_POSITION) {
					return;
				}
				mainView.findViewById(R.id.groups_recycler_view).setVisibility(View.GONE);
				mainView.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
				MarkersSyncGroup group = createMapMarkersSyncGroup(position);
				mapMarkersHelper.addMarkersSyncGroup(group);
				mapMarkersHelper.syncGroup(group, new MapMarkersHelper.OnGroupSyncedListener() {
					@Override
					public void onSyncDone() {
						if (listener != null) {
							listener.onGroupAdded();
						}
					}
				});
			}
		});
		recyclerView.setAdapter(adapter);

		mainView.findViewById(R.id.close_row).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
	}

	@Override
	public void onDestroyView() {
		Dialog dialog = getDialog();
		if (dialog != null && getRetainInstance()) {
			dialog.setDismissMessage(null);
		}
		super.onDestroyView();
	}

	protected abstract void createAdapter();

	protected abstract MarkersSyncGroup createMapMarkersSyncGroup(int position);

	public interface AddGroupListener {
		void onGroupAdded();
	}
}
