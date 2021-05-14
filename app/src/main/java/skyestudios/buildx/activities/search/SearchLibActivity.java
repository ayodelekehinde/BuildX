package skyestudios.buildx.activities.search;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import skyestudios.buildx.R;
import skyestudios.buildx.adapters.LibsAdapter;
import skyestudios.buildx.adapters.SearchLibsAdapter;
import skyestudios.buildx.helpers.Constants;
import skyestudios.buildx.helpers.Utils;
import skyestudios.buildx.interfaces.ResponseCallback;
import skyestudios.buildx.models.APIResponse;
import skyestudios.buildx.models.Libs;
import skyestudios.buildx.models.SearchLib;
import skyestudios.buildx.service.SearchTask;

public class SearchLibActivity extends AppCompatActivity implements SearchView {

    private AppCompatImageView mDetailBackButtonPost;
    private AppCompatTextView mTitleToolbar;
    private Toolbar mLayoutToolbar;
    private AppCompatEditText mSearchText;
    private AppCompatImageView mTextviewCancel;
    private RecyclerView mListSearch;
    private SwipeRefreshLayout mRefreshCategory;
    private SearchLibsAdapter adapter;
    private SearchInteractor interactor;

    @Override
    protected void onStart() {
        super.onStart();
        interactor = new SearchInteractor(this);
        interactor.getFirstCookie();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib_search);
        initView();

    }

    private void initView() {
        mDetailBackButtonPost = (AppCompatImageView) findViewById(R.id.post_detail_back_button);
        mTitleToolbar = (AppCompatTextView) findViewById(R.id.toolbar_title);
        mLayoutToolbar = (Toolbar) findViewById(R.id.toolbar_layout);
        mSearchText = (AppCompatEditText) findViewById(R.id.search_text);
        mTextviewCancel = (AppCompatImageView) findViewById(R.id.cancel_textview);
        mListSearch = (RecyclerView) findViewById(R.id.search_list);
        mRefreshCategory = (SwipeRefreshLayout) findViewById(R.id.category_refresh);
        mDetailBackButtonPost.setOnClickListener(view -> finish());
        mTextviewCancel.setOnClickListener(view -> mSearchText.setText(""));
        setUpEditText(mSearchText);

    }
    private void initRecyclerView() {
        mListSearch.setHasFixedSize(true);
        mListSearch.setItemViewCacheSize(20);
        mListSearch.setDrawingCacheEnabled(true);

       LinearLayoutManager mManager = new LinearLayoutManager(this);
        adapter = new SearchLibsAdapter(this);
        mListSearch.setLayoutManager(mManager);
        mListSearch.setAdapter(adapter);
    }
    private void setUpEditText(AppCompatEditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
                }
                return true;
            }
        });

    }

    private void doSearch() {
        if (!mSearchText.getText().toString().isEmpty() && mSearchText.getText().length() >= 3) {
            clearFocus();
            showOrHideProgressBar(true);
            interactor.getSearch(mSearchText.getText().toString().trim());
        } else {
            mSearchText.setText("");
            mSearchText.setHint("Enter at least 3 characters");
        }

    }

    private void showOrHideProgressBar(boolean show) {
            mRefreshCategory.setRefreshing(show);
    }

    private void clearFocus() {
        mSearchText.clearFocus();
        InputMethodManager mm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
    }

    @Override
    public void getResponse(List<SearchLib> libs) {
        initRecyclerView();
        adapter.getList(libs);
        showOrHideProgressBar(false);
    }

    @Override
    public void getError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.showMessage(SearchLibActivity.this, "An Error Occurred");
            }
        });
    }

}
