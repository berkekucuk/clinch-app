import SwiftUI

struct ContentView: View {
    @StateObject private var splashViewModel = SplashViewModel()

    var body: some View {
        ZStack {
            ComposeView()
                .ignoresSafeArea()

            if splashViewModel.isVisible {
                SplashOverlay()
                    .transition(.opacity)
                    .zIndex(1)
            }
        }
    }
}
