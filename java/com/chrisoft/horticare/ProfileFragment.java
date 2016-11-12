package com.chrisoft.horticare;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment used to display profile management screen
 * Created by Chris on 19/08/2015.
 */
public class ProfileFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "section_number";
    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.container);

        mTabHost.addTab(mTabHost.newTabSpec("Shared Recipes").setIndicator("Recipes"),
                RecipesFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("User Details").setIndicator("User Details"),
                UserFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("New Recipe").setIndicator("New Recipe"),
                RecipeAddFragment.class, null);
        mTabHost.setBackgroundColor(Color.CYAN);
        return mTabHost;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivity act = ((MainActivity) activity);
        if (getArguments()!=null) {
            act.onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
            act.setFilterValue(null);
        }
    }
}