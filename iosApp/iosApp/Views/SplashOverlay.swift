import SwiftUI

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
