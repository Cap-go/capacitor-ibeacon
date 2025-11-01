# Changelog

All notable changes to this project will be documented in this file. See [commit-and-tag-version](https://github.com/absolute-version/commit-and-tag-version) for commit guidelines.

## [7.0.7](https://github.com/Cap-go/capacitor-ibeacon/compare/7.0.6...7.0.7) (2025-11-01)


### Bug Fixes

* missing implementation ([68071ea](https://github.com/Cap-go/capacitor-ibeacon/commit/68071ea1b9d82361f6b336243df95f39308bb7e9))

## [7.0.6](https://github.com/Cap-go/capacitor-ibeacon/compare/7.0.5...7.0.6) (2025-10-30)


### Bug Fixes

* pluginVersion lint issue ([60712ff](https://github.com/Cap-go/capacitor-ibeacon/commit/60712ffeb0aeb7cdb0f50012c5e84e536f67b89b))

## [7.0.5](https://github.com/Cap-go/capacitor-ibeacon/compare/7.0.4...7.0.5) (2025-10-29)


### Bug Fixes

* CI/CD config ([76d33dc](https://github.com/Cap-go/capacitor-ibeacon/commit/76d33dca51bdd70f33833a642dc488a864241dcd))
* update AI model in build workflow ([03474e0](https://github.com/Cap-go/capacitor-ibeacon/commit/03474e0498673ee0d1f6d459bc702b3b28d722c6))
* update github action ([2c3f9ee](https://github.com/Cap-go/capacitor-ibeacon/commit/2c3f9ee9283db31b30edf8e08579a2bda1ab1981))

## [7.0.4](https://github.com/Cap-go/capacitor-ibeacon/compare/7.0.3...7.0.4) (2025-10-27)


### Bug Fixes

* renovate capacitor dep ([c57bc3c](https://github.com/Cap-go/capacitor-ibeacon/commit/c57bc3c17cbf26b47f1e83ae576ad4417fae00c7))

## [7.0.3](https://github.com/Cap-go/capacitor-ibeacon/compare/7.0.2...7.0.3) (2025-10-27)


### Bug Fixes

* lint ([48e76bd](https://github.com/Cap-go/capacitor-ibeacon/commit/48e76bd7f7d4fabf1941c29dfe0d7ac9d5925428))

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
