package skyestudios.buildx.othereditor.interfaces;

import java.util.List;

import skyestudios.buildx.othereditor.common.TextColorProvider;
import skyestudios.buildx.othereditor.simpleclass.ResultItem;


/**
 * Interface for auto completion analysis
 * @author Rose
 */
public interface AutoCompleteProvider
{

    /**
     * Analyze auto complete items
     * @param prefix The prefix of input to match
     * @param isInCodeBlock Whether auto complete position is in code block
     * @param colors Last analyze result
     * @param line The line of cursor
     * @return Analyzed items
     */
    List<ResultItem> getAutoCompleteItems(String prefix, boolean isInCodeBlock, TextColorProvider.TextColors colors, int line);

}

