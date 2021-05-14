package skyestudios.buildx.othereditor.interfaces;


import skyestudios.buildx.othereditor.common.TextColorProvider;

/**
 * Interface for analyzing highlight
 * @author Rose
 */
public interface CodeAnalyzer {


    void analyze(CharSequence content, TextColorProvider.TextColors colors, TextColorProvider.AnalyzeThread.Delegate delegate);

}
