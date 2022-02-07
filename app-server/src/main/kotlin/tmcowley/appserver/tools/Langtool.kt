package tmcowley.appserver.tools

import org.languagetool.JLanguageTool
import org.languagetool.language.BritishEnglish
import org.languagetool.rules.RuleMatch;

class Langtool {
    fun countErrors(sentence: String): Int {
        val langTool: JLanguageTool = JLanguageTool(BritishEnglish());

        // comment in to use statistical ngram data:
        // langTool.activateLanguageModelRules(new File("/data/google-ngram-data"));

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
