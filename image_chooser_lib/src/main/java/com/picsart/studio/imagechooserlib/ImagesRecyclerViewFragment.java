package com.picsart.studio.imagechooserlib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by AramNazaryan on 9/2/15.
 */
public class ImagesRecyclerViewFragment extends android.support.v4.app.Fragment{

	private RecyclerView imagesRecyclerView = null;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View parentView = inflater.inflate(R.layout.images_list_recycler_view_layout, container, false);
		imagesRecyclerView = (RecyclerView) parentView.findViewById(R.id.images_recycler_view);
		return parentView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
