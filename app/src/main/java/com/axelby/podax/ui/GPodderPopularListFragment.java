package com.axelby.podax.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.axelby.gpodder.Client;
import com.axelby.gpodder.dto.Podcast;
import com.axelby.podax.R;
import com.axelby.podax.SubscriptionProvider;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.annotation.Nonnull;

public class GPodderPopularListFragment extends ListFragment {

	public GPodderPopularListFragment() { }

	@Override
	public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.innerlist, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		String[] strings = {"Loading from gpodder.net"};
		setListAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, strings));

		getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Podcast[]>() {
			ToplistPodcastLoader _loader;
			@Override
			public Loader<Podcast[]> onCreateLoader(int i, Bundle bundle) {
				_loader = new ToplistPodcastLoader(getActivity());
				return _loader;
			}

			@Override
			public void onLoadFinished(Loader<Podcast[]> loader, Podcast[] feeds) {
				if (feeds != null)
					setListAdapter(new ToplistAdapter(getActivity(), feeds));
				else {
					String[] strings = {"Error loading toplist: " + _loader.getError()};
					setListAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, strings));
				}
			}

			@Override
			public void onLoaderReset(Loader<Podcast[]> loader) {
			}
		});
	}

	private static class ToplistPodcastLoader extends AsyncTaskLoader<Podcast[]> {
		private String _error = null;

		public ToplistPodcastLoader(Context context) {
			super(context);
		}

		@Override
		public Podcast[] loadInBackground() {
			Client client = new Client();
			List<Podcast> toplist = client.getPodcastToplist(20);
			if (toplist != null)
				return toplist.toArray(new Podcast[20]);
			return null;
		}

		@Override
		protected void onStartLoading() {
			forceLoad();
		}

		@Override
		protected void onStopLoading() {
			cancelLoad();
		}

		public String getError() {
			return _error;
		}
	}

	private static class ToplistAdapter extends ArrayAdapter<Podcast> {
		private Activity _activity;

		private final View.OnClickListener addPodcastHandler = view -> {
			View listitem = (View) view.getParent().getParent();
			Podcast podcast = (Podcast) listitem.getTag();
			SubscriptionProvider.addNewSubscription(getContext(), podcast.url);
		};
		private final View.OnClickListener viewWebsiteHandler = view -> {
			View listitem = (View) view.getParent().getParent();
			Podcast podcast = (Podcast) listitem.getTag();
			_activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(podcast.website)));
		};

		public ToplistAdapter(Activity context, Podcast[] feeds) {
			super(context, R.layout.gpodder_toplist_item, feeds);
			_activity = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Podcast podcast = getItem(position);

			View v = convertView;
			if (v == null)
				v = _activity.getLayoutInflater().inflate(R.layout.gpodder_toplist_item, parent, false);
			if (v == null)
				return null;

			((TextView) v.findViewById(R.id.title)).setText(podcast.title);
			((TextView) v.findViewById(R.id.description)).setText(podcast.description.replace('\n', ' '));
			Picasso.with(getContext()).load(podcast.logo_url).into((ImageView) v.findViewById(R.id.logo));
			v.findViewById(R.id.add).setOnClickListener(addPodcastHandler);
			v.findViewById(R.id.view_website).setOnClickListener(viewWebsiteHandler);
			v.setTag(podcast);
			return v;
		}
	}
}
