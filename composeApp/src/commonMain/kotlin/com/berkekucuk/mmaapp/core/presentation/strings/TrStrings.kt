package com.berkekucuk.mmaapp.core.presentation.strings

import com.berkekucuk.mmaapp.core.presentation.AppLanguage
import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.enums.ReportReason

val TrStrings = AppStrings(
    language = AppLanguage.TR,
    tabUpcoming = "Yaklaşan",
    tabCompleted = "Tamamlanan",
    toBeAnnounced = "AÇIKLANACAK",
    tba = "tba",
    liveEvent = "Canlı Etkinlik",
    selectYear = "Yıl Seçin",
    contentDescriptionFlag = "Bayrak",
    contentDescriptionInfo = "Bilgi",
    contentDescriptionWin = "Galibiyet",
    contentDescriptionLoss = "Yenilgi",
    eventDetailsFallback = "Etkinlik Detayları",
    contentDescriptionBack = "Geri",
    tabMainCard = "Ana Kart",
    tabPrelims = "Ön Eleme Kartı",
    emptyMainCardFights = "Mevcut değil",
    emptyPrelimFights = "Mevcut değil",
    tabFightDetails = "Detaylar",
    tabFightComparison = "Karşılaştırma",
    radarLabelHeight = "Boy",
    radarLabelReach = "Kanat Açıklığı",
    radarLabelOdds = "Oranlar",
    radarLabelWinRate = "Galibiyet %",
    radarLabelKoTkoRate = "KO/TKO %\n(UFC)",
    radarLabelSubRate = "Pes Ettirme %\n(UFC)",
    fightDetailLabelName = "İsim",
    fightDetailLabelAge = "Maçtaki Yaş",
    fightDetailLabelHometown = "Temsil Ediyor",
    fightDetailLabelHeight = "Boy",
    fightDetailLabelReach = "Kanat Açıklığı",
    fightDetailLabelResult = "Sonuç",
    fightDetailLabelOdds = "Oranlar",
    fightDetailLabelRecord = "Maç Sonrası Rekor",
    fightDetailLabelRoundsFormat = "Format",
    fightDetailLabelMethod = "Yöntem",
    fightDetailLabelRoundSummary = "Süre",
    heightCm = { "$it cm" },
    rankingsTitle = "Sıralamalar",
    tabMens = "Erkekler",
    tabWomens = "Kadınlar",
    rankingsChampion = "ŞAMPİYON",
    rankingsVacant = "Boş",
    contentDescriptionCollapse = "Kapat",
    contentDescriptionExpand = "Aç",
    rankingsChampionRankLabel = "C",
    tabOverview = "Genel",
    tabFights = "Dövüşler",
    fighterDetailLabelRecord = "Rekor",
    fighterDetailLabelWeightClass = "Siklet",
    fighterDetailLabelHeight = "Boy",
    fighterDetailLabelReach = "Kanat Açıklığı",
    fighterDetailLabelAge = "Yaş",
    fighterDetailLabelDateOfBirth = "Doğum Tarihi",
    fighterDetailLabelBorn = "Doğum Yeri",
    fighterDetailLabelFightingOutOf = "Temsil Ediyor",
    fighterDetailValueUnavailable = "—",
    fighterDetailAgeYears = { age, years -> "$age ($years yaş)" },
    fighterDetailRecordWins = "Galibiyet",
    fighterDetailRecordLosses = "Yenilgi",
    fighterDetailRecordDraws = "Beraberlik",
    fighterDetailResultWin = "W",
    fighterDetailResultLoss = "L",
    fighterDetailResultDraw = "D",
    fighterDetailResultNoContest = "NC",
    fighterDetailResultPending = "–",
    profileEdit = "Düzenle",
    profileSignOut = "Çıkış Yap",
    profileTabOverview = "Genel",
    profileTabPredictions = "Tahminler",
    emptyPredictionList = "Henüz tahminde bulunulmadı.",
    emptyInteractionList = "Henüz bir ekleme yapılmadı.",
    profileFavoriteFighters = "Favori Dövüşçüler",
    profileGoatFighters = "Goat Listesi",
    profileHatedFighters = "Sevilmeyen Dövüşçüler",
    profileJoinedDate = { date -> "$date tarihinde katıldı" },
    profileEditTitle = "Profili Düzenle",
    profileEditPersonalInfo = "Kişisel Bilgiler",
    profileEditEmail = "E-posta",
    profileEditFullName = "Ad",
    profileEditUsernameLabel = "Kullanıcı Adı",
    profileEditSaveChanges = "Kaydet",
    profileEditDeleteAccount = "Hesabı Sil",
    profileEditDeleteAccountTitle = "Hesabı Sil",
    profileEditDeleteAccountConfirm = "Hesabınızı silmek istediğinizden emin misiniz? Bu işlem geri alınamaz ve tüm verileriniz kalıcı olarak silinecektir.",
    reportUserTitle = "Şikayet Et",
    reportUserSubmit = "Gönder",
    reportReasonDisplayName = { reason ->
        when (reason) {
            ReportReason.INAPPROPRIATE_PROFILE_PICTURE -> "Uygunsuz profil fotoğrafı"
            ReportReason.INAPPROPRIATE_USERNAME -> "Uygunsuz kullanıcı adı"
            ReportReason.SPAM_OR_BOT -> "Spam veya bot"
            ReportReason.ABUSIVE_OR_HARASSING_BEHAVIOR -> "Taciz edici veya kötüye kullanım"
            ReportReason.OTHER -> "Diğer"
        }
    },
    blockUserTitle = "Engelle",
    blockUserConfirm = "Bu kullanıcıyı engellemek istediğinizden emin misiniz? Artık bu kullanıcının profilini görmeyeceksiniz.",
    fighterSearchPlaceholder = "Dövüşçü ara...",
    fighterSearchEmpty = "Dövüşçü bulunamadı",
    retry = "Yenile",
    dialogAccept = "Onayla",
    dialogCancel = "Vazgeç",
    fightNotificationDialogMessage = "Bu dövüş için bildirimleri aç.",
    fightNotificationRemoveDialogMessage = "Bu dövüş için bildirimleri kapat.",
    fightReminderRemoveDialogMessage = "Bu dövüş için hatırlatıcıyı kapat.",
    notificationPermissionSettingsTitle = "Bildirim İzinleri",
    notificationPermissionSettingsMessage = "Lütfen cihaz ayarlarınızdan bildirimleri etkinleştirin.",
    riskUnsure = "25 (Emin Değilim)",
    riskNormal = "50 (Normal)",
    riskConfident = "75 (Güveniyorum)",
    riskAllIn = "100 (Eminim)",
    settingsTitle = "Ayarlar",
    settingsSectionLanguage = "Dil",
    settingsSectionMeasurements = "Ölçüm Birimi",
    settingsSectionOdds = "Oran Formatı",
    settingsSectionTheme = "Tema",
    settingsThemeLight = "Açık Tema",
    settingsThemeDark = "Koyu Tema",
    settingsSectionBlockedUsers = "Engellenen Kullanıcılar",
    settingsSectionBlockedUsersSub = "Engellenen hesapları yönetin",
    settingsSectionSupport = "Destek & Geri Bildirim",
    settingsSectionSupportSub = "clinchapp0@gmail.com",
    unblockUser = "Engeli Kaldır",
    blockedUsersEmpty = "Engellenen kullanıcı yok.",
    menuSignInPrompt = "Favori dövüşçülerinizi takip edin, tahminlerle puan kazanın ve gerçek zamanlı etkinlik bildirimleri alın.",
    menuTitle = "Menü",
    menuItemLeaderboard = "Liderlik Tablosu",
    menuItemNotifications = "Bildirimler",
    menuItemSettings = "Ayarlar",
    menuSignInButton = "Giriş yap",
    menuSignInTerms = "Giriş yaparak Gizlilik Politikası'nı kabul etmiş olursunuz.",
    menuSignInPrivacyPolicy = "Gizlilik Politikası",
    menuProfileSettings = "Profil Ayarları",
    menuSignInWithGoogle = "Google ile giriş yap",
    menuSignInWithApple = "Apple ile giriş yap",
    menuNotificationsDisabled = "Bildirimleri kapattınız",
    menuNotificationsEnabled = "Açık",
    navEvents = "Etkinlikler",
    navRankings = "Sıralamalar",
    navMenu = "Menü",
    weightClassDisplayName = { id ->
        when (id.uppercase()) {
            "SW" -> "Kadınlar Saman Siklet"
            "FLW" -> "Sinek Siklet"
            "W_FLW" -> "Kadınlar Sinek Siklet"
            "BW" -> "Horoz Siklet"
            "W_BW" -> "Kadınlar Horoz Siklet"
            "FW" -> "Tüy Siklet"
            "W_FW" -> "Kadınlar Tüy Siklet"
            "LW" -> "Hafif Siklet"
            "WW" -> "Welter Siklet"
            "MW" -> "Orta Siklet"
            "LHW" -> "Hafif Ağır Siklet"
            "HW" -> "Ağır Siklet"
            "CW" -> "Ara Siklet"
            "MENS_P4P" -> "Erkekler Pound-for-Pound"
            "WOMENS_P4P" -> "Kadınlar Pound-for-Pound"
            else -> id
        }
    },
    resultDisplayName = { name ->
        when (name) {
            "WIN" -> "Galibiyet"
            "LOSS" -> "Yenilgi"
            "DRAW" -> "Beraberlik"
            "NO_CONTEST" -> "Sonuçsuz"
            "PENDING" -> "Beklemede"
            "CANCELLED" -> "İptal Edildi"
            "FIZZLED" -> "Gerçekleşmedi"
            else -> name.lowercase().replaceFirstChar { it.uppercase() }
        }
    },
    toUpperCase = { str ->
        str.map { char ->
            when (char) {
                'i' -> 'İ'
                'ı' -> 'I'
                else -> char.uppercaseChar()
            }
        }.joinToString("")
    },
    predictionQuestionTitle = "Kim kazanır?",
    predictionConfirmTitle = "Tahmini Kilitle",
    predictionConfirmMessage = { name -> "$name seçimini onaylıyor musunuz? Kilitlenen tahminler değiştirilemez ve puanınız seçtiğiniz riske göre hesaplanır." },
    leaderboardInfoTitle = "Skor Sistemi",
    leaderboardInfoClose = "Kapat",
    tabOverall = "Genel",
    tabWeekly = "Haftalık",
    notificationChannelName = "Dövüş Sonuçları",
    notificationChannelDescription = "Dövüş sonuçları hakkında bildirim al",
    commonRemove = "Kaldır",
    commonCancel = "Vazgeç",
    profileRemoveFighterConfirm = { name -> "$name listenizden kaldırılsın mı?" },
    interactionLimitReachedTitle = "Limit Doldu",
    interactionLimitReachedText = "Bu listeye en fazla 5 dövüşçü ekleyebilirsiniz.",
    dialogOkay = "Tamam",
    mapError = { error ->
        when (error) {
            AppError.NETWORK -> "İnternet bağlantısı yok."
            AppError.SERVER_ERROR -> "Sunucu kaynaklı bir hata oluştu."
            AppError.UNAUTHENTICATED -> "Bu özelliği kullanmak için lütfen giriş yapın."
            AppError.ALREADY_EXISTS -> "Bu dövüşçü zaten listenizde."
            AppError.USERNAME_TAKEN -> "Bu kullanıcı adı zaten alınmış."
            AppError.EMPTY_USERNAME -> "Kullanıcı adı boş olamaz."
            AppError.INVALID_USERNAME -> "Kullanıcı adı yalnızca a-z, 0-9, _ ve nokta içerebilir."
            AppError.USERNAME_TOO_SHORT -> "Kullanıcı adı en az 3 karakter olmalıdır."
            AppError.USERNAME_TOO_LONG -> "Kullanıcı adı en fazla 20 karakter olmalıdır."
            AppError.EMPTY_FULLNAME -> "Ad boş olamaz."
            AppError.FULLNAME_TOO_SHORT -> "Ad en az 3 karakter olmalıdır."
            AppError.FULLNAME_TOO_LONG -> "Ad en fazla 50 karakter olmalıdır."
            AppError.ODDS_NOT_PUBLISHED -> "Oranlar bekleniyor. Tahminler yakında açılacak."
            AppError.EVENT_OVER -> "Etkinlik zaten tamamlandı."
            AppError.FIGHT_OVER -> "Bu dövüş zaten tamamlandı."
            AppError.FIGHT_PENDING -> "Sonuç bekleniyor. Tahminler kapandı."
            AppError.ALREADY_REPORTED -> "Bu kullanıcıyı zaten şikayet ettiniz."
            AppError.UNKNOWN -> "Bir şeyler ters gitti."
            else -> null
        }
    },
    updateForceTitle = "Güncelleme Gerekli",
    updateForceMessage = "Clinch'i kullanmaya devam edebilmek için lütfen uygulamayı son sürüme güncelleyin.",
    updateFlexibleTitle = "Güncelleme Mevcut",
    updateFlexibleMessage = "Clinch için yeni özellikler ve performans iyileştirmeleri içeren bir güncelleme mevcut.",
    updateButton = "Güncelle",
    updateLaterButton = "Sonra",
    alarmFightTime = "MAÇ BAŞLIYOR!",
    alarmDismiss = "ALARMI SUSTUR",
    notificationTypeTitle = "Hatırlatıcı Türü",
    notificationTypeMessage = "Dövüş başlamadan hemen önce size bir hatırlatma göndereceğiz. Lütfen uyarı türünü seçin:\n\nNot: Alarm özelliği henüz Beta aşamasındadır. Kesinlikle kaçırmak istemediğiniz dövüşler için telefonunuzun kendi alarmını da kurmanızı tavsiye ederiz.",
    notificationTypeRegular = "Bildirim",
    notificationTypeAlarm = "Alarm",
)
