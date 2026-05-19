package com.berkekucuk.mmaapp.presentation.screens.profile_edit

sealed interface ProfileEditUiAction {
    data class OnFullNameChanged(val value: String) : ProfileEditUiAction
    data class OnUsernameChanged(val value: String) : ProfileEditUiAction
    data class OnImageSelected(val bytes: ByteArray) : ProfileEditUiAction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as OnImageSelected

            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }
    data object OnSaveClicked : ProfileEditUiAction
    data object OnBackClicked : ProfileEditUiAction
    data object OnDeleteAccountClicked : ProfileEditUiAction
}
