package tmcowley.appserver.utils

import org.languagetool.JLanguageTool
import org.languagetool.language.BritishEnglish
import org.languagetool.rules.RuleMatch

class LangTool {

    private val langTool: JLanguageTool = JLanguageTool(BritishEnglish())

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
