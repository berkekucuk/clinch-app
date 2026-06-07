package com.berkekucuk.mmaapp.core.presentation.strings

import com.berkekucuk.mmaapp.core.presentation.AppLanguage
import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.enums.ReportReason

val EnStrings = AppStrings(
    language = AppLanguage.EN,
    tabUpcoming = "Upcoming",
    tabCompleted = "Completed",
    toBeAnnounced = "TO BE ANNOUNCED",
    tba = "tba",
    liveEvent = "Live Event",
    selectYear = "Select Year",
    contentDescriptionFlag = "Flag",
    contentDescriptionInfo = "Information",
    contentDescriptionWin = "Win",
    contentDescriptionLoss = "Loss",
    eventDetailsFallback = "Event Details",
    contentDescriptionBack = "Back",
    tabMainCard = "Main Card",
    tabPrelims = "Prelims",
    emptyMainCardFights = "No main card fights available",
    emptyPrelimFights = "No prelim fights available",
    tabFightDetails = "Details",
    tabFightComparison = "Comparison",
    radarLabelHeight = "Height",
    radarLabelReach = "Reach",
    radarLabelOdds = "Odds",
    radarLabelWinRate = "Win %",
    radarLabelKoTkoRate = "KO/TKO %\n(UFC)",
    radarLabelSubRate = "Submission %\n(UFC)",
    fightDetailLabelName = "Name",
    fightDetailLabelAge = "Age at Fight",
    fightDetailLabelHometown = "Fighting out of",
    fightDetailLabelHeight = "Height",
    fightDetailLabelReach = "Reach",
    fightDetailLabelResult = "Result",
    fightDetailLabelOdds = "Odds",
    fightDetailLabelRecord = "Record After Fight",
    fightDetailLabelRoundsFormat = "Format",
    fightDetailLabelMethod = "Method",
    fightDetailLabelRoundSummary = "Time",
    heightCm = { "$it cm" },
    rankingsTitle = "Rankings",
    tabMens = "Men's",
    tabWomens = "Women's",
    rankingsChampion = "CHAMPION",
    rankingsVacant = "Vacant",
    contentDescriptionCollapse = "Collapse",
    contentDescriptionExpand = "Expand",
    rankingsChampionRankLabel = "C",
    tabOverview = "Overview",
    tabFights = "Fights",
    fighterDetailLabelRecord = "Record",
    fighterDetailLabelWeightClass = "Weight Class",
    fighterDetailLabelHeight = "Height",
    fighterDetailLabelReach = "Reach",
    fighterDetailLabelAge = "Age",
    fighterDetailLabelDateOfBirth = "Date of Birth",
    fighterDetailLabelBorn = "Born",
    fighterDetailLabelFightingOutOf = "Fighting Out Of",
    fighterDetailValueUnavailable = "—",
    fighterDetailAgeYears = { age, years -> "$age ($years yrs)" },
    fighterDetailRecordWins = "Wins",
    fighterDetailRecordLosses = "Losses",
    fighterDetailRecordDraws = "Draws",
    fighterDetailResultWin = "W",
    fighterDetailResultLoss = "L",
    fighterDetailResultDraw = "D",
    fighterDetailResultNoContest = "NC",
    fighterDetailResultPending = "–",
    profileEdit = "Edit",
    profileSignOut = "Sign Out",
    profileTabOverview = "Overview",
    profileTabPredictions = "Predictions",
    emptyPredictionList = "No predictions made yet.",
    emptyInteractionList = "No items added yet.",
    profileFavoriteFighters = "Favorite Fighters",
    profileGoatFighters = "Goat List",
    profileHatedFighters = "Hated Fighters",
    profileEditTitle = "Edit Profile",
    profileEditPersonalInfo = "Personal Information",
    profileEditEmail = "Email",
    profileEditFullName = "Name",
    profileEditUsernameLabel = "Username",
    profileEditSaveChanges = "Save",
    profileEditDeleteAccount = "Delete Account",
    profileEditDeleteAccountTitle = "Delete Account",
    profileEditDeleteAccountConfirm = "Are you sure you want to delete your account? This action cannot be undone and all your data will be permanently removed.",
    reportUserTitle = "Report",
    reportUserSubmit = "Submit",
    reportReasonDisplayName = { reason ->
        when (reason) {
            ReportReason.INAPPROPRIATE_PROFILE_PICTURE -> "Inappropriate profile picture"
            ReportReason.INAPPROPRIATE_USERNAME -> "Inappropriate username"
            ReportReason.SPAM_OR_BOT -> "Spam or bot"
            ReportReason.ABUSIVE_OR_HARASSING_BEHAVIOR -> "Abusive or harassing behavior"
            ReportReason.OTHER -> "Other"
        }
    },
    blockUserTitle = "Block",
    blockUserConfirm = "Are you sure you want to block this user? You will no longer see their profile.",
    fighterSearchPlaceholder = "Search fighters...",
    fighterSearchEmpty = "No fighters found",
    retry = "Retry",
    dialogAccept = "Accept",
    dialogCancel = "Cancel",
    fightNotificationDialogMessage = "You'll receive notifications before the fight starts and for the result.",
    fightNotificationRemoveDialogMessage = "Turn off notifications for this fight.",
    notificationPermissionSettingsTitle = "Enable Notifications",
    notificationPermissionSettingsMessage = "Please enable notifications in your device settings.",
    riskUnsure = "25 (Unsure)",
    riskNormal = "50 (Normal)",
    riskConfident = "75 (Confident)",
    riskAllIn = "100 (Sure)",
    pointsFormat = { points -> if (points > 0) "+$points pts" else "$points pts" },
    settingsTitle = "Settings",
    settingsSectionLanguage = "Language",
    settingsSectionMeasurements = "Measurements",
    settingsSectionOdds = "Odds Format",
    settingsSectionTheme = "Theme",
    settingsThemeLight = "Light Mode",
    settingsThemeDark = "Dark Mode",
    settingsSectionBlockedUsers = "Blocked Users",
    settingsSectionBlockedUsersSub = "Manage blocked accounts",
    unblockUser = "Unblock",
    blockedUsersEmpty = "No blocked users.",
    menuSignInPrompt = "Sign in to follow your favorite fighters and get real-time event notifications.",
    menuTitle = "Menu",
    menuItemLeaderboard = "Leaderboard",
    menuItemNotifications = "Notifications",
    menuItemSettings = "Settings",
    menuSignInButton = "Sign in",
    menuSignInTerms = "By signing in, you accept our Privacy Policy.",
    menuSignInPrivacyPolicy = "Privacy Policy",
    menuProfileSettings = "Profile Settings",
    menuSignInWithGoogle = "Sign in with Google",
    menuSignInWithApple = "Sign in with Apple",
    menuNotificationsDisabled = "You have disabled Notifications",
    menuNotificationsEnabled = "Enabled",
    navEvents = "Events",
    navRankings = "Rankings",
    navMenu = "Menu",
    weightClassDisplayName = { id ->
        when (id.uppercase()) {
            "SW" -> "Women's Strawweight"
            "FLW" -> "Flyweight"
            "W_FLW" -> "Women's Flyweight"
            "BW" -> "Bantamweight"
            "W_BW" -> "Women's Bantamweight"
            "FW" -> "Featherweight"
            "W_FW" -> "Women's Featherweight"
            "LW" -> "Lightweight"
            "WW" -> "Welterweight"
            "MW" -> "Middleweight"
            "LHW" -> "Light Heavyweight"
            "HW" -> "Heavyweight"
            "CW" -> "Catchweight"
            "MENS_P4P" -> "Men's Pound-for-Pound"
            "WOMENS_P4P" -> "Women's Pound-for-Pound"
            else -> id
        }
    },
    resultDisplayName = { name ->
        when (name) {
            "WIN" -> "Win"
            "LOSS" -> "Loss"
            "DRAW" -> "Draw"
            "NO_CONTEST" -> "No Contest"
            "PENDING" -> "Pending"
            "CANCELLED" -> "Cancelled"
            "FIZZLED" -> "Fizzled"
            else -> name.lowercase().replaceFirstChar { it.uppercase() }
        }
    },
    toUpperCase = { it.uppercase() },
    predictionQuestionTitle = "Who will win?",
    predictionConfirmTitle = "Lock in Prediction",
    predictionConfirmMessage = { name -> "Confirm $name? Once locked, it cannot be changed and points will be calculated based on your selected risk." },
    leaderboardInfoTitle = "Score System",
    leaderboardInfoClose = "Close",
    notificationChannelName = "Fight Results",
    notificationChannelDescription = "Get notified about fight results",
    commonRemove = "Remove",
    commonCancel = "Cancel",
    profileRemoveFighterConfirm = { name -> "Remove $name from your list?" },
    interactionLimitReachedTitle = "Limit Reached",
    interactionLimitReachedText = "You can add a maximum of 5 fighters to this list.",
    dialogOkay = "Okay",
    mapError = { error ->
        when (error) {
            AppError.NETWORK -> "No internet connection."
            AppError.SERVER_ERROR -> "Something went wrong."
            AppError.UNAUTHENTICATED -> "Please sign in to use this feature."
            AppError.ALREADY_EXISTS -> "This fighter is already in your list."
            AppError.USERNAME_TAKEN -> "This username is already taken."
            AppError.EMPTY_USERNAME -> "Username cannot be empty."
            AppError.INVALID_USERNAME -> "Username can only contain a-z, 0-9, _, and dots."
            AppError.USERNAME_TOO_SHORT -> "Username must be at least 3 characters."
            AppError.USERNAME_TOO_LONG -> "Username cannot exceed 20 characters."
            AppError.EMPTY_FULLNAME -> "Name cannot be empty."
            AppError.FULLNAME_TOO_SHORT -> "Name must be at least 3 characters."
            AppError.FULLNAME_TOO_LONG -> "Name cannot exceed 50 characters."
            AppError.ODDS_NOT_PUBLISHED -> "Odds pending. Predictions opening soon."
            AppError.EVENT_OVER -> "Event already over."
            AppError.FIGHT_OVER -> "Fight already over."
            AppError.FIGHT_PENDING -> "Result pending. Predictions locked."
            AppError.ALREADY_REPORTED -> "You have already reported this user."
            AppError.UNKNOWN -> "Something went wrong."
            else -> null
        }
    }
)
