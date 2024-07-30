package com.kelsix.mymoviefinder.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.kelsix.mymoviefinder.R;

public class SearchFragment extends Fragment {
    private NavController navController;
    private String query;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        navController = NavHostFragment.findNavController(this);

        view.findViewById(R.id.backButton).setOnClickListener(v -> navController.popBackStack());

        searchView = view.findViewById(R.id.searchView);

        if (getArguments() != null) {
            query = getArguments().getString("query");
            searchView.setQuery(query, false);
            searchForQuery(view, query);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchForQuery(view, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    private void searchForQuery(View v, String query) {
        ViewPager2 viewPager = v.findViewById(R.id.view_pager);
        TabLayout tabLayout = v.findViewById(R.id.tab_layout);

        SearchPagerAdapter adapter = new SearchPagerAdapter(requireActivity(), query);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Movies");
                            break;
                        case 1:
                            tab.setText("TV Series");
                            break;
                        case 2:
                            tab.setText("Actors");
                            break;
                    }
                }).attach();
    }

    public static class SearchPagerAdapter extends FragmentStateAdapter {

        private final String query;

        public SearchPagerAdapter(@NonNull FragmentActivity fragmentActivity, String query) {
            super(fragmentActivity);
            this.query = query;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;
            switch (position) {
                case 1:
                    fragment = new TvSearchFragment();
                    break;
                case 2:
                    fragment = new ActorSearchFragment();
                    break;
                default:
                    fragment = new MovieSearchFragment();
                    break;
            }

            Bundle args = new Bundle();
            args.putString("query", query);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
