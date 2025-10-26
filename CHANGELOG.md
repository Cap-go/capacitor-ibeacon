# Changelog

All notable changes to this project will be documented in this file. See [commit-and-tag-version](https://github.com/absolute-version/commit-and-tag-version) for commit guidelines.

## [7.0.2](https://github.com/Cap-go/capacitor-ibeacon/compare/7.0.1...7.0.2) (2025-10-26)


### Bug Fixes

* android build ([fb02bb6](https://github.com/Cap-go/capacitor-ibeacon/commit/fb02bb6d8e772814ddd014a0b4cef980d3aa4335))

## 7.0.1 (2025-10-26)

## [0.0.1] - 2025-10-26

### Added

- Initial release of @capgo/capacitor-ibeacon
- Beacon region monitoring (enter/exit detection)
- Beacon ranging (distance measurement)
- iBeacon advertising support (iOS only)
- Location permission management
- Bluetooth state detection
- ARMA filter support for Android
- iOS implementation with CoreLocation
- Android implementation framework (requires AltBeacon library)
- Web platform stub implementation
- TypeScript definitions for all APIs
- Event listeners for beacon detection
- Comprehensive documentation and examples

### Notes

- Android implementation requires AltBeacon library integration
- iOS requires location permissions in Info.plist
- Background monitoring requires "Always" location permission
