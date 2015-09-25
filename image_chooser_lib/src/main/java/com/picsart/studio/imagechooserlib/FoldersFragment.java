package com.picsart.studio.imagechooserlib;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.picsart.studio.imagechooserlib.adapter.FoldersListAdapter;
import com.picsart.studio.imagechooserlib.items.FolderData;
import com.picsart.studio.imagechooserlib.listeners.RecyclerItemClickListener;

import java.util.ArrayList;

/**
 * Created by AramNazaryan on 9/9/15.
 */
public class FoldersFragment extends Fragment {

	private RecyclerView foldersListRecyclerView = null;
	private RecyclerView socialsListRecyclerView = null;
	private FoldersListAdapter foldersListAdapter = null;
	private static int GRID_SPACING;

	public FoldersFragment() {
		setRetainInstance(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.folders_fragment_layout, container, false);
	}


	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Bundle arguments = getArguments();
		ArrayList<FolderData> folders = arguments.getParcelableArrayList(ImagePickerConstants.KEY_FOLDERS_LIST);
		foldersListRecyclerView = (RecyclerView) view.findViewById(R.id.folders_list_recycler_view);
		socialsListRecyclerView = (RecyclerView) view.findViewById(R.id.social_folders_recyclerview);
		foldersListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
		foldersListAdapter = new FoldersListAdapter();
		for (FolderData data : folders) {
			foldersListAdapter.add(data);
		}
		foldersListRecyclerView.setAdapter(foldersListAdapter);
		foldersListRecyclerView.addItemDecoration(new OffsetDecorator());
		foldersListRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				MainActivity activity = (MainActivity) getActivity();
				if (activity != null && !activity.isFinishing()) {
					activity.selectFolder(foldersListAdapter.getItem(position));
				}
			}
		}));
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		GRID_SPACING = (int) getActivity().getResources().getDimension(R.dimen.activity_horizontal_margin);
	}

	private class OffsetDecorator  extends RecyclerView.ItemDecoration {
		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			super.getItemOffsets(outRect, view, parent, state);
			int position = parent.getChildAdapterPosition(view);
			outRect.set(0, GRID_SPACING, 0, GRID_SPACING);
		}
	}
}
