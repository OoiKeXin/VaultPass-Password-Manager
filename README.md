# VaultPass - Secure Offline Password Manager 🛡️

VaultPass is a premium, offline-first Android password manager designed with privacy and user experience at its core. Built with modern Android development practices, it provides a secure "vault" for all your sensitive credentials without ever letting your data leave the device.

## ✨ Key Features

### 🔐 Security & Privacy
- **Offline-First Architecture**: Your passwords never touch a server. All data is stored locally using an encrypted-ready Room Database.
- **Biometric Authentication**: Instant access with Fingerprint or Face Unlock for both initial login and vault re-entry.
- **Privacy Mode (`FLAG_SECURE`)**: Prevents screenshots/screen recordings and blurs app content in the "Recent Apps" switcher.
- **Clipboard Auto-Clear**: Automatically wipes copied passwords from the clipboard after 30 seconds to prevent accidental leaks.

### 🚀 User Experience
- **Interactive Dashboard**: Real-time category counters (Browser, Mobile, Payment) with dynamic UI highlighting.
- **Quick-Action Search**: Fast filtering with an instant "Clear Search" button.
- **Long-Press to Copy**: Copy passwords directly from the home screen without navigating to details.
- **Smart PIN Entry**: Automatic vault unlocking as soon as the 4th PIN digit is typed.
- **Remember Me**: Remembers your email for faster logins while keeping the master password secure.

### 🛠️ Advanced Tools
- **Password Generator**: Custom rules for length, uppercase, symbols, and numbers with real-time strength evaluation.
- **Vault Organization**: Group credentials by category and add secure notes, recovery questions, and secondary PINs.

## 🏗️ Technical Stack
- **Language**: Java
- **Database**: Room Persistence Library (SQLite)
- **UI Components**: Material Design 3 (M3), ConstraintLayout, BottomSheetDialog
- **Authentication**: Android Biometric API
- **Testing**: JUnit 4 (Local Unit Tests) & AndroidX Test (Instrumented Database Tests)

## 📸 Screenshots
*(Add your screenshots here later)*

## 🚦 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- Android SDK 35+ (API 36 Preview recommended)
- A device/emulator with Biometric support

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/VaultPass.git
   ```
2. Open the project in Android Studio.
3. Sync Project with Gradle Files.
4. Run on your physical device or emulator.

## 🧪 Running Tests
- **Unit Tests**: Right-click `ValidationUtilsTest` or `PasswordUtilsTest` -> Run.
- **Instrumented Tests**: Connect a device and run `DatabaseTest`.

## 📄 License
This project was developed for the **Mobile Application Development** course. 

---
*Developed with ❤️ by Ooi Ke Xin*
