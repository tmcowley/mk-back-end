package tmcowley.appserver.utils

import org.languagetool.JLanguageTool
import org.languagetool.language.BritishEnglish
import org.languagetool.rules.RuleMatch
import org.springframework.cache.annotation.Cacheable

final class LangTool {

    val langTool: JLanguageTool = JLanguageTool(BritishEnglish())

    init {
        // comment in to use statistical ngram data:
        // langTool.activateLanguageModelRules(new File("/data/google-ngram-data"));
    }

    @Cacheable
    fun countErrors(sentence: String): Int {
        // use language tool to count error instances
        val matches: List<RuleMatch> = langTool.check(sentence)
        return matches.size
    }

    // untested
    @Cacheable
    fun countErrorsVerbose(sentence: String): Int {

        val matches: List<RuleMatch> = langTool.check(sentence)

        for (match: RuleMatch in matches) {
            println("Potential error at characters ${match.getFromPos()}-${match.getToPos()}:${match.getMessage()}")
            println("Suggested correction(s): ${match.getSuggestedReplacements()}")
        }

        return matches.size
    }
}
