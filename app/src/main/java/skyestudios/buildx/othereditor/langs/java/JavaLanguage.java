package skyestudios.buildx.othereditor.langs.java;


import skyestudios.buildx.othereditor.interfaces.AutoCompleteProvider;
import skyestudios.buildx.othereditor.interfaces.CodeAnalyzer;
import skyestudios.buildx.othereditor.interfaces.EditorLanguage;
import skyestudios.buildx.othereditor.langs.IdentifierAutoComplete;
import skyestudios.buildx.othereditor.langs.internal.MyCharacter;

/**
 * Java language is much complex.
 * This is a basic support
 * @author Rose
 */
public class JavaLanguage implements EditorLanguage {

    private static JavaLanguage _theOne;

    public static EditorLanguage getInstance() {
        if (_theOne == null) {
            _theOne = new JavaLanguage();
        }
        return _theOne;
    }

    public JavaLanguage() {
    }

    @Override
    public CodeAnalyzer createAnalyzer() {
        return new JavaCodeAnalyzer();
    }

    @Override
    public AutoCompleteProvider createAutoComplete() {
        IdentifierAutoComplete autoComplete = new IdentifierAutoComplete();
        autoComplete.setKeywords(JavaTextTokenizer.sKeywords,true);
        return autoComplete;
    }

    @Override
    public boolean isAutoCompleteChar(char ch) {
        return MyCharacter.isJavaIdentifierPart(ch);
    }

    @Override
    public int getIndentAdvance(String content) {
        JavaTextTokenizer t = new JavaTextTokenizer(content);
        Tokens token;
        int advance = 0;
        while ((token = t.directNextToken()) != Tokens.EOF) {
            switch (token) {
                case LBRACE:
                    advance++;
                    break;
                case RBRACE:
                    advance--;
                    break;
            }
        }
        advance = Math.max(0,advance);
        return advance * 4;
    }

    @Override
    public boolean useTab() {
        return true;
    }

    @Override
    public CharSequence format(CharSequence text) {
        return text;
    }
}
