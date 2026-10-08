package com.berkekucuk.mmaapp.presentation.screens.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.jan.supabase.SupabaseClient
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
): SocialAuthHandler {
    val googleAction = supabaseClient.composeAuth.rememberSignInWithGoogle()
    val appleAction = supabaseClient.composeAuth.rememberSignInWithApple(onResult = {})

    return remember(googleAction, appleAction) {
        SocialAuthHandler(
            startGoogleSignIn = { googleAction.startFlow() },
            startAppleSignIn = { appleAction.startFlow() }
        )
    }
}
