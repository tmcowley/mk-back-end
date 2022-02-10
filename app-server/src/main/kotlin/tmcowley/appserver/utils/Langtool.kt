package tmcowley.appserver.utils

import org.languagetool.JLanguageTool
import org.languagetool.language.BritishEnglish
import org.languagetool.rules.RuleMatch;

class Langtool {

    val langTool: JLanguageTool = JLanguageTool(BritishEnglish());

    init{
        // comment in to use statistical ngram data:
        // langTool.activateLanguageModelRules(new File("/data/google-ngram-data"));
    }

    fun countErrors(sentence: String): Int {

        val hardcodedSentence = "A sentence with a error in the Hitchhiker's Guide tot he Galaxy";
        val matches: List<RuleMatch> = langTool.check(sentence);

        return matches.size;
        // for (match: RuleMatch in matches) {
        //   System.out.println("Potential error at characters " +
        //       match.getFromPos() + "-" + match.getToPos() + ": " +
        //       match.getMessage());
        //   System.out.println("Suggested correction(s): " +
        //       match.getSuggestedReplacements());
        // }
    }
}
