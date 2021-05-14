package skyestudios.buildx.othereditor.langs;



import java.util.ArrayList;
import java.util.List;

import skyestudios.buildx.othereditor.common.TextColorProvider;
import skyestudios.buildx.othereditor.interfaces.AutoCompleteProvider;
import skyestudios.buildx.othereditor.interfaces.CodeAnalyzer;
import skyestudios.buildx.othereditor.interfaces.EditorLanguage;
import skyestudios.buildx.othereditor.simpleclass.ResultItem;

/**
 * Empty language without any effect
 * @author Rose
 */
public class EmptyLanguage implements EditorLanguage
{

    @Override
    public CharSequence format(CharSequence text)
    {
        return text;
    }


    @Override
    public CodeAnalyzer createAnalyzer()
    {
        return new CodeAnalyzer(){

            @Override
            public void analyze(CharSequence content, TextColorProvider.TextColors colors, TextColorProvider.AnalyzeThread.Delegate delegate)
            {
                colors.addNormalIfNull();
            }


        };
    }

    @Override
    public AutoCompleteProvider createAutoComplete()
    {
        return new AutoCompleteProvider(){

            @Override
            public List<ResultItem> getAutoCompleteItems(String prefix, boolean isInCodeBlock, TextColorProvider.TextColors colors, int line)
            {
                return new ArrayList<>();
            }


        };
    }

    @Override
    public boolean isAutoCompleteChar(char ch)
    {
        return false;
    }

    @Override
    public int getIndentAdvance(String content)
    {
        return 0;
    }

    @Override
    public boolean useTab()
    {
        return false;
    }

}

