# 🎥 Kaze – Talk to Random People
Kaze is a real-time peer-to-peer communication Android app that lets users video chat with random strangers. Built with Kotlin, Jetpack Compose, WebRTC, and Firebase, it offers a smooth and responsive communication experience optimized for mobile networks.

---

## 🚀 Features

- 🎥 **Peer-to-Peer Video Calls & Text Communication**  
  High-quality real-time communication using WebRTC APIs.

- 🔀 **Random User Matching**  
  Queue-based system that matches users randomly and reconnects gracefully on disconnection.

- ⚡ **Firebase Signaling Server**  
  Utilizes Firebase Realtime Database for seamless signaling (offer, answer, ICE candidates).

- 🎨 **Modern UI with Jetpack Compose**  
  Clean, intuitive design featuring camera previews and chat interface.
---

## 🛠️ Tech Stack

| Tech                     | Purpose                                 |
|--------------------------|-----------------------------------------|
| Kotlin                   | Programming language              |
| Jetpack Compose          | Declarative UI                          |
| WebRTC                   | Real-time Video & Text Communication   |
| Firebase Realtime DB     | Signaling Server for WebRTC             |

---

## 📦 Architecture Overview

```plaintext
User A             Firebase (Signaling)             User B
  |                       |                           |
  |--- Offer ------------>|                           |
  |                       |--- Offer ---------------->|
  |                       |<-- Answer ----------------|
  |<-- Answer ------------|                           |
  |--- ICE Candidates --->|                           |
  |                       |---- ICE Candidates ------>|
  |<-- Media Stream ----->|<--- Media Stream -------->|
```
---

### 🧪 Getting Started

1.  **Clone the Repository**
2.  **Open in Android Studio**
3.  **Set Up Firebase**
    * Go to Firebase Console
    * Create a new project.
    * Add an Android app to the project.
    * Download `google-services.json` and place it in the `app/` directory.
    * Enable Realtime Database and set read/write rules (for testing):
        ```json
        {
          "rules": {
            ".read": true,
            ".write": true
          }
        }
        ```
4.  **Build & Run**
    Run on a real device (camera & microphone access required).

### 🖼️ Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/77813710-0cc9-4bd1-b4f6-2dfbf3d5f53f" alt="Screenshot 2" width="30%" style="margin-right: 50px;" style="left: 50px;" />
  &nbsp;&nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/1015d12f-fce6-4eec-b3b4-6d671429dcf1" alt="Screenshot 3" width="30%" style="margin-right: 10px;" style="left: 50px;" />
</p>

