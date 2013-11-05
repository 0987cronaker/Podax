package com.axelby.podax.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.axelby.podax.R;

public class SubscriptionFragment extends Fragment {

	private PodcastListFragment _podcastListFragment = null;
	private SubscriptionListFragment _subscriptionListFragment = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.subscription_fragment, container, false);
	}

	@Override
	public void onDestroyView() {
		try {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.remove(_subscriptionListFragment);
			if (_podcastListFragment != null)
				ft.remove(_podcastListFragment);
			ft.commit();
		} catch (Exception ex) {
		}
		super.onDestroyView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// do this in code to allow nested fragments
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		_subscriptionListFragment = new SubscriptionListFragment();
		ft.replace(R.id.subscriptionlist_fragment, _subscriptionListFragment, "subscriptionlist");
		if (getActivity().findViewById(R.id.podcastlist_fragment) != null) {
			_podcastListFragment = new PodcastListFragment();
			ft.replace(R.id.podcastlist_fragment, _podcastListFragment, "podcastlist");
		}
		ft.commit();
	}
}
