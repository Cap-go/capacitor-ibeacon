# Changelog

All notable changes to this project will be documented in this file.

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
