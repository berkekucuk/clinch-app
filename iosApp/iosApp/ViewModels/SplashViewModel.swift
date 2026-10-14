import SwiftUI
import ComposeApp

class SplashViewModel: ObservableObject {
    @Published var isVisible: Bool = true

    private let controller = SplashController()

    init() {
        controller.observeLoadingState {
            DispatchQueue.main.async {
                withAnimation(.easeOut(duration: 0.4)) {
                    self.isVisible = false
                }
            }
        }
    }

    deinit {
        controller.dispose()
    }
}
