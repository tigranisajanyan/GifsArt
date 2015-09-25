package com.picsart.studio.imagechooserlib;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.picsart.studio.imagechooserlib.adapter.ImagesGridAdapter;
import com.picsart.studio.imagechooserlib.items.FolderData;
import com.picsart.studio.imagechooserlib.items.ImageData;
import com.picsart.studio.imagechooserlib.listeners.OnImagesRetrievedListener;
import com.picsart.studio.imagechooserlib.listeners.RecyclerItemClickListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	private ImagesGridAdapter adapter = null;
	private RecyclerView imagesGrid = null;
	private static int GRID_SPACING;
	private int columnCount = 3;
	private Fragment foldersFragment = null;

	private FragmentManager fragmentManager = null;
	android.support.v4.app.FragmentTransaction fragmentTransaction = null;

	private static final String TAG_FOLDERS_FRAGMENT = "foldersFragmentTag";
	private static final String KEY_SELECTED_FRAGMENT_TAG = "selectedFragmentTag";
	private SlidingUpPanelLayout slidingLayout = null;
	private ArrayList<ImageData> allImages = new ArrayList<>();
	private TextView selectedFolderName = null;
	private TextView selectedImagesCountTextView = null;
	private int selectedImagesCount = 0;
	private View startEditBtn = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		selectedFolderName = (TextView) findViewById(R.id.selected_folder_name);
		selectedImagesCountTextView = (TextView) findViewById(R.id.selected_images_count);
		startEditBtn = findViewById(R.id.btn_edit);
		PhoneImagesRetriever retriever = new PhoneImagesRetriever(getLoaderManager(), getApplicationContext());
		imagesGrid = (RecyclerView) findViewById(R.id.images_grid_recycler_view);
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		columnCount = getResources().getInteger(R.integer.grid_column_count);
		GRID_SPACING = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
		float padding = getResources().getDimension(R.dimen.activity_horizontal_margin);
		float imageSize = (width - 2 * padding - (columnCount - 1) * GRID_SPACING) / columnCount;
		adapter = new ImagesGridAdapter(imageSize);

		retriever.retrieveImages(new OnImagesRetrievedListener() {
			@Override
			public void onImagesRetrieved(ArrayList<ImageData> data) {
				allImages = data;
				for (ImageData imageData : data) {
					adapter.addItem(imageData);
				}
				adapter.notifyDataSetChanged();
				showFoldersList();
			}
		});


		imagesGrid.setLayoutManager(new GridLayoutManager(getApplicationContext(), columnCount));
		imagesGrid.setAdapter(adapter);
		imagesGrid.addItemDecoration(new GridItemDecoration());
		imagesGrid.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				if (adapter.getItem(position).isSelected()) {
					selectedImagesCount--;
					adapter.getItem(position).setSelected(false);
				} else {
					selectedImagesCount++;
					adapter.getItem(position).setSelected(true);
				}
				adapter.notifyItemChanged(position);
				selectedImagesCountTextView.setText(String.valueOf(selectedImagesCount));
			}
		}));

		startEditBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<ImageData> selectedImages = new ArrayList<ImageData>();
				for (ImageData image : allImages) {
					if (image.isSelected()) {
						selectedImages.add(image);
					}
				}

				Intent intent = new Intent();
				intent.putExtra(ImagePickerConstants.DATA_IMAGES_ARRAY, selectedImages);
				setResult(RESULT_OK);
				finish();
			}
		});

	}

	public void selectFolder(FolderData folder) {
		//TODO check if selected folder is social
		slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
		setSelectedFolder(folder.folderPath);
		selectedFolderName.setText(folder.folderName);
	}


	public void setSelectedFolder(String selectedFolderPath) {
		adapter.clear();
		for (ImageData imageData : allImages) {
			if (selectedFolderPath.equalsIgnoreCase(imageData.getFolderPath())) {
				adapter.addItem(imageData);
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
		super.onSaveInstanceState(outState, outPersistentState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private class GridItemDecoration extends RecyclerView.ItemDecoration {

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			super.getItemOffsets(outRect, view, parent, state);
			int position = parent.getChildAdapterPosition(view);
			outRect.set(GRID_SPACING, GRID_SPACING, GRID_SPACING, GRID_SPACING);
		}
	}

	private void showFoldersList() {
		foldersFragment = fragmentManager.findFragmentByTag(TAG_FOLDERS_FRAGMENT);
		if (foldersFragment == null) {
			foldersFragment = new FoldersFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList(ImagePickerConstants.KEY_FOLDERS_LIST, PhoneImagesRetriever.getUniqueFolders(adapter.getItems()));
			foldersFragment.setArguments(bundle);
		}

		if (!foldersFragment.isAdded()) {
			fragmentTransaction.add(R.id.fragment_container, foldersFragment, TAG_FOLDERS_FRAGMENT);
		} else if (foldersFragment.isVisible()) {
			fragmentTransaction.show(foldersFragment);
		}
		findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
		fragmentTransaction.commitAllowingStateLoss();
	}


	@Override
	public void onBackPressed() {
		if (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
			slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
		} else {
			setResult(RESULT_CANCELED);
			finish();
		}
	}
}
