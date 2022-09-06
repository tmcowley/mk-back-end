package tmcowley.appserver.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tmcowley.appserver.models.SignUpForm
import tmcowley.appserver.models.TrainingSessionData

internal class ValidationKtTests {

    @Test
    fun `sign-up form validation - valid forms`() {
        // given
        val validSignUpForms = listOf(
            // vary age from min to max
            SignUpForm(13, 60),
            SignUpForm(21, 60),
            SignUpForm(200, 60),

            // vary speed from min to max
            SignUpForm(21, 0),
            SignUpForm(21, 60),
            SignUpForm(21, 400)
        )

        validSignUpForms.forEach { form ->
            // when
            val formIsValid = validateSignUpForm(form)

            // then
            assertThat(formIsValid)
        }
    }

    @Test
    fun `sign-up form validation - invalid forms`() {
        // given
        val invalidSignUpForms = listOf(
            // age fails
            SignUpForm(-10, 60),
            SignUpForm(12, 60),
            SignUpForm(201, 60),

            // speed fails
            SignUpForm(13, -10),
            SignUpForm(13, 401),

            // both fail
            SignUpForm(0, -1)
        )

        invalidSignUpForms.forEach { form ->
            // when
            val formIsValid = validateSignUpForm(form)

            // then
            assertThat(formIsValid).isFalse
        }
    }

    @Test
    fun `training session validation - valid`() {
        // given
        val validTrainingSessions = listOf(
            // vary speed from min to max
            TrainingSessionData(0f, 80f),
            TrainingSessionData(60f, 80f),
            TrainingSessionData(100f, 80f),
            TrainingSessionData(400f, 80f),

            // vary accuracy from min to max
            TrainingSessionData(60f, 0f),
            TrainingSessionData(60f, 50f),
            TrainingSessionData(60f, 100f)
        )

        validTrainingSessions.forEach { session ->
            // when
            val trainingSessionIsValid = validateTrainingSessionData(session)

            // then
            assertThat(trainingSessionIsValid)
        }
    }

    @Test
    fun `training session validation - invalid`() {
        // given
        val invalidTrainingSessions = listOf(
            // speed fails
            TrainingSessionData(-10f, 80f),

            // accuracy fails
            TrainingSessionData(60f, -1f),
            TrainingSessionData(60f, 101f)
        )

        invalidTrainingSessions.forEach { session ->
            // when
            val trainingSessionIsValid = validateTrainingSessionData(session)

            // then
            assertThat(trainingSessionIsValid).isFalse
        }
    }

    @Test
    fun `user-code format validation - valid`() {
        // given
        val codes = listOf(
            "spilt-cubes-world",
            "altos-fiche-twist",
            "altar-drift-drool"
        )

        codes.forEach { code ->
            // when
            val codeIsValid = validateUserCode(code)

            // then
            assertThat(codeIsValid)
        }
    }

    @Test
    fun `user-code format validation - invalid`() {
        // given
        val codes = listOf(

            // empty
            "",

            // too short
            "altos-fiche",

            // too long
            "spilt-cubes-worlds",

            // wrong formatting
            "spilt_cubes_world",

            // words wrong size
            "a-caterpillar-art",

            // wrong case
            "SPLIT-CUBES-WORLD",
            "Spilt-Cubes-World",
        )

        codes.forEach { code ->
            // when
            val codeIsValid = validateUserCode(code)

            // then
            assertThat(codeIsValid).isFalse
        }
    }
}