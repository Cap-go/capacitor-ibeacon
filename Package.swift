// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapgoCapacitorIbeacon",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapgoCapacitorIbeacon",
            targets: ["CapacitorIbeaconPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "CapacitorIbeaconPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/CapacitorIbeaconPlugin"),
        .testTarget(
            name: "CapacitorIbeaconPluginTests",
            dependencies: ["CapacitorIbeaconPlugin"],
            path: "ios/Tests/CapacitorIbeaconPluginTests")
    ]
)
