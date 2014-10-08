package me.strlght.campanion.app.adapter;

import android.content.Context;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import me.strlght.campanion.app.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by StarLight on 10/5/14.
 */
public class ImageArrayAdapter extends BaseAdapter {

	public static final String TAG = "ImageArrayAdapter";

	private Context mContext;
	private List<String> mImages;
	private List<Boolean> mSelected;
	private String mPath = null;
	private FileObserver mObserver;

	public ImageArrayAdapter(Context context, String path) {
		mContext = context;
		mPath = path;
		mSelected = new ArrayList<Boolean>();

		int flags = FileObserver.CREATE | FileObserver.DELETE | FileObserver.DELETE_SELF
				| FileObserver.MODIFY | FileObserver.MOVE_SELF | FileObserver.MOVED_FROM
				| FileObserver.MOVED_FROM;
		mObserver = new ImageObserver(path, flags);
		mObserver.onEvent(0, null);
		mObserver.startWatching();
	}

	public void clearSelection() {
		mSelected.clear();
		for (int i = 0; i < mImages.size(); i++) {
			mSelected.add(false);
		}
	}

	public boolean isAnyChosen() {
		for (int i = 0; i < mImages.size(); i++) {
			if (mSelected.get(i)) {
				return true;
			}
		}
		return false;
	}

	public void setSelected(int i, boolean selected) {
		mSelected.set(i, selected);
	}

	public boolean isSelected(int i) {
		return mSelected.get(i);
	}

	public List<String> getSelected() {
		List<String> images = new ArrayList<String>();
		for (int i = 0; i < mImages.size(); i++) {
			if (mSelected.get(i)) {
				images.add(mImages.get(i));
			}
		}
		return images;
	}

	@Override
	public int getCount() {
		if (mImages != null) {
			return mImages.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int i) {
		if (mImages != null) {
			return mImages.get(i);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int i) {
		if (mImages != null) {
			return mImages.get(i).hashCode();
		} else {
			return 0;
		}
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		if (mImages == null) {
			return null;
		}

		final String image = mImages.get(i);

		View v = view;
		if (v == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(mContext);
			v = layoutInflater.inflate(R.layout.li_gallery, viewGroup, false);
		}
		ImageView imageView = (ImageView) v.findViewById(R.id.image_view);

		boolean isSelected = mSelected.get(i);
		imageView.setSelected(isSelected);

		Picasso.with(mContext)
				.load(new File(image))
				.resize(256, 256)
				.centerInside()
				.into(imageView);

		return v;
	}

	private class ImageObserver extends FileObserver {

		private final Handler mHandler = new Handler(Looper.getMainLooper());

		ImageObserver(String path) {
			super(path);
		}

		ImageObserver(String path, int flags) {
			super(path, flags);
		}

		@Override
		public void onEvent(final int i, final String s) {
			if ((i & FileObserver.MOVE_SELF) != 0) {
				mPath = s;
			}
			String[] strings = new File(mPath).list();
			if (strings == null) {
				mImages = null;
			} else {
				for (int j = 0; j < strings.length; j++) {
					strings[j] = mPath + File.separator + strings[j];
				}
				mImages = Arrays.asList(strings);
				Collections.reverse(mImages);
			}

			clearSelection();
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					notifyDataSetChanged();
				}

			});
		}

	}

}
