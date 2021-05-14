package skyestudios.buildx.activities.search;

import java.util.List;

import skyestudios.buildx.models.SearchLib;

public interface SearchView {
    void getResponse(List<SearchLib> libs);
    void getError();
}
