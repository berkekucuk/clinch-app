import UIKit
import UserNotifications
import ComposeApp
import FirebaseCore
import FirebaseMessaging

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        Messaging.messaging().delegate = self
        UNUserNotificationCenter.current().delegate = self

        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(options: authOptions) { granted, error in
            print("Permission granted: \(granted)")
            if let error = error {
                print("Permission error: \(error.localizedDescription)")
            }
        }
        UserDefaults.standard.set(true, forKey: "has_requested_permission")
        application.registerForRemoteNotifications()
        return true
    }

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        // Hand the APNs token to Firebase so it can issue an FCM token
        Messaging.messaging().apnsToken = deviceToken
    }

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let fcmToken = fcmToken else { return }
        print("FCM Token: \(fcmToken)")
        // Kotlin side (DeviceTokenProvider.ios.kt) reads this key
        UserDefaults.standard.set(fcmToken, forKey: "device_token")
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .list, .sound])
    }

    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        let hasApsAlert: Bool = {
            if let aps = userInfo["aps"] as? [String: Any] {
                return aps["alert"] != nil
            }
            return false
        }()
        
        if !hasApsAlert {
            let title = userInfo["title"] as? String
            let body = userInfo["body"] as? String
            
            if let finalTitle = title, let finalBody = body {
                showNativeNotification(title: finalTitle, body: finalBody, userInfo: userInfo)
            }
        }
        
        completionHandler(.newData)
    }

    private func showNativeNotification(title: String, body: String, userInfo: [AnyHashable: Any]) {
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = .default
        content.userInfo = userInfo
        
        let request = UNNotificationRequest(
            identifier: UUID().uuidString,
            content: content,
            trigger: nil
        )
        
        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("Error showing native notification: \(error.localizedDescription)")
            }
        }
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let userInfo = response.notification.request.content.userInfo
        if let fightId = userInfo["fight_id"] as? String {
            DeepLinkManager.shared.navigateToFight(fightId: fightId)
        } else if let eventId = userInfo["event_id"] as? String {
            DeepLinkManager.shared.navigateToEvent(eventId: eventId)
        }
        
        completionHandler()
    }
}
