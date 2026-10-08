package com.berkekucuk.mmaapp.presentation.screens.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithApple
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import org.koin.compose.koinInject

class SocialAuthHandler(
    val startGoogleSignIn: () -> Unit,
    val startAppleSignIn: () -> Unit,
)

@Composable
fun rememberSocialAuthHandler(
    supabaseClient: SupabaseClient = koinInject(),
    onSuccess: () -> Unit = {},
): SocialAuthHandler {

    val googleAction = supabaseClient.composeAuth.rememberSignInWithGoogle(
        onResult = { result ->
            if (result is NativeSignInResult.Success) {
                onSuccess()
            }
        }
    )

    val appleAction = supabaseClient.composeAuth.rememberSignInWithApple(
        onResult = { result ->
            if (result is NativeSignInResult.Success) {
                onSuccess()
            }
        }
    )

    return remember(googleAction, appleAction) {
        SocialAuthHandler(
            startGoogleSignIn = { googleAction.startFlow() },
            startAppleSignIn = { appleAction.startFlow() }
        )
    }
}
