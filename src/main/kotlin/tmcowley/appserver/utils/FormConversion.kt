package tmcowley.appserver.utils

import tmcowley.appserver.Singleton
import tmcowley.appserver.models.Key
import tmcowley.appserver.models.KeyPair
import tmcowley.appserver.models.KeyboardSide

// -----
// form-conversion methods

// -----

/**
 * form-conversion methods
 * convert from full-form inputs to half-forms
 */

/** convert from any form to right-hand side form */
fun convertToRight(input: String?): String {
    return convertToForm(KeyboardSide.RIGHT, input)
}

/** convert from any form to left-hand side form */
fun convertToLeft(input: String?): String {
    return convertToForm(KeyboardSide.LEFT, input)
}

/** convert from any form to side forms */
private fun convertToForm(form: KeyboardSide, input: String?): String {
    input ?: return ""

    val inputForm = input
        .map { char ->
            if (isNotAlphabetic(char))
                char
            else
                when (form) {
                    KeyboardSide.LEFT -> (Singleton.getKeyPairOrNull(Key(char)) ?: KeyPair(char, char)).leftKey.character
                    KeyboardSide.RIGHT -> (Singleton.getKeyPairOrNull(Key(char)) ?: KeyPair(char, char)).rightKey.character
                }
        }
        .joinToString(separator = "")

    return inputForm
}