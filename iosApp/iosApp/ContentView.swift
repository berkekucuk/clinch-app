import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.mainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

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

struct SplashOverlay: View {
    var body: some View {
        GeometryReader { geo in
            ZStack {
                Color(red: 26/255, green: 26/255, blue: 26/255)
                    .ignoresSafeArea()

                Image("SplashLogo")
                    .resizable()
                    .scaledToFit()
                    .frame(width: geo.size.width * 0.60, height: geo.size.width * 0.60)
                    .blendMode(.screen)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
        .ignoresSafeArea()
    }
}

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
