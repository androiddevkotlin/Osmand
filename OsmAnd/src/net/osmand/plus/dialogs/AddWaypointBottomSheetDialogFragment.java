package net.osmand.plus.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.osmand.data.LatLon;
import net.osmand.data.PointDescription;
import net.osmand.plus.R;
import net.osmand.plus.TargetPointsHelper;
import net.osmand.plus.TargetPointsHelper.TargetPoint;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.base.MenuBottomSheetDialogFragment;
import net.osmand.plus.base.bottomsheetmenu.BaseBottomSheetItem;
import net.osmand.plus.base.bottomsheetmenu.BottomSheetItemWithDescription;
import net.osmand.plus.base.bottomsheetmenu.DividerHalfBottomSheetItem;
import net.osmand.plus.base.bottomsheetmenu.SimpleBottomSheetItem;

public class AddWaypointBottomSheetDialogFragment extends MenuBottomSheetDialogFragment {

	public static final String TAG = "AddWaypointBottomSheetDialogFragment";
	public static final String LAT_KEY = "latitude";
	public static final String LON_KEY = "longitude";
	public static final String POINT_DESCRIPTION_KEY = "point_description";

	@Override
	public View createMenuItems(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = getArguments();
		final LatLon latLon = new LatLon(args.getDouble(LAT_KEY), args.getDouble(LON_KEY));
		final PointDescription name = PointDescription.deserializeFromString(args.getString(POINT_DESCRIPTION_KEY), latLon);
		final TargetPointsHelper targetPointsHelper = getMyApplication().getTargetPointsHelper();

		BaseBottomSheetItem titleItem = new SimpleBottomSheetItem.Builder()
				.setTitle(getString(R.string.new_destination_point_dialog))
				.setLayoutId(R.layout.bottom_sheet_item_title)
				.create();
		items.add(titleItem);

		BaseBottomSheetItem replaceDestItem = new BottomSheetItemWithDescription.Builder()
				.setDescription(getCurrentPointName(targetPointsHelper.getPointToNavigate(), false))
				.setIcon(getIcon(R.drawable.list_destination, 0))
				.setTitle(getString(R.string.replace_destination_point))
				.setLayoutId(R.layout.bottom_sheet_item_with_descr_56dp)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						targetPointsHelper.navigateToPoint(latLon, true, -1, name);
						dismiss();
					}
				})
				.create();
		items.add(replaceDestItem);

		BaseBottomSheetItem replaceStartItem = new BottomSheetItemWithDescription.Builder()
				.setDescription(getCurrentPointName(targetPointsHelper.getPointToStart(), true))
				.setIcon(getIcon(R.drawable.list_startpoint, 0))
				.setTitle(getString(R.string.make_as_start_point))
				.setLayoutId(R.layout.bottom_sheet_item_with_descr_56dp)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						TargetPoint start = targetPointsHelper.getPointToStart();
						if (start != null) {
							targetPointsHelper.navigateToPoint(new LatLon(start.getLatitude(), start.getLongitude()),
									false, 0, start.getOriginalPointDescription());
						}
						targetPointsHelper.setStartPoint(latLon, true, name);
						dismiss();
					}
				})
				.create();
		items.add(replaceStartItem);

		items.add(new DividerHalfBottomSheetItem(getContext(), getCloseRowDividerColorId()));

		BaseBottomSheetItem subsequentDestItem = new BottomSheetItemWithDescription.Builder()
				.setDescription(getString(R.string.subsequent_dest_description))
				.setIcon(getSubsequentDestIcon())
				.setTitle(getString(R.string.keep_and_add_destination_point))
				.setLayoutId(R.layout.bottom_sheet_item_with_descr_56dp)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						targetPointsHelper.navigateToPoint(latLon, true,
								targetPointsHelper.getIntermediatePoints().size() + 1, name);
						dismiss();
					}
				})
				.create();
		items.add(subsequentDestItem);

		BaseBottomSheetItem firstIntermItem = new BottomSheetItemWithDescription.Builder()
				.setDescription(getString(R.string.first_intermediate_dest_description))
				.setIcon(getFirstIntermDestIcon())
				.setTitle(getString(R.string.add_as_first_destination_point))
				.setLayoutId(R.layout.bottom_sheet_item_with_descr_56dp)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						targetPointsHelper.navigateToPoint(latLon, true, 0, name);
						dismiss();
					}
				})
				.create();
		items.add(firstIntermItem);

		BaseBottomSheetItem lastIntermItem = new BottomSheetItemWithDescription.Builder()
				.setDescription(getString(R.string.last_intermediate_dest_description))
				.setIcon(getLastIntermDistIcon())
				.setTitle(getString(R.string.add_as_last_destination_point))
				.setLayoutId(R.layout.bottom_sheet_item_with_descr_56dp)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						targetPointsHelper.navigateToPoint(latLon, true,
								targetPointsHelper.getIntermediatePoints().size(), name);
						dismiss();
					}
				})
				.create();
		items.add(lastIntermItem);

		return null;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		closeContextMenu();
	}

	@Override
	protected int getCloseRowDividerColorId() {
		return nightMode ? R.color.route_info_bottom_view_bg_dark : -1;
	}

	@Override
	protected Drawable getActiveIcon(@DrawableRes int id) {
		return getIcon(id, nightMode ? R.color.ctx_menu_direction_color_dark : R.color.map_widget_blue);
	}

	@Override
	protected int getPortraitBgResId() {
		return nightMode ? R.drawable.bg_additional_menu_dark : R.drawable.bg_bottom_menu_light;
	}

	@Override
	protected int getLandscapeTopsidesBgResId() {
		return nightMode ? R.drawable.bg_additional_menu_topsides_dark : R.drawable.bg_bottom_sheet_topsides_landscape_light;
	}

	@Override
	protected int getLandscapeSidesBgResId() {
		return nightMode ? R.drawable.bg_additional_menu_sides_dark : R.drawable.bg_bottom_sheet_sides_landscape_light;
	}

	private Drawable getBackgroundIcon(@DrawableRes int resId) {
		return getIcon(resId, R.color.searchbar_text_hint_light);
	}

	private LayerDrawable getLayerDrawable(@DrawableRes int bgIdRes, @DrawableRes int icIdRes) {
		return new LayerDrawable(new Drawable[]{getBackgroundIcon(bgIdRes), getActiveIcon(icIdRes)});
	}

	private LayerDrawable getSubsequentDestIcon() {
		return getLayerDrawable(R.drawable.ic_action_route_subsequent_destination,
				R.drawable.ic_action_route_subsequent_destination_point);
	}

	private LayerDrawable getFirstIntermDestIcon() {
		return getLayerDrawable(R.drawable.ic_action_route_first_intermediate,
				R.drawable.ic_action_route_first_intermediate_point);
	}

	private LayerDrawable getLastIntermDistIcon() {
		return getLayerDrawable(R.drawable.ic_action_route_last_intermediate,
				R.drawable.ic_action_route_last_intermediate_point);
	}

	private String getCurrentPointName(@Nullable TargetPoint point, boolean start) {
		Context ctx = getContext();
		StringBuilder builder = new StringBuilder(ctx.getString(R.string.shared_string_current));
		builder.append(": ");
		if (point != null) {
			if (point.getOnlyName().length() > 0) {
				builder.append(point.getOnlyName());
			} else {
				builder.append(ctx.getString(R.string.route_descr_map_location));
				builder.append(" ");
				builder.append(ctx.getString(R.string.route_descr_lat_lon, point.getLatitude(), point.getLongitude()));
			}
		} else if (start) {
			builder.append(ctx.getString(R.string.shared_string_my_location));
		}
		return builder.toString();
	}

	private void closeContextMenu() {
		Activity activity = getActivity();
		if (activity instanceof MapActivity) {
			((MapActivity) activity).getContextMenu().close();
		}
	}
}
