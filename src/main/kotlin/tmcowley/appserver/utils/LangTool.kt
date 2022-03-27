package tmcowley.appserver.utils

import org.languagetool.JLanguageTool
import org.languagetool.language.BritishEnglish
import org.languagetool.rules.RuleMatch

class LangTool {

    val langTool: JLanguageTool = JLanguageTool(BritishEnglish())

    init {
        // comment in to use statistical ngram data:
//        langTool.activateLanguageModelRules(new File("/data/google-ngram-data"))
    }

    /** count the number of syntax errors in the given sentence */
    fun countErrors(sentence: String): Int {
        // use language tool to count error instances
        val matches: List<RuleMatch> = langTool.check(sentence)
//        matches.forEach { match ->
//            println("Potential error at characters ${match.fromPos}-${match.toPos}:${match.message}")
//            println("Suggested correction(s): ${match.suggestedReplacements}")
//        }
        return matches.size
    }
}
