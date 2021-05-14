package skyestudios.buildx.helpers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import skyestudios.buildx.fragments.logic.LogicFragment;
import skyestudios.buildx.fragments.view.ViewFragment;

public class MainAdapter extends FragmentPagerAdapter {


    public MainAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ViewFragment();
            case 1:
                return new LogicFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

}
