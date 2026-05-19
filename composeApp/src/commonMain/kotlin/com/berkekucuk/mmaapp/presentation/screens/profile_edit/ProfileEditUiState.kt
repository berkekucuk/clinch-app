package com.berkekucuk.mmaapp.presentation.screens.profile_edit

import com.berkekucuk.mmaapp.core.utils.AppError

data class ProfileEditUiState(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val selectedImageBytes: ByteArray? = null,
    val originalFullName: String = "",
    val originalUsername: String = "",
    val isSaving: Boolean = false,
    val error: AppError? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ProfileEditUiState

        if (isSaving != other.isSaving) return false
        if (fullName != other.fullName) return false
        if (username != other.username) return false
        if (email != other.email) return false
        if (avatarUrl != other.avatarUrl) return false
        if (!selectedImageBytes.contentEquals(other.selectedImageBytes)) return false
        if (originalFullName != other.originalFullName) return false
        if (originalUsername != other.originalUsername) return false
        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isSaving.hashCode()
        result = 31 * result + fullName.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + avatarUrl.hashCode()
        result = 31 * result + (selectedImageBytes?.contentHashCode() ?: 0)
        result = 31 * result + originalFullName.hashCode()
        result = 31 * result + originalUsername.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        return result
    }
}
