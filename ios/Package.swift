// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "STYNO",
    platforms: [
        .iOS(.v16)
    ],
    products: [
        .library(
            name: "STYNO",
            targets: ["STYNO"]
        )
    ],
    dependencies: [
        .package(url: "https://github.com/firebase/firebase-ios-sdk.git", from: "10.24.0"),
        .package(url: "https://github.com/google/GoogleSignIn-iOS.git", from: "7.1.0")
    ],
    targets: [
        .target(
            name: "STYNO",
            dependencies: [
                .product(name: "FirebaseAuth", package: "firebase-ios-sdk"),
                .product(name: "FirebaseFirestore", package: "firebase-ios-sdk"),
                .product(name: "FirebaseStorage", package: "firebase-ios-sdk"),
                .product(name: "GoogleSignIn", package: "GoogleSignIn-iOS")
            ],
            path: "STYNO"
        ),
        .testTarget(
            name: "STYNOTests",
            dependencies: ["STYNO"],
            path: "STYNOTests"
        )
    ]
)
